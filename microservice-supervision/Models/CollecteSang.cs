namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Collecte de sang organisée dans un lieu précis.
    /// Peut appartenir à une campagne (nullable).
    /// Statut : Planifiee, EnCours, Terminee, Annulee
    /// </summary>
    public class CollecteSang
    {
        public int Id { get; set; }
        public string Lieu { get; set; } = string.Empty;
        public string Ville { get; set; } = string.Empty;
        public DateTime DateCollecte { get; set; }
        public TimeSpan HeureDebut { get; set; }
        public TimeSpan HeureFin { get; set; }

        /// <summary>
        /// Nombre de poches de sang visées
        /// </summary>
        public int ObjectifPoches { get; set; }

        /// <summary>
        /// Statut : Planifiee, EnCours, Terminee, Annulee
        /// </summary>
        public string Statut { get; set; } = "Planifiee";

        // Campagne optionnelle (une collecte peut être indépendante)
        public int? CampagneId { get; set; }
        public Campagne? Campagne { get; set; }

        // Agent responsable de la collecte
        public int AgentPromoteurId { get; set; }
        public AgentPromoteur? AgentPromoteur { get; set; }

        // Navigation
        public ICollection<Benevole> Benevoles { get; set; } = new List<Benevole>();
    }
}
