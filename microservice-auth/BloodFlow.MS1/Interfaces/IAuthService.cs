using BloodFlow.MS1.DTOs.Auth;

namespace BloodFlow.MS1.Interfaces
{
    /// <summary>
    /// Interface du service d'authentification.
    /// Définit le contrat que doit respecter AuthService.
    /// Injectée dans le contrôleur via l'injection de dépendances.
    /// </summary>
    public interface IAuthService
    {
        /// <summary>Inscription d'un nouvel utilisateur.</summary>
        Task<(bool Success, string Message)> RegisterAsync(RegisterDto dto, string? ipAddress);

        /// <summary>Connexion avec email + mot de passe.</summary>
        Task<(bool Success, string Message, TokenResponseDto? Token)> LoginAsync(LoginDto dto, string? ipAddress);

        /// <summary>Renouvellement de l'access token via refresh token.</summary>
        Task<(bool Success, string Message, TokenResponseDto? Token)> RefreshTokenAsync(string refreshToken, string? ipAddress);

        /// <summary>Déconnexion : révocation du refresh token.</summary>
        Task<(bool Success, string Message)> LogoutAsync(string refreshToken, string? ipAddress);

        /// <summary>Changement de mot de passe (utilisateur connecté).</summary>
        Task<(bool Success, string Message)> ChangePasswordAsync(int userId, ChangePasswordDto dto);

        /// <summary>Demande de réinitialisation du mot de passe.</summary>
        Task<(bool Success, string Message, string? ResetToken)> ForgotPasswordAsync(ForgotPasswordDto dto);

        /// <summary>Réinitialisation effective du mot de passe avec le token.</summary>
        Task<(bool Success, string Message)> ResetPasswordAsync(ResetPasswordDto dto);
    }
}
