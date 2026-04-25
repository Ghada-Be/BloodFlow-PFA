using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS3.Services
{
    /// <summary>
    /// Service de gestion des alertes système.
    /// Une alerte peut être créée manuellement ou automatiquement par la supervision.
    /// </summary>
    public class AlerteService : IAlerteService
    {
        private readonly AppDbContext _context;
        private readonly IJournalSystemeService _journal;

        public AlerteService(AppDbContext context, IJournalSystemeService journal)
        {
            _context = context;
            _journal = journal;
        }

        public async Task<IEnumerable<AlerteDto>> GetAllAsync(string? etat = null, string? niveau = null)
        {
            var query = _context.Alertes
                .Include(a => a.ServiceSurveille)
                .AsQueryable();

            if (!string.IsNullOrEmpty(etat))
                query = query.Where(a => a.Etat == etat);

            if (!string.IsNullOrEmpty(niveau))
                query = query.Where(a => a.NiveauUrgence == niveau);

            return await query
                .OrderByDescending(a => a.DateAlerte)
                .Select(a => ToDto(a))
                .ToListAsync();
        }

        public async Task<AlerteDto?> GetByIdAsync(int id)
        {
            var alerte = await _context.Alertes
                .Include(a => a.ServiceSurveille)
                .FirstOrDefaultAsync(a => a.Id == id);

            return alerte == null ? null : ToDto(alerte);
        }

        public async Task<AlerteDto> CreateAsync(AlerteCreateDto dto)
        {
            var alerte = new Alerte
            {
                NiveauUrgence = dto.NiveauUrgence,
                Titre = dto.Titre,
                Description = dto.Description,
                ServiceSurveilleId = dto.ServiceSurveilleId,
                CreeeParSysteme = false,
                Etat = "Ouverte",
                DateAlerte = DateTime.UtcNow
            };

            _context.Alertes.Add(alerte);
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Info", "AlerteService",
                $"Nouvelle alerte créée : {alerte.Titre}", $"NiveauUrgence={alerte.NiveauUrgence}");

            return ToDto(alerte);
        }

        public async Task<bool> UpdateEtatAsync(int id, string etat)
        {
            var alerte = await _context.Alertes.FindAsync(id);
            if (alerte == null) return false;

            alerte.Etat = etat;
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Info", "AlerteService",
                $"Alerte #{id} mise à jour : état = {etat}");

            return true;
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var alerte = await _context.Alertes.FindAsync(id);
            if (alerte == null) return false;

            _context.Alertes.Remove(alerte);
            await _context.SaveChangesAsync();
            return true;
        }

        // Méthode interne pour créer une alerte automatique (utilisée par ServiceSurveilleService)
        public async Task<Alerte> CreerAlerteAutomatiqueAsync(string titre, string description,
            string niveau, int? serviceId)
        {
            var alerte = new Alerte
            {
                NiveauUrgence = niveau,
                Titre = titre,
                Description = description,
                ServiceSurveilleId = serviceId,
                CreeeParSysteme = true,
                Etat = "Ouverte",
                DateAlerte = DateTime.UtcNow
            };

            _context.Alertes.Add(alerte);
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Avertissement", "Système",
                $"Alerte automatique créée : {titre}", $"ServiceId={serviceId}");

            return alerte;
        }

        // Conversion Model → DTO
        private static AlerteDto ToDto(Alerte a) => new()
        {
            Id = a.Id,
            DateAlerte = a.DateAlerte,
            NiveauUrgence = a.NiveauUrgence,
            Titre = a.Titre,
            Description = a.Description,
            Etat = a.Etat,
            ServiceSurveilleId = a.ServiceSurveilleId,
            NomService = a.ServiceSurveille?.NomService,
            CreeeParSysteme = a.CreeeParSysteme
        };
    }
}
