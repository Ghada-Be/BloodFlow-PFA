namespace BloodFlow.MS1.Models
{
    /// <summary>
    /// Token temporaire utilisé pour réinitialiser le mot de passe.
    /// Envoyé par email à l'utilisateur (simulé dans ce projet).
    /// </summary>
    public class PasswordResetToken
    {
        public int Id { get; set; }

        // Le token de reset (valeur aléatoire)
        public string Token { get; set; } = string.Empty;

        // Dates
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime ExpiresAt { get; set; }

        // Est-il déjà utilisé ?
        public bool IsUsed { get; set; } = false;

        // FK vers l'utilisateur
        public int UserId { get; set; }
        public User User { get; set; } = null!;

        // Propriété calculée
        public bool IsValid => !IsUsed && DateTime.UtcNow < ExpiresAt;
    }
}
