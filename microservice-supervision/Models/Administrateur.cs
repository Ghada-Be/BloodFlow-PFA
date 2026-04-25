namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Représente un administrateur du système BloodFlow MS3.
    /// Il est lié à un utilisateur du Microservice 1 via UserIdMs1.
    /// </summary>
    public class Administrateur
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public bool Actif { get; set; } = true;

        /// <summary>
        /// Référence vers l'utilisateur du Microservice 1 (authentification)
        /// </summary>
        public string UserIdMs1 { get; set; } = string.Empty;

        public DateTime DateCreation { get; set; } = DateTime.UtcNow;

        // Navigation
        public ICollection<Rapport> Rapports { get; set; } = new List<Rapport>();
    }
}
