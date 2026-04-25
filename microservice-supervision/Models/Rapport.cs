namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Rapport généré par un administrateur.
    /// Type : Global, Campagne, Collecte, Alertes, Système
    /// Format : JSON, CSV, Texte
    /// </summary>
    public class Rapport
    {
        public int Id { get; set; }
        public DateTime DateGeneration { get; set; } = DateTime.UtcNow;

        /// <summary>
        /// Type de rapport : Global, Campagne, Collecte, Alertes, Système
        /// </summary>
        public string Type { get; set; } = string.Empty;

        public string Contenu { get; set; } = string.Empty;

        /// <summary>
        /// Format de sortie : JSON, CSV, Texte
        /// </summary>
        public string Format { get; set; } = "JSON";

        // Clé étrangère vers Administrateur
        public int CreeParAdminId { get; set; }
        public Administrateur? CreeParAdmin { get; set; }
    }
}
