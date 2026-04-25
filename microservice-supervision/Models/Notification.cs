namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Notification envoyée à un destinataire.
    /// Type : Alerte, Campagne, AppelUrgent, Information
    /// Canal : Email, SMS, App, System
    /// StatutEnvoi : EnAttente, Envoyee, Echouee
    /// </summary>
    public class Notification
    {
        public int Id { get; set; }
        public DateTime DateEnvoi { get; set; } = DateTime.UtcNow;
        public string Message { get; set; } = string.Empty;

        /// <summary>
        /// Type : Alerte, Campagne, AppelUrgent, Information
        /// </summary>
        public string Type { get; set; } = "Information";

        /// <summary>
        /// Canal : Email, SMS, App, System
        /// </summary>
        public string Canal { get; set; } = "Email";

        /// <summary>
        /// Statut : EnAttente, Envoyee, Echouee
        /// </summary>
        public string StatutEnvoi { get; set; } = "EnAttente";

        /// <summary>
        /// Destinataire : email, numéro de téléphone, ou identifiant
        /// </summary>
        public string Destinataire { get; set; } = string.Empty;

        // Liens optionnels vers une alerte ou une campagne
        public int? AlerteId { get; set; }
        public Alerte? Alerte { get; set; }

        public int? CampagneId { get; set; }
        public Campagne? Campagne { get; set; }
    }
}
