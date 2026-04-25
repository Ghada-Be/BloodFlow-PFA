namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Bénévole recruté pour participer à une collecte de sang.
    /// Il peut être disponible ou non, et peut être affecté à une collecte.
    /// </summary>
    public class Benevole
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Contact { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;

        /// <summary>
        /// Exemple : "WeekEnd", "JourSemaine", "ToutTemps"
        /// </summary>
        public string Disponibilite { get; set; } = string.Empty;

        // Collecte optionnelle (un bénévole peut ne pas encore être affecté)
        public int? CollecteSangId { get; set; }
        public CollecteSang? CollecteSang { get; set; }
    }
}
