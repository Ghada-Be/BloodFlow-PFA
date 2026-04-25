using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS3.Services
{
    /// <summary>
    /// Service de gestion des collectes de sang.
    /// Vérifie les conflits de date/lieu avant création.
    /// </summary>
    public class CollecteSangService : ICollecteSangService
    {
        private readonly AppDbContext _context;
        private readonly IJournalSystemeService _journal;

        public CollecteSangService(AppDbContext context, IJournalSystemeService journal)
        {
            _context = context;
            _journal = journal;
        }

        public async Task<IEnumerable<CollecteSangDto>> GetAllAsync(string? ville = null, string? statut = null)
        {
            var query = _context.CollectesSang
                .Include(c => c.AgentPromoteur)
                .Include(c => c.Campagne)
                .Include(c => c.Benevoles)
                .AsQueryable();

            if (!string.IsNullOrEmpty(ville))
                query = query.Where(c => c.Ville.Contains(ville));

            if (!string.IsNullOrEmpty(statut))
                query = query.Where(c => c.Statut == statut);

            return await query
                .OrderBy(c => c.DateCollecte)
                .Select(c => ToDto(c))
                .ToListAsync();
        }

        public async Task<CollecteSangDto?> GetByIdAsync(int id)
        {
            var c = await _context.CollectesSang
                .Include(c => c.AgentPromoteur)
                .Include(c => c.Campagne)
                .Include(c => c.Benevoles)
                .FirstOrDefaultAsync(c => c.Id == id);

            return c == null ? null : ToDto(c);
        }

        public async Task<CollecteSangDto> CreateAsync(CollecteSangCreateDto dto)
        {
            // Vérification de conflit : même lieu/ville/date
            var conflit = await _context.CollectesSang.AnyAsync(c =>
                c.Ville == dto.Ville &&
                c.Lieu == dto.Lieu &&
                c.DateCollecte.Date == dto.DateCollecte.Date &&
                c.Statut != "Annulee");

            if (conflit)
                throw new InvalidOperationException(
                    "Une collecte existe déjà à ce lieu, cette ville et cette date.");

            var collecte = new CollecteSang
            {
                Lieu = dto.Lieu,
                Ville = dto.Ville,
                DateCollecte = dto.DateCollecte,
                HeureDebut = dto.HeureDebut,
                HeureFin = dto.HeureFin,
                ObjectifPoches = dto.ObjectifPoches,
                Statut = "Planifiee",
                CampagneId = dto.CampagneId,
                AgentPromoteurId = dto.AgentPromoteurId
            };

            _context.CollectesSang.Add(collecte);
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Info", "CollecteSangService",
                $"Collecte créée à {dto.Ville} - {dto.Lieu} le {dto.DateCollecte:dd/MM/yyyy}");

            // Recharger avec navigations
            return (await GetByIdAsync(collecte.Id))!;
        }

        public async Task<CollecteSangDto?> UpdateAsync(int id, CollecteSangUpdateDto dto)
        {
            var collecte = await _context.CollectesSang
                .Include(c => c.AgentPromoteur)
                .Include(c => c.Campagne)
                .Include(c => c.Benevoles)
                .FirstOrDefaultAsync(c => c.Id == id);

            if (collecte == null) return null;

            collecte.Lieu = dto.Lieu;
            collecte.Ville = dto.Ville;
            collecte.DateCollecte = dto.DateCollecte;
            collecte.HeureDebut = dto.HeureDebut;
            collecte.HeureFin = dto.HeureFin;
            collecte.ObjectifPoches = dto.ObjectifPoches;
            collecte.Statut = dto.Statut;

            await _context.SaveChangesAsync();
            return ToDto(collecte);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var collecte = await _context.CollectesSang.FindAsync(id);
            if (collecte == null) return false;

            _context.CollectesSang.Remove(collecte);
            await _context.SaveChangesAsync();
            return true;
        }

        private static CollecteSangDto ToDto(CollecteSang c) => new()
        {
            Id = c.Id,
            Lieu = c.Lieu,
            Ville = c.Ville,
            DateCollecte = c.DateCollecte,
            HeureDebut = c.HeureDebut.ToString(@"hh\:mm"),
            HeureFin = c.HeureFin.ToString(@"hh\:mm"),
            ObjectifPoches = c.ObjectifPoches,
            Statut = c.Statut,
            CampagneId = c.CampagneId,
            TitreCampagne = c.Campagne?.Titre,
            AgentPromoteurId = c.AgentPromoteurId,
            NomAgent = c.AgentPromoteur != null
                ? $"{c.AgentPromoteur.Prenom} {c.AgentPromoteur.Nom}"
                : string.Empty,
            NombreBenevoles = c.Benevoles?.Count ?? 0
        };
    }
}
