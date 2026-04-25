namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Alerte système : peut être créée manuellement ou automatiquement.
    /// NiveauUrgence : Faible, Moyen, Élevé, Critique
    /// Etat : Ouverte, EnCours, Resolue, Ignoree
    /// </summary>
    public class Alerte
    {
        public int Id { get; set; }
        public DateTime DateAlerte { get; set; } = DateTime.UtcNow;

        /// <summary>
        /// Niveau d'urgence : Faible, Moyen, Élevé, Critique
        /// </summary>
        public string NiveauUrgence { get; set; } = "Moyen";

        public string Titre { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;

        /// <summary>
        /// Etat : Ouverte, EnCours, Resolue, Ignoree
        /// </summary>
        public string Etat { get; set; } = "Ouverte";

        /// <summary>
        /// Service concerné si l'alerte vient d'une surveillance (nullable)
        /// </summary>
        public int? ServiceSurveilleId { get; set; }
        public ServiceSurveille? ServiceSurveille { get; set; }

        /// <summary>
        /// True si l'alerte a été créée automatiquement par le système
        /// </summary>
        public bool CreeeParSysteme { get; set; } = false;

        // Navigation
        public ICollection<Notification> Notifications { get; set; } = new List<Notification>();
    }
}
