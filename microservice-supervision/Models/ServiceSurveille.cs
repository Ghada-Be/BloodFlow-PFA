namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Représente un service externe surveillé par MS3 (ex: MS1, MS2).
    /// MS3 vérifie périodiquement leur état via leur URL de health check.
    /// </summary>
    public class ServiceSurveille
    {
        public int Id { get; set; }
        public string NomService { get; set; } = string.Empty;
        public string UrlHealthCheck { get; set; } = string.Empty;

        /// <summary>
        /// Etat possible : Disponible, Dégradé, Indisponible
        /// </summary>
        public string Etat { get; set; } = "Inconnu";

        public DateTime? DateDerniereVerification { get; set; }
        public int? DerniereLatenceMs { get; set; }
        public string? MessageEtat { get; set; }

        // Navigation
        public ICollection<Alerte> Alertes { get; set; } = new List<Alerte>();
    }
}
