namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Appel urgent lancé par un administrateur pour mobiliser des donneurs.
    /// Contient le groupe sanguin cible, la ville, la priorité et le message.
    /// Les notifications sont générées à partir de cet appel.
    /// </summary>
    public class AppelUrgent
    {
        public int Id { get; set; }
        public DateTime DateAppel { get; set; } = DateTime.UtcNow;

        /// <summary>
        /// Groupe sanguin ciblé : A+, A-, B+, B-, AB+, AB-, O+, O-
        /// </summary>
        public string GroupeSanguin { get; set; } = string.Empty;

        public string Ville { get; set; } = string.Empty;
        public string Message { get; set; } = string.Empty;

        /// <summary>
        /// Priorité : Normale, Haute, Critique
        /// </summary>
        public string Priorite { get; set; } = "Haute";

        public bool EstActif { get; set; } = true;

        /// <summary>
        /// Nombre de notifications envoyées suite à cet appel
        /// </summary>
        public int NombreNotificationsEnvoyees { get; set; } = 0;

        public int CreeParAdminId { get; set; }
    }
}
