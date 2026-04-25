using System.ComponentModel.DataAnnotations;

namespace BloodFlow.MS1.DTOs.Auth
{
    // ─────────────────────────────────────────────────────────────────
    // DTO : Connexion
    // ─────────────────────────────────────────────────────────────────
    public class LoginDto
    {
        [Required(ErrorMessage = "L'email est obligatoire")]
        [EmailAddress(ErrorMessage = "Format d'email invalide")]
        public string Email { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le mot de passe est obligatoire")]
        public string Password { get; set; } = string.Empty;
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Inscription
    // ─────────────────────────────────────────────────────────────────
    public class RegisterDto
    {
        [Required(ErrorMessage = "Le prénom est obligatoire")]
        [StringLength(100)]
        public string FirstName { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le nom est obligatoire")]
        [StringLength(100)]
        public string LastName { get; set; } = string.Empty;

        [Required(ErrorMessage = "L'email est obligatoire")]
        [EmailAddress(ErrorMessage = "Format d'email invalide")]
        public string Email { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le mot de passe est obligatoire")]
        [StringLength(100, MinimumLength = 8)]
        public string Password { get; set; } = string.Empty;

        [Compare("Password", ErrorMessage = "Les mots de passe ne correspondent pas")]
        public string ConfirmPassword { get; set; } = string.Empty;

        [Phone]
        public string? PhoneNumber { get; set; }

        public string? Role { get; set; }
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Réponse Login (IMPORTANT)
    // ─────────────────────────────────────────────────────────────────
    public class TokenResponseDto
    {
        public string AccessToken { get; set; } = string.Empty;
        public string RefreshToken { get; set; } = string.Empty;
        public DateTime ExpiresAt { get; set; }
        public string Email { get; set; } = string.Empty;
        public string FullName { get; set; } = string.Empty;
        public List<string> Roles { get; set; } = new();

        // 🔴 NOUVEAU CHAMP (TRÈS IMPORTANT)
        public bool MustChangePassword { get; set; }
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Refresh Token
    // ─────────────────────────────────────────────────────────────────
    public class RefreshTokenRequestDto
    {
        [Required]
        public string RefreshToken { get; set; } = string.Empty;
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Changement de mot de passe
    // ─────────────────────────────────────────────────────────────────
    public class ChangePasswordDto
    {
        [Required]
        public string CurrentPassword { get; set; } = string.Empty;

        [Required]
        [StringLength(100, MinimumLength = 8)]
        public string NewPassword { get; set; } = string.Empty;

        [Compare("NewPassword")]
        public string ConfirmNewPassword { get; set; } = string.Empty;
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Forgot Password
    // ─────────────────────────────────────────────────────────────────
    public class ForgotPasswordDto
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; } = string.Empty;
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Reset Password
    // ─────────────────────────────────────────────────────────────────
    public class ResetPasswordDto
    {
        [Required]
        public string Token { get; set; } = string.Empty;

        [Required]
        [EmailAddress]
        public string Email { get; set; } = string.Empty;

        [Required]
        [StringLength(100, MinimumLength = 8)]
        public string NewPassword { get; set; } = string.Empty;

        [Compare("NewPassword")]
        public string ConfirmNewPassword { get; set; } = string.Empty;
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Logout
    // ─────────────────────────────────────────────────────────────────
    public class LogoutDto
    {
        [Required]
        public string RefreshToken { get; set; } = string.Empty;
    }
}