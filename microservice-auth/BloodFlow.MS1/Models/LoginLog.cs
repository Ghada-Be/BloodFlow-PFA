namespace BloodFlow.MS1.Models
{
    /// <summary>
    /// Journal des tentatives de connexion.
    /// Permet de tracer les accès réussis et échoués pour des raisons de sécurité.
    /// </summary>
    public class LoginLog
    {
        public int Id { get; set; }

        // FK optionnelle (null si l'email n'existe pas)
        public int? UserId { get; set; }
        public User? User { get; set; }

        // L'email utilisé lors de la tentative
        public string Email { get; set; } = string.Empty;

        // Succès ou échec
        public bool IsSuccess { get; set; }

        // Message optionnel (ex: "Invalid password", "Account locked")
        public string? FailureReason { get; set; }

        // Adresse IP du client
        public string? IpAddress { get; set; }

        // Date de la tentative
        public DateTime AttemptedAt { get; set; } = DateTime.UtcNow;
    }
}
