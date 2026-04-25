namespace BloodFlow.MS1.Models
{
    /// <summary>
    /// Stocke les refresh tokens en base de données.
    /// Permet de renouveler l'access token sans redemander la connexion.
    /// Le refresh token est une chaîne aléatoire longue durée stockée côté serveur.
    /// </summary>
    public class RefreshToken
    {
        public int Id { get; set; }

        // Le token lui-même (valeur aléatoire sécurisée)
        public string Token { get; set; } = string.Empty;

        // Dates de vie du token
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime ExpiresAt { get; set; }

        // Statut : utilisé ou révoqué ?
        public bool IsRevoked { get; set; } = false;
        public DateTime? RevokedAt { get; set; }

        // Informations de traçabilité
        public string? CreatedByIp { get; set; }
        public string? RevokedByIp { get; set; }

        // FK vers l'utilisateur propriétaire
        public int UserId { get; set; }
        public User User { get; set; } = null!;

        // Propriété calculée : est-ce que le token est encore actif ?
        public bool IsActive => !IsRevoked && DateTime.UtcNow < ExpiresAt;
    }
}
