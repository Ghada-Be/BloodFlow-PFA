namespace BloodFlow.MS3.DTOs
{
    // ─── CollecteSang ─────────────────────────────────────────────────────────
    public class CollecteSangDto
    {
        public int Id { get; set; }
        public string Lieu { get; set; } = string.Empty;
        public string Ville { get; set; } = string.Empty;
        public DateTime DateCollecte { get; set; }
        public string HeureDebut { get; set; } = string.Empty;
        public string HeureFin { get; set; } = string.Empty;
        public int ObjectifPoches { get; set; }
        public string Statut { get; set; } = string.Empty;
        public int? CampagneId { get; set; }
        public string? TitreCampagne { get; set; }
        public int AgentPromoteurId { get; set; }
        public string NomAgent { get; set; } = string.Empty;
        public int NombreBenevoles { get; set; }
    }

    public class CollecteSangCreateDto
    {
        public string Lieu { get; set; } = string.Empty;
        public string Ville { get; set; } = string.Empty;
        public DateTime DateCollecte { get; set; }
        public TimeSpan HeureDebut { get; set; }
        public TimeSpan HeureFin { get; set; }
        public int ObjectifPoches { get; set; }
        public int? CampagneId { get; set; }
        public int AgentPromoteurId { get; set; }
    }

    public class CollecteSangUpdateDto
    {
        public string Lieu { get; set; } = string.Empty;
        public string Ville { get; set; } = string.Empty;
        public DateTime DateCollecte { get; set; }
        public TimeSpan HeureDebut { get; set; }
        public TimeSpan HeureFin { get; set; }
        public int ObjectifPoches { get; set; }
        public string Statut { get; set; } = string.Empty;
    }

    // ─── Benevole ──────────────────────────────────────────────────────────────
    public class BenevoleDto
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Contact { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string Disponibilite { get; set; } = string.Empty;
        public int? CollecteSangId { get; set; }
        public string? LieuCollecte { get; set; }
    }

    public class BenevoleCreateDto
    {
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Contact { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string Disponibilite { get; set; } = string.Empty;
        public int? CollecteSangId { get; set; }
    }

    // ─── Rapport ───────────────────────────────────────────────────────────────
    public class RapportDto
    {
        public int Id { get; set; }
        public DateTime DateGeneration { get; set; }
        public string Type { get; set; } = string.Empty;
        public string Contenu { get; set; } = string.Empty;
        public string Format { get; set; } = string.Empty;
        public int CreeParAdminId { get; set; }
        public string NomAdmin { get; set; } = string.Empty;
    }

    public class RapportGenerateDto
    {
        /// <summary>
        /// Type : Global, Campagne, Collecte, Alertes, Système
        /// </summary>
        public string Type { get; set; } = string.Empty;

        /// <summary>
        /// Format : JSON, CSV, Texte
        /// </summary>
        public string Format { get; set; } = "JSON";

        public int CreeParAdminId { get; set; }
    }

    // ─── AppelUrgent ───────────────────────────────────────────────────────────
    public class AppelUrgentDto
    {
        public int Id { get; set; }
        public DateTime DateAppel { get; set; }
        public string GroupeSanguin { get; set; } = string.Empty;
        public string Ville { get; set; } = string.Empty;
        public string Message { get; set; } = string.Empty;
        public string Priorite { get; set; } = string.Empty;
        public bool EstActif { get; set; }
        public int NombreNotificationsEnvoyees { get; set; }
    }

    public class AppelUrgentCreateDto
    {
        public string GroupeSanguin { get; set; } = string.Empty;
        public string Ville { get; set; } = string.Empty;
        public string Message { get; set; } = string.Empty;
        public string Priorite { get; set; } = "Haute";
        public int CreeParAdminId { get; set; }
    }

    // ─── ServiceSurveille ──────────────────────────────────────────────────────
    public class ServiceSurveilleDto
    {
        public int Id { get; set; }
        public string NomService { get; set; } = string.Empty;
        public string UrlHealthCheck { get; set; } = string.Empty;
        public string Etat { get; set; } = string.Empty;
        public DateTime? DateDerniereVerification { get; set; }
        public int? DerniereLatenceMs { get; set; }
        public string? MessageEtat { get; set; }
    }

    public class ServiceSurveilleCreateDto
    {
        public string NomService { get; set; } = string.Empty;
        public string UrlHealthCheck { get; set; } = string.Empty;
    }

    // ─── JournalSysteme ────────────────────────────────────────────────────────
    public class JournalSystemeDto
    {
        public int Id { get; set; }
        public DateTime DateEvenement { get; set; }
        public string Niveau { get; set; } = string.Empty;
        public string Source { get; set; } = string.Empty;
        public string Message { get; set; } = string.Empty;
        public string? Details { get; set; }
        public string? CorrelationId { get; set; }
    }

    // ─── Administrateur & AgentPromoteur ───────────────────────────────────────
    public class AdministrateurDto
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public bool Actif { get; set; }
        public string UserIdMs1 { get; set; } = string.Empty;
    }

    public class AdministrateurCreateDto
    {
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string UserIdMs1 { get; set; } = string.Empty;
    }

    public class AgentPromoteurDto
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public bool Actif { get; set; }
        public string UserIdMs1 { get; set; } = string.Empty;
    }

    public class AgentPromoteurCreateDto
    {
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string UserIdMs1 { get; set; } = string.Empty;
    }

    // ─── Réponse générique paginée ────────────────────────────────────────────
    public class PagedResult<T>
    {
        public IEnumerable<T> Data { get; set; } = Enumerable.Empty<T>();
        public int Total { get; set; }
        public int Page { get; set; }
        public int PageSize { get; set; }
    }
}
