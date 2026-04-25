using BloodFlow.MS1.Data;
using BloodFlow.MS1.DTOs.Auth;
using BloodFlow.MS1.Helpers;
using BloodFlow.MS1.Interfaces;
using BloodFlow.MS1.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS1.Services
{
    /// <summary>
    /// Service d'authentification : inscription, connexion, JWT, refresh token,
    /// déconnexion, changement de mot de passe, reset password.
    ///
    /// Toute la logique métier est ici, le contrôleur reste mince.
    /// </summary>
    public class AuthService : IAuthService
    {
        private readonly ApplicationDbContext _db;
        private readonly JwtHelper _jwtHelper;
        private readonly IConfiguration _config;

        // Nombre maximum de tentatives avant blocage temporaire
        private const int MaxFailedAttempts = 5;
        // Durée de blocage en minutes
        private const int LockoutDurationMinutes = 15;

        public AuthService(ApplicationDbContext db, JwtHelper jwtHelper, IConfiguration config)
        {
            _db = db;
            _jwtHelper = jwtHelper;
            _config = config;
        }

        // ─────────────────────────────────────────────────────────────────
        // INSCRIPTION
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> RegisterAsync(RegisterDto dto, string? ipAddress)
        {
            // 1. Vérifier unicité de l'email
            if (await _db.Users.AnyAsync(u => u.Email == dto.Email.ToLower()))
                return (false, "Un compte avec cet email existe déjà.");

            // 2. Déterminer le rôle (défaut : Donor)
            var roleName = string.IsNullOrWhiteSpace(dto.Role) ? "Donor" : dto.Role;
            var role = await _db.Roles.FirstOrDefaultAsync(r => r.Name == roleName);
            if (role == null)
                return (false, $"Le rôle '{roleName}' n'existe pas.");

            // 3. Créer l'utilisateur
            var user = new User
            {
                FirstName = dto.FirstName.Trim(),
                LastName = dto.LastName.Trim(),
                Email = dto.Email.ToLower().Trim(),
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password, 12),
                PhoneNumber = dto.PhoneNumber,
                IsActive = true,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow,
                MustChangePassword = false
            };

            _db.Users.Add(user);
            await _db.SaveChangesAsync();

            // 4. Attribuer le rôle
            _db.UserRoles.Add(new UserRole
            {
                UserId = user.Id,
                RoleId = role.Id,
                AssignedAt = DateTime.UtcNow
            });
            await _db.SaveChangesAsync();

            return (true, "Inscription réussie.");
        }

        // ─────────────────────────────────────────────────────────────────
        // CONNEXION
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message, TokenResponseDto? Token)> LoginAsync(LoginDto dto, string? ipAddress)
        {
            // 1. Trouver l'utilisateur par email
            var user = await _db.Users
                .Include(u => u.UserRoles)
                    .ThenInclude(ur => ur.Role)
                .FirstOrDefaultAsync(u => u.Email == dto.Email.ToLower());

            // 2. Si email non trouvé → log + refus générique
            if (user == null)
            {
                await LogLoginAttemptAsync(null, dto.Email, false, "Email introuvable", ipAddress);
                return (false, "Email ou mot de passe incorrect.", null);
            }

            // 3. Vérifier si le compte est actif
            if (!user.IsActive)
            {
                await LogLoginAttemptAsync(user.Id, dto.Email, false, "Compte désactivé", ipAddress);
                return (false, "Ce compte est désactivé. Contactez l'administrateur.", null);
            }

            // 4. Vérifier si le compte est verrouillé
            if (user.LockoutEnd.HasValue && user.LockoutEnd > DateTime.UtcNow)
            {
                var remainingMinutes = (int)(user.LockoutEnd.Value - DateTime.UtcNow).TotalMinutes;
                await LogLoginAttemptAsync(user.Id, dto.Email, false, "Compte verrouillé", ipAddress);
                return (false, $"Compte verrouillé. Réessayez dans {remainingMinutes} minutes.", null);
            }

            // 5. Vérifier le mot de passe
            if (!BCrypt.Net.BCrypt.Verify(dto.Password, user.PasswordHash))
            {
                user.FailedLoginAttempts++;

                if (user.FailedLoginAttempts >= MaxFailedAttempts)
                {
                    user.LockoutEnd = DateTime.UtcNow.AddMinutes(LockoutDurationMinutes);
                    await LogLoginAttemptAsync(user.Id, dto.Email, false, "Compte verrouillé après trop de tentatives", ipAddress);
                    await _db.SaveChangesAsync();
                    return (false, $"Trop de tentatives. Compte verrouillé pour {LockoutDurationMinutes} minutes.", null);
                }

                await LogLoginAttemptAsync(user.Id, dto.Email, false, "Mot de passe incorrect", ipAddress);
                await _db.SaveChangesAsync();
                return (false, "Email ou mot de passe incorrect.", null);
            }

            // 6. Connexion réussie → réinitialiser le compteur
            user.FailedLoginAttempts = 0;
            user.LockoutEnd = null;

            // 7. Récupérer les rôles
            var roles = user.UserRoles.Select(ur => ur.Role.Name).ToList();

            // 8. Générer les tokens
            var accessToken = _jwtHelper.GenerateAccessToken(user, roles);
            var refreshToken = _jwtHelper.GenerateRefreshToken();

            // 9. Stocker le refresh token en base
            var refreshTokenEntity = new RefreshToken
            {
                Token = refreshToken,
                UserId = user.Id,
                CreatedAt = DateTime.UtcNow,
                ExpiresAt = _jwtHelper.GetRefreshTokenExpiry(),
                CreatedByIp = ipAddress
            };

            _db.RefreshTokens.Add(refreshTokenEntity);

            // 10. Log de connexion réussie
            await LogLoginAttemptAsync(user.Id, dto.Email, true, null, ipAddress);

            await _db.SaveChangesAsync();

            // 11. Construire la réponse
            var response = new TokenResponseDto
            {
                AccessToken = accessToken,
                RefreshToken = refreshToken,
                ExpiresAt = _jwtHelper.GetAccessTokenExpiry(),
                Email = user.Email,
                FullName = $"{user.FirstName} {user.LastName}",
                Roles = roles,
                MustChangePassword = user.MustChangePassword
            };

            return (true, "Connexion réussie.", response);
        }

        // ─────────────────────────────────────────────────────────────────
        // REFRESH TOKEN
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message, TokenResponseDto? Token)> RefreshTokenAsync(string refreshToken, string? ipAddress)
        {
            // 1. Chercher le token en base
            var storedToken = await _db.RefreshTokens
                .Include(rt => rt.User)
                    .ThenInclude(u => u.UserRoles)
                        .ThenInclude(ur => ur.Role)
                .FirstOrDefaultAsync(rt => rt.Token == refreshToken);

            if (storedToken == null)
                return (false, "Refresh token invalide.", null);

            if (!storedToken.IsActive)
                return (false, "Refresh token expiré ou révoqué.", null);

            if (!storedToken.User.IsActive)
                return (false, "Ce compte est désactivé.", null);

            // 2. Révoquer l'ancien refresh token
            storedToken.IsRevoked = true;
            storedToken.RevokedAt = DateTime.UtcNow;
            storedToken.RevokedByIp = ipAddress;

            // 3. Générer de nouveaux tokens
            var roles = storedToken.User.UserRoles.Select(ur => ur.Role.Name).ToList();
            var newAccess = _jwtHelper.GenerateAccessToken(storedToken.User, roles);
            var newRefresh = _jwtHelper.GenerateRefreshToken();

            _db.RefreshTokens.Add(new RefreshToken
            {
                Token = newRefresh,
                UserId = storedToken.UserId,
                CreatedAt = DateTime.UtcNow,
                ExpiresAt = _jwtHelper.GetRefreshTokenExpiry(),
                CreatedByIp = ipAddress
            });

            await _db.SaveChangesAsync();

            var response = new TokenResponseDto
            {
                AccessToken = newAccess,
                RefreshToken = newRefresh,
                ExpiresAt = _jwtHelper.GetAccessTokenExpiry(),
                Email = storedToken.User.Email,
                FullName = $"{storedToken.User.FirstName} {storedToken.User.LastName}",
                Roles = roles,
                MustChangePassword = storedToken.User.MustChangePassword
            };

            return (true, "Token renouvelé.", response);
        }

        // ─────────────────────────────────────────────────────────────────
        // DÉCONNEXION
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> LogoutAsync(string refreshToken, string? ipAddress)
        {
            var storedToken = await _db.RefreshTokens
                .FirstOrDefaultAsync(rt => rt.Token == refreshToken);

            if (storedToken == null || !storedToken.IsActive)
                return (false, "Refresh token invalide ou déjà révoqué.");

            storedToken.IsRevoked = true;
            storedToken.RevokedAt = DateTime.UtcNow;
            storedToken.RevokedByIp = ipAddress;

            await _db.SaveChangesAsync();
            return (true, "Déconnexion réussie.");
        }

        // ─────────────────────────────────────────────────────────────────
        // CHANGEMENT DE MOT DE PASSE
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> ChangePasswordAsync(int userId, ChangePasswordDto dto)
        {
            var user = await _db.Users.FindAsync(userId);
            if (user == null)
                return (false, "Utilisateur non trouvé.");

            // Vérifier l'ancien mot de passe
            if (!BCrypt.Net.BCrypt.Verify(dto.CurrentPassword, user.PasswordHash))
                return (false, "Mot de passe actuel incorrect.");

            // Hacher et sauvegarder le nouveau mot de passe
            user.PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.NewPassword, 12);
            user.UpdatedAt = DateTime.UtcNow;

            // Très important : si le mot de passe a été changé, il n'est plus temporaire
            user.MustChangePassword = false;

            // Révoquer tous les refresh tokens
            var userTokens = await _db.RefreshTokens
                .Where(rt => rt.UserId == userId && !rt.IsRevoked)
                .ToListAsync();

            foreach (var token in userTokens)
            {
                token.IsRevoked = true;
                token.RevokedAt = DateTime.UtcNow;
            }

            await _db.SaveChangesAsync();
            return (true, "Mot de passe modifié avec succès.");
        }

        // ─────────────────────────────────────────────────────────────────
        // MOT DE PASSE OUBLIÉ
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message, string? ResetToken)> ForgotPasswordAsync(ForgotPasswordDto dto)
        {
            var user = await _db.Users
                .FirstOrDefaultAsync(u => u.Email == dto.Email.ToLower());

            if (user == null)
                return (true, "Si cet email existe, un lien de réinitialisation a été envoyé.", null);

            var oldTokens = await _db.PasswordResetTokens
                .Where(t => t.UserId == user.Id && !t.IsUsed)
                .ToListAsync();

            foreach (var t in oldTokens)
                t.IsUsed = true;

            var resetToken = Guid.NewGuid().ToString("N") + Guid.NewGuid().ToString("N");

            _db.PasswordResetTokens.Add(new PasswordResetToken
            {
                Token = resetToken,
                UserId = user.Id,
                CreatedAt = DateTime.UtcNow,
                ExpiresAt = DateTime.UtcNow.AddHours(1)
            });

            await _db.SaveChangesAsync();

            return (true, "Token de réinitialisation généré.", resetToken);
        }

        // ─────────────────────────────────────────────────────────────────
        // RESET MOT DE PASSE
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> ResetPasswordAsync(ResetPasswordDto dto)
        {
            var resetToken = await _db.PasswordResetTokens
                .Include(t => t.User)
                .FirstOrDefaultAsync(t =>
                    t.Token == dto.Token &&
                    t.User.Email == dto.Email.ToLower() &&
                    !t.IsUsed &&
                    t.ExpiresAt > DateTime.UtcNow);

            if (resetToken == null)
                return (false, "Token de réinitialisation invalide ou expiré.");

            // Modifier le mot de passe
            resetToken.User.PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.NewPassword, 12);
            resetToken.User.UpdatedAt = DateTime.UtcNow;

            // Comme pour un changement de mot de passe, on retire l'obligation
            resetToken.User.MustChangePassword = false;

            // Marquer le token comme utilisé
            resetToken.IsUsed = true;

            await _db.SaveChangesAsync();
            return (true, "Mot de passe réinitialisé avec succès.");
        }

        // ─────────────────────────────────────────────────────────────────
        // MÉTHODE PRIVÉE : Journalisation des connexions
        // ─────────────────────────────────────────────────────────────────

        private async Task LogLoginAttemptAsync(int? userId, string email, bool isSuccess,
                                                string? failureReason, string? ipAddress)
        {
            _db.LoginLogs.Add(new LoginLog
            {
                UserId = userId,
                Email = email,
                IsSuccess = isSuccess,
                FailureReason = failureReason,
                IpAddress = ipAddress,
                AttemptedAt = DateTime.UtcNow
            });

            await _db.SaveChangesAsync();
        }
    }
}