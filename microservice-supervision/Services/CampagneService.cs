using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS3.Services
{
    /// <summary>
    /// Service de gestion des campagnes de sensibilisation.
    /// Permet à un agent promoteur de créer, modifier et suivre ses campagnes.
    /// </summary>
    public class CampagneService : ICampagneService
    {
        private readonly AppDbContext _context;
        private readonly IJournalSystemeService _journal;

        public CampagneService(AppDbContext context, IJournalSystemeService journal)
        {
            _context = context;
            _journal = journal;
        }

        public async Task<IEnumerable<CampagneDto>> GetAllAsync(string? statut = null)
        {
            var query = _context.Campagnes
                .Include(c => c.AgentPromoteur)
                .Include(c => c.Collectes)
                .AsQueryable();

            if (!string.IsNullOrEmpty(statut))
                query = query.Where(c => c.Statut == statut);

            return await query
                .OrderByDescending(c => c.DateDebut)
                .Select(c => ToDto(c))
                .ToListAsync();
        }

        public async Task<CampagneDto?> GetByIdAsync(int id)
        {
            var c = await _context.Campagnes
                .Include(c => c.AgentPromoteur)
                .Include(c => c.Collectes)
                .FirstOrDefaultAsync(c => c.Id == id);

            return c == null ? null : ToDto(c);
        }

        public async Task<CampagneDto> CreateAsync(CampagneCreateDto dto)
        {
            // Vérification que l'agent promoteur existe
            var agent = await _context.AgentsPromoteurs.FindAsync(dto.AgentPromoteurId);
            if (agent == null)
                throw new KeyNotFoundException($"Agent promoteur #{dto.AgentPromoteurId} introuvable.");

            var campagne = new Campagne
            {
                Titre = dto.Titre,
                Objectif = dto.Objectif,
                Description = dto.Description,
                DateDebut = dto.DateDebut,
                DateFin = dto.DateFin,
                Statut = "Brouillon",
                AgentPromoteurId = dto.AgentPromoteurId
            };

            _context.Campagnes.Add(campagne);
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Info", "CampagneService",
                $"Campagne créée : {campagne.Titre}", $"Agent={dto.AgentPromoteurId}");

            // Recharger avec navigation
            campagne.AgentPromoteur = agent;
            return ToDto(campagne);
        }

        public async Task<CampagneDto?> UpdateAsync(int id, CampagneUpdateDto dto)
        {
            var campagne = await _context.Campagnes
                .Include(c => c.AgentPromoteur)
                .Include(c => c.Collectes)
                .FirstOrDefaultAsync(c => c.Id == id);

            if (campagne == null) return null;

            campagne.Titre = dto.Titre;
            campagne.Objectif = dto.Objectif;
            campagne.Description = dto.Description;
            campagne.DateDebut = dto.DateDebut;
            campagne.DateFin = dto.DateFin;
            campagne.Statut = dto.Statut;

            await _context.SaveChangesAsync();
            return ToDto(campagne);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var campagne = await _context.Campagnes.FindAsync(id);
            if (campagne == null) return false;

            _context.Campagnes.Remove(campagne);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> ChangerStatutAsync(int id, string statut)
        {
            var campagne = await _context.Campagnes.FindAsync(id);
            if (campagne == null) return false;

            campagne.Statut = statut;
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Info", "CampagneService",
                $"Campagne #{id} - statut changé en {statut}");
            return true;
        }

        private static CampagneDto ToDto(Campagne c) => new()
        {
            Id = c.Id,
            Titre = c.Titre,
            Objectif = c.Objectif,
            Description = c.Description,
            DateDebut = c.DateDebut,
            DateFin = c.DateFin,
            Statut = c.Statut,
            AgentPromoteurId = c.AgentPromoteurId,
            NomAgent = c.AgentPromoteur != null
                ? $"{c.AgentPromoteur.Prenom} {c.AgentPromoteur.Nom}"
                : string.Empty,
            NombreCollectes = c.Collectes?.Count ?? 0
        };
    }
}
