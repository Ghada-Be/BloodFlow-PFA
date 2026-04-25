namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Campagne de sensibilisation au don de sang.
    /// Statut : Brouillon, Planifiee, Active, Terminee, Annulee
    /// </summary>
    public class Campagne
    {
        public int Id { get; set; }
        public string Titre { get; set; } = string.Empty;
        public string Objectif { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public DateTime DateDebut { get; set; }
        public DateTime DateFin { get; set; }

        /// <summary>
        /// Statut : Brouillon, Planifiee, Active, Terminee, Annulee
        /// </summary>
        public string Statut { get; set; } = "Brouillon";

        // Clé étrangère vers AgentPromoteur responsable
        public int AgentPromoteurId { get; set; }
        public AgentPromoteur? AgentPromoteur { get; set; }

        // Navigation
        public ICollection<CollecteSang> Collectes { get; set; } = new List<CollecteSang>();
        public ICollection<Notification> Notifications { get; set; } = new List<Notification>();
    }
}
