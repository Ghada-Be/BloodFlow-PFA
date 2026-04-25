namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Agent promoteur : organise les campagnes et collectes de sang.
    /// Lié à MS1 via UserIdMs1.
    /// </summary>
    public class AgentPromoteur
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public bool Actif { get; set; } = true;
        public string UserIdMs1 { get; set; } = string.Empty;
        public DateTime DateCreation { get; set; } = DateTime.UtcNow;

        // Navigation
        public ICollection<Campagne> Campagnes { get; set; } = new List<Campagne>();
        public ICollection<CollecteSang> Collectes { get; set; } = new List<CollecteSang>();
    }
}
