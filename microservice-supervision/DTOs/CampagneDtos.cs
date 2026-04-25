namespace BloodFlow.MS3.DTOs
{
    public class CampagneDto
    {
        public int Id { get; set; }
        public string Titre { get; set; } = string.Empty;
        public string Objectif { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public DateTime DateDebut { get; set; }
        public DateTime DateFin { get; set; }
        public string Statut { get; set; } = string.Empty;
        public int AgentPromoteurId { get; set; }
        public string NomAgent { get; set; } = string.Empty;
        public int NombreCollectes { get; set; }
    }

    public class CampagneCreateDto
    {
        public string Titre { get; set; } = string.Empty;
        public string Objectif { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public DateTime DateDebut { get; set; }
        public DateTime DateFin { get; set; }
        public int AgentPromoteurId { get; set; }
    }

    public class CampagneUpdateDto
    {
        public string Titre { get; set; } = string.Empty;
        public string Objectif { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public DateTime DateDebut { get; set; }
        public DateTime DateFin { get; set; }
        public string Statut { get; set; } = string.Empty;
    }
}
