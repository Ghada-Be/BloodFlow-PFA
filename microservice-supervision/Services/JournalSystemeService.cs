using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS3.Services
{
    /// <summary>
    /// Service de journalisation système.
    /// Enregistre tous les événements importants dans la table JournalSysteme.
    /// </summary>
    public class JournalSystemeService : IJournalSystemeService
    {
        private readonly AppDbContext _context;

        public JournalSystemeService(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<JournalSystemeDto>> GetAllAsync(
            string? niveau = null, string? source = null, DateTime? depuis = null)
        {
            var query = _context.JournalSysteme.AsQueryable();

            if (!string.IsNullOrEmpty(niveau))
                query = query.Where(j => j.Niveau == niveau);

            if (!string.IsNullOrEmpty(source))
                query = query.Where(j => j.Source.Contains(source));

            if (depuis.HasValue)
                query = query.Where(j => j.DateEvenement >= depuis.Value);

            return await query
                .OrderByDescending(j => j.DateEvenement)
                .Take(500) // Limite de sécurité
                .Select(j => new JournalSystemeDto
                {
                    Id = j.Id,
                    DateEvenement = j.DateEvenement,
                    Niveau = j.Niveau,
                    Source = j.Source,
                    Message = j.Message,
                    Details = j.Details,
                    CorrelationId = j.CorrelationId
                })
                .ToListAsync();
        }

        public async Task LoggerAsync(string niveau, string source, string message, string? details = null)
        {
            var entree = new JournalSysteme
            {
                DateEvenement = DateTime.UtcNow,
                Niveau = niveau,
                Source = source,
                Message = message,
                Details = details,
                CorrelationId = Guid.NewGuid().ToString("N")[..8]
            };

            _context.JournalSysteme.Add(entree);
            await _context.SaveChangesAsync();
        }
    }
}
