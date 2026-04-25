namespace BloodFlow.MS3.Models
{
    /// <summary>
    /// Journal système : trace tous les événements importants de MS3.
    /// Permet la traçabilité et le diagnostic.
    /// Niveau : Info, Avertissement, Erreur, Critique
    /// </summary>
    public class JournalSysteme
    {
        public int Id { get; set; }
        public DateTime DateEvenement { get; set; } = DateTime.UtcNow;

        /// <summary>
        /// Niveau : Info, Avertissement, Erreur, Critique
        /// </summary>
        public string Niveau { get; set; } = "Info";

        /// <summary>
        /// Source de l'événement : ex "AlerteService", "CollecteController"
        /// </summary>
        public string Source { get; set; } = string.Empty;

        public string Message { get; set; } = string.Empty;
        public string? Details { get; set; }

        /// <summary>
        /// ID de corrélation pour tracer une requête entre services
        /// </summary>
        public string? CorrelationId { get; set; }
    }
}
