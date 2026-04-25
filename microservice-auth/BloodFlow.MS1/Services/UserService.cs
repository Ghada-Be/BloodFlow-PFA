using BloodFlow.AuthService.Dtos;
using BloodFlow.MS1.Data;
using BloodFlow.MS1.DTOs.Users;
using BloodFlow.MS1.Interfaces;
using BloodFlow.MS1.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS1.Services
{
    /// <summary>
    /// Service de gestion des utilisateurs.
    /// Couvre : consultation, modification profil, gestion des rôles, activation/désactivation,
    /// et création des comptes staff par l'administrateur.
    /// </summary>
    public class UserService : IUserService
    {
        private readonly ApplicationDbContext _db;
        private readonly IPasswordHasher<User> _passwordHasher;

        public UserService(ApplicationDbContext db, IPasswordHasher<User> passwordHasher)
        {
            _db = db;
            _passwordHasher = passwordHasher;
        }

        // ─────────────────────────────────────────────────────────────────
        // CRÉER UN COMPTE STAFF (Admin)
        // ─────────────────────────────────────────────────────────────────

        public async Task<ServiceResult<UserDto>> CreateStaffAsync(CreateStaffUserDto dto)
        {
            var existingUser = await _db.Users
                .FirstOrDefaultAsync(u => u.Email == dto.Email);

            if (existingUser != null)
            {
                return ServiceResult<UserDto>.Failure("Cet email existe déjà.");
            }

            var role = await _db.Roles
                .FirstOrDefaultAsync(r => r.Name == dto.RoleName);

            if (role == null)
            {
                return ServiceResult<UserDto>.Failure("Rôle introuvable.");
            }

            var user = new User
            {
                FirstName = dto.FirstName,
                LastName = dto.LastName,
                Email = dto.Email,
                PhoneNumber = dto.PhoneNumber,
                IsActive = true,
                IsEmailVerified = true,
                FailedLoginAttempts = 0,
                LockoutEnd = null,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow,
                MustChangePassword = true
            };

            user.PasswordHash = _passwordHasher.HashPassword(user, dto.TemporaryPassword);

            _db.Users.Add(user);
            await _db.SaveChangesAsync();

            var userRole = new UserRole
            {
                UserId = user.Id,
                RoleId = role.Id,
                AssignedAt = DateTime.UtcNow
            };

            _db.UserRoles.Add(userRole);
            await _db.SaveChangesAsync();

            var userDto = new UserDto
            {
                Id = user.Id,
                FirstName = user.FirstName,
                LastName = user.LastName,
                Email = user.Email,
                PhoneNumber = user.PhoneNumber,
                IsActive = user.IsActive,
                MustChangePassword = user.MustChangePassword,
                Role = role.Name
            };

            return ServiceResult<UserDto>.Success(userDto);
        }

        // ─────────────────────────────────────────────────────────────────
        // LISTE DES UTILISATEURS (Admin seulement)
        // ─────────────────────────────────────────────────────────────────

        public async Task<PagedResult<UserDto>> GetAllUsersAsync(PaginationParams pagination)
        {
            var query = _db.Users
                .Include(u => u.UserRoles)
                    .ThenInclude(ur => ur.Role)
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(pagination.Search))
            {
                var search = pagination.Search.ToLower();
                query = query.Where(u =>
                    u.Email.ToLower().Contains(search) ||
                    u.FirstName.ToLower().Contains(search) ||
                    u.LastName.ToLower().Contains(search));
            }

            var totalCount = await query.CountAsync();

            var users = await query
                .OrderBy(u => u.LastName)
                .Skip((pagination.Page - 1) * pagination.PageSize)
                .Take(pagination.PageSize)
                .ToListAsync();

            return new PagedResult<UserDto>
            {
                Data = users.Select(MapToDto).ToList(),
                TotalCount = totalCount,
                Page = pagination.Page,
                PageSize = pagination.PageSize
            };
        }

        // ─────────────────────────────────────────────────────────────────
        // RÉCUPÉRER UN UTILISATEUR PAR ID
        // ─────────────────────────────────────────────────────────────────

        public async Task<UserDto?> GetUserByIdAsync(int userId)
        {
            var user = await _db.Users
                .Include(u => u.UserRoles)
                    .ThenInclude(ur => ur.Role)
                .FirstOrDefaultAsync(u => u.Id == userId);

            return user == null ? null : MapToDto(user);
        }

        // ─────────────────────────────────────────────────────────────────
        // RÉCUPÉRER LE PROFIL COURANT
        // ─────────────────────────────────────────────────────────────────

        public async Task<UserDto?> GetCurrentUserAsync(int userId)
            => await GetUserByIdAsync(userId);

        // ─────────────────────────────────────────────────────────────────
        // METTRE À JOUR LE PROFIL
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> UpdateProfileAsync(int userId, UpdateProfileDto dto)
        {
            var user = await _db.Users.FindAsync(userId);
            if (user == null)
                return (false, "Utilisateur non trouvé.");

            user.FirstName = dto.FirstName.Trim();
            user.LastName = dto.LastName.Trim();
            user.PhoneNumber = dto.PhoneNumber?.Trim();
            user.UpdatedAt = DateTime.UtcNow;

            await _db.SaveChangesAsync();
            return (true, "Profil mis à jour avec succès.");
        }

        // ─────────────────────────────────────────────────────────────────
        // ACTIVER / DÉSACTIVER UN COMPTE (Admin)
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> SetUserActiveStatusAsync(int userId, bool isActive)
        {
            var user = await _db.Users.FindAsync(userId);
            if (user == null)
                return (false, "Utilisateur non trouvé.");

            if (!isActive)
            {
                var isAdmin = await _db.UserRoles
                    .Include(ur => ur.Role)
                    .AnyAsync(ur => ur.UserId == userId && ur.Role.Name == "Admin");

                if (isAdmin)
                {
                    var adminCount = await _db.UserRoles
                        .Include(ur => ur.Role)
                        .Where(ur => ur.Role.Name == "Admin")
                        .Select(ur => ur.UserId)
                        .Distinct()
                        .CountAsync();

                    if (adminCount <= 1)
                        return (false, "Impossible de désactiver le seul compte administrateur.");
                }
            }

            user.IsActive = isActive;
            user.UpdatedAt = DateTime.UtcNow;

            await _db.SaveChangesAsync();
            return (true, isActive ? "Compte activé." : "Compte désactivé.");
        }

        // ─────────────────────────────────────────────────────────────────
        // ATTRIBUER UN RÔLE (Admin)
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> AssignRoleAsync(int userId, AssignRoleDto dto)
        {
            var user = await _db.Users
                .Include(u => u.UserRoles)
                .FirstOrDefaultAsync(u => u.Id == userId);

            if (user == null)
                return (false, "Utilisateur non trouvé.");

            var role = await _db.Roles.FirstOrDefaultAsync(r => r.Name == dto.RoleName);
            if (role == null)
                return (false, $"Le rôle '{dto.RoleName}' n'existe pas.");

            if (user.UserRoles.Any(ur => ur.RoleId == role.Id))
                return (false, $"L'utilisateur a déjà le rôle '{dto.RoleName}'.");

            _db.UserRoles.Add(new UserRole
            {
                UserId = userId,
                RoleId = role.Id,
                AssignedAt = DateTime.UtcNow
            });

            await _db.SaveChangesAsync();
            return (true, $"Rôle '{dto.RoleName}' attribué avec succès.");
        }

        // ─────────────────────────────────────────────────────────────────
        // RÉVOQUER UN RÔLE (Admin)
        // ─────────────────────────────────────────────────────────────────

        public async Task<(bool Success, string Message)> RevokeRoleAsync(int userId, string roleName)
        {
            var userRole = await _db.UserRoles
                .Include(ur => ur.Role)
                .FirstOrDefaultAsync(ur => ur.UserId == userId && ur.Role.Name == roleName);

            if (userRole == null)
                return (false, $"L'utilisateur n'a pas le rôle '{roleName}'.");

            _db.UserRoles.Remove(userRole);
            await _db.SaveChangesAsync();
            return (true, $"Rôle '{roleName}' révoqué avec succès.");
        }

        // ─────────────────────────────────────────────────────────────────
        // HELPER PRIVÉ : Mapper User → UserDto
        // ─────────────────────────────────────────────────────────────────

        private static UserDto MapToDto(User user) => new()
        {
            Id = user.Id,
            FirstName = user.FirstName,
            LastName = user.LastName,
            Email = user.Email,
            PhoneNumber = user.PhoneNumber,
            IsActive = user.IsActive,
            IsEmailVerified = user.IsEmailVerified,
            CreatedAt = user.CreatedAt,
            Roles = user.UserRoles.Select(ur => ur.Role.Name).ToList()
        };
    }
}