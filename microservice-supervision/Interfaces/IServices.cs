using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Models;

namespace BloodFlow.MS3.Interfaces
{
    // ─── Alerte ───────────────────────────────────────────────────────────────
    public interface IAlerteService
    {
        Task<IEnumerable<AlerteDto>> GetAllAsync(string? etat = null, string? niveau = null);
        Task<AlerteDto?> GetByIdAsync(int id);
        Task<AlerteDto> CreateAsync(AlerteCreateDto dto);
        Task<bool> UpdateEtatAsync(int id, string etat);
        Task<bool> DeleteAsync(int id);
    }

    // ─── Notification ──────────────────────────────────────────────────────────
    public interface INotificationService
    {
        Task<IEnumerable<NotificationDto>> GetAllAsync();
        Task<NotificationDto?> GetByIdAsync(int id);
        Task<NotificationDto> CreateAsync(NotificationCreateDto dto);
        Task<bool> MarquerEnvoyeeAsync(int id);
        Task EnvoyerNotificationsGroupeAsync(string groupeSanguin, string ville, string message);
    }

    // ─── Campagne ─────────────────────────────────────────────────────────────
    public interface ICampagneService
    {
        Task<IEnumerable<CampagneDto>> GetAllAsync(string? statut = null);
        Task<CampagneDto?> GetByIdAsync(int id);
        Task<CampagneDto> CreateAsync(CampagneCreateDto dto);
        Task<CampagneDto?> UpdateAsync(int id, CampagneUpdateDto dto);
        Task<bool> DeleteAsync(int id);
        Task<bool> ChangerStatutAsync(int id, string statut);
    }

    // ─── CollecteSang ─────────────────────────────────────────────────────────
    public interface ICollecteSangService
    {
        Task<IEnumerable<CollecteSangDto>> GetAllAsync(string? ville = null, string? statut = null);
        Task<CollecteSangDto?> GetByIdAsync(int id);
        Task<CollecteSangDto> CreateAsync(CollecteSangCreateDto dto);
        Task<CollecteSangDto?> UpdateAsync(int id, CollecteSangUpdateDto dto);
        Task<bool> DeleteAsync(int id);
    }

    // ─── Benevole ─────────────────────────────────────────────────────────────
    public interface IBenevoleService
    {
        Task<IEnumerable<BenevoleDto>> GetAllAsync(int? collecteId = null);
        Task<BenevoleDto?> GetByIdAsync(int id);
        Task<BenevoleDto> CreateAsync(BenevoleCreateDto dto);
        Task<bool> AffecterACollecteAsync(int benevoleId, int collecteId);
        Task<bool> DeleteAsync(int id);
    }

    // ─── Rapport ──────────────────────────────────────────────────────────────
    public interface IRapportService
    {
        Task<IEnumerable<RapportDto>> GetAllAsync();
        Task<RapportDto?> GetByIdAsync(int id);
        Task<RapportDto> GenererAsync(RapportGenerateDto dto);
    }

    // ─── ServiceSurveille ─────────────────────────────────────────────────────
    public interface IServiceSurveilleService
    {
        Task<IEnumerable<ServiceSurveilleDto>> GetAllAsync();
        Task<ServiceSurveilleDto?> GetByIdAsync(int id);
        Task<ServiceSurveilleDto> CreateAsync(ServiceSurveilleCreateDto dto);
        Task VerifierTousLesServicesAsync();
        Task<ServiceSurveilleDto?> VerifierServiceAsync(int id);
    }

    // ─── JournalSysteme ───────────────────────────────────────────────────────
    public interface IJournalSystemeService
    {
        Task<IEnumerable<JournalSystemeDto>> GetAllAsync(string? niveau = null, string? source = null, DateTime? depuis = null);
        Task LoggerAsync(string niveau, string source, string message, string? details = null);
    }

    // ─── AppelUrgent ──────────────────────────────────────────────────────────
    public interface IAppelUrgentService
    {
        Task<IEnumerable<AppelUrgentDto>> GetAllAsync();
        Task<AppelUrgentDto?> GetByIdAsync(int id);
        Task<AppelUrgentDto> LancerAppelAsync(AppelUrgentCreateDto dto);
        Task<bool> DesactiverAsync(int id);
    }
}
