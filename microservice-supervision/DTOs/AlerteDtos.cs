// ============================================================
// DTOs/AlerteDtos.cs
// ============================================================
namespace BloodFlow.MS3.DTOs
{
    public class AlerteDto
    {
        public int Id { get; set; }
        public DateTime DateAlerte { get; set; }
        public string NiveauUrgence { get; set; } = string.Empty;
        public string Titre { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string Etat { get; set; } = string.Empty;
        public int? ServiceSurveilleId { get; set; }
        public string? NomService { get; set; }
        public bool CreeeParSysteme { get; set; }
    }

    public class AlerteCreateDto
    {
        public string NiveauUrgence { get; set; } = string.Empty;
        public string Titre { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public int? ServiceSurveilleId { get; set; }
    }

    public class AlerteUpdateEtatDto
    {
        public string Etat { get; set; } = string.Empty;
    }
}
