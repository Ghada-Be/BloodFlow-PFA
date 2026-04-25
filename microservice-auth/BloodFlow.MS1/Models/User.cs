namespace BloodFlow.MS1.Models
{
    /// <summary>
    /// Entité principale représentant un utilisateur du système BloodFlow.
    /// Tous les acteurs (Admin, Donor, Doctor, etc.) sont des User avec un rôle.
    /// </summary>
    public class User
    {
        public int Id { get; set; }

        // Informations de base
        public string FirstName { get; set; } = string.Empty;
        public string LastName { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string PasswordHash { get; set; } = string.Empty;
        public string? PhoneNumber { get; set; }

        // Statut du compte
        public bool IsActive { get; set; } = true;
        public bool IsEmailVerified { get; set; } = false;

        // 🔴 NOUVEAU (TRÈS IMPORTANT)
        // Indique si l'utilisateur doit changer son mot de passe (mot de passe temporaire)
        public bool MustChangePassword { get; set; } = false;

        // Sécurité : tentatives de connexion
        public int FailedLoginAttempts { get; set; } = 0;
        public DateTime? LockoutEnd { get; set; }

        // Métadonnées
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime? UpdatedAt { get; set; }

        // Navigation : un utilisateur peut avoir plusieurs rôles
        public ICollection<UserRole> UserRoles { get; set; } = new List<UserRole>();

        // Navigation : les refresh tokens de cet utilisateur
        public ICollection<RefreshToken> RefreshTokens { get; set; } = new List<RefreshToken>();

        // Navigation : les logs de connexion
        public ICollection<LoginLog> LoginLogs { get; set; } = new List<LoginLog>();

        // Navigation : les tokens de reset de mot de passe
        public ICollection<PasswordResetToken> PasswordResetTokens { get; set; } = new List<PasswordResetToken>();
    }
}