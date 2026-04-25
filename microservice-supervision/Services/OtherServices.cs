using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;
using System.Text.Json;

namespace BloodFlow.MS3.Services
{
    // ═══════════════════════════════════════════════════════════════════════════
    // BENEVOLE SERVICE
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Service de gestion des bénévoles.
    /// Permet de recruter et affecter des bénévoles à des collectes.
    /// </summary>
    public class BenevoleService : IBenevoleService
    {
        private readonly AppDbContext _context;
        private readonly IJournalSystemeService _journal;

        public BenevoleService(AppDbContext context, IJournalSystemeService journal)
        {
            _context = context;
            _journal = journal;
        }

        public async Task<IEnumerable<BenevoleDto>> GetAllAsync(int? collecteId = null)
        {
            var query = _context.Benevoles
                .Include(b => b.CollecteSang)
                .AsQueryable();

            if (collecteId.HasValue)
                query = query.Where(b => b.CollecteSangId == collecteId);

            return await query
                .Select(b => new BenevoleDto
                {
                    Id = b.Id,
                    Nom = b.Nom,
                    Prenom = b.Prenom,
                    Contact = b.Contact,
                    Email = b.Email,
                    Disponibilite = b.Disponibilite,
                    CollecteSangId = b.CollecteSangId,
                    LieuCollecte = b.CollecteSang != null
                        ? $"{b.CollecteSang.Lieu}, {b.CollecteSang.Ville}"
                        : null
                })
                .ToListAsync();
        }

        public async Task<BenevoleDto?> GetByIdAsync(int id)
        {
            var b = await _context.Benevoles
                .Include(b => b.CollecteSang)
                .FirstOrDefaultAsync(b => b.Id == id);

            if (b == null) return null;
            return new BenevoleDto
            {
                Id = b.Id, Nom = b.Nom, Prenom = b.Prenom,
                Contact = b.Contact, Email = b.Email,
                Disponibilite = b.Disponibilite,
                CollecteSangId = b.CollecteSangId,
                LieuCollecte = b.CollecteSang != null
                    ? $"{b.CollecteSang.Lieu}, {b.CollecteSang.Ville}" : null
            };
        }

        public async Task<BenevoleDto> CreateAsync(BenevoleCreateDto dto)
        {
            var benevole = new Benevole
            {
                Nom = dto.Nom, Prenom = dto.Prenom,
                Contact = dto.Contact, Email = dto.Email,
                Disponibilite = dto.Disponibilite,
                CollecteSangId = dto.CollecteSangId
            };

            _context.Benevoles.Add(benevole);
            await _context.SaveChangesAsync();
            await _journal.LoggerAsync("Info", "BenevoleService",
                $"Bénévole recruté : {dto.Prenom} {dto.Nom}");

            return (await GetByIdAsync(benevole.Id))!;
        }

        public async Task<bool> AffecterACollecteAsync(int benevoleId, int collecteId)
        {
            var benevole = await _context.Benevoles.FindAsync(benevoleId);
            if (benevole == null) return false;

            var collecte = await _context.CollectesSang.FindAsync(collecteId);
            if (collecte == null) return false;

            benevole.CollecteSangId = collecteId;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var b = await _context.Benevoles.FindAsync(id);
            if (b == null) return false;
            _context.Benevoles.Remove(b);
            await _context.SaveChangesAsync();
            return true;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // RAPPORT SERVICE
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Service de génération de rapports.
    /// Les rapports sont générés en JSON/CSV/Texte à partir des données réelles de la BDD.
    /// </summary>
    public class RapportService : IRapportService
    {
        private readonly AppDbContext _context;
        private readonly IJournalSystemeService _journal;

        public RapportService(AppDbContext context, IJournalSystemeService journal)
        {
            _context = context;
            _journal = journal;
        }

        public async Task<IEnumerable<RapportDto>> GetAllAsync()
        {
            return await _context.Rapports
                .Include(r => r.CreeParAdmin)
                .OrderByDescending(r => r.DateGeneration)
                .Select(r => ToDto(r))
                .ToListAsync();
        }

        public async Task<RapportDto?> GetByIdAsync(int id)
        {
            var r = await _context.Rapports
                .Include(r => r.CreeParAdmin)
                .FirstOrDefaultAsync(r => r.Id == id);
            return r == null ? null : ToDto(r);
        }

        public async Task<RapportDto> GenererAsync(RapportGenerateDto dto)
        {
            var admin = await _context.Administrateurs.FindAsync(dto.CreeParAdminId);
            if (admin == null)
                throw new KeyNotFoundException($"Administrateur #{dto.CreeParAdminId} introuvable.");

            string contenu = dto.Type switch
            {
                "Global" => await GenererRapportGlobalAsync(),
                "Campagne" => await GenererRapportCampagnesAsync(),
                "Collecte" => await GenererRapportCollectesAsync(),
                "Alertes" => await GenererRapportAlertesAsync(),
                "Système" => await GenererRapportSystemeAsync(),
                _ => throw new ArgumentException($"Type de rapport inconnu : {dto.Type}")
            };

            var rapport = new Rapport
            {
                DateGeneration = DateTime.UtcNow,
                Type = dto.Type,
                Contenu = contenu,
                Format = dto.Format,
                CreeParAdminId = dto.CreeParAdminId
            };

            _context.Rapports.Add(rapport);
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Info", "RapportService",
                $"Rapport {dto.Type} généré par admin #{dto.CreeParAdminId}");

            rapport.CreeParAdmin = admin;
            return ToDto(rapport);
        }

        private async Task<string> GenererRapportGlobalAsync()
        {
            var data = new
            {
                GenereLe = DateTime.UtcNow,
                NombreAdmins = await _context.Administrateurs.CountAsync(),
                NombreAgents = await _context.AgentsPromoteurs.CountAsync(),
                NombreCampagnes = await _context.Campagnes.CountAsync(),
                NombreCollectes = await _context.CollectesSang.CountAsync(),
                NombreAlertes = await _context.Alertes.CountAsync(a => a.Etat == "Ouverte"),
                NombreNotifications = await _context.Notifications.CountAsync(),
                NombreBenevoles = await _context.Benevoles.CountAsync()
            };
            return JsonSerializer.Serialize(data, new JsonSerializerOptions { WriteIndented = true });
        }

        private async Task<string> GenererRapportCampagnesAsync()
        {
            var campagnes = await _context.Campagnes
                .Include(c => c.AgentPromoteur)
                .Include(c => c.Collectes)
                .Select(c => new { c.Id, c.Titre, c.Statut, c.DateDebut, c.DateFin,
                    Agent = $"{c.AgentPromoteur!.Prenom} {c.AgentPromoteur.Nom}",
                    NbCollectes = c.Collectes.Count })
                .ToListAsync();
            return JsonSerializer.Serialize(campagnes, new JsonSerializerOptions { WriteIndented = true });
        }

        private async Task<string> GenererRapportCollectesAsync()
        {
            var collectes = await _context.CollectesSang
                .Select(c => new { c.Id, c.Lieu, c.Ville, c.DateCollecte,
                    c.ObjectifPoches, c.Statut })
                .ToListAsync();
            return JsonSerializer.Serialize(collectes, new JsonSerializerOptions { WriteIndented = true });
        }

        private async Task<string> GenererRapportAlertesAsync()
        {
            var alertes = await _context.Alertes
                .Select(a => new { a.Id, a.Titre, a.NiveauUrgence, a.Etat, a.DateAlerte })
                .ToListAsync();
            return JsonSerializer.Serialize(alertes, new JsonSerializerOptions { WriteIndented = true });
        }

        private async Task<string> GenererRapportSystemeAsync()
        {
            var services = await _context.ServicesSurveilles
                .Select(s => new { s.NomService, s.Etat, s.DateDerniereVerification, s.DerniereLatenceMs })
                .ToListAsync();
            return JsonSerializer.Serialize(services, new JsonSerializerOptions { WriteIndented = true });
        }

        private static RapportDto ToDto(Rapport r) => new()
        {
            Id = r.Id,
            DateGeneration = r.DateGeneration,
            Type = r.Type,
            Contenu = r.Contenu,
            Format = r.Format,
            CreeParAdminId = r.CreeParAdminId,
            NomAdmin = r.CreeParAdmin != null
                ? $"{r.CreeParAdmin.Prenom} {r.CreeParAdmin.Nom}" : string.Empty
        };
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // APPEL URGENT SERVICE
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Service pour lancer des appels urgents aux donneurs.
    /// IMPORTANT : La récupération des donneurs est MOCKÉE dans ce projet.
    /// En production, on appellerait MS1 ou MS2 via HTTP pour obtenir la liste réelle.
    /// </summary>
    public class AppelUrgentService : IAppelUrgentService
    {
        private readonly AppDbContext _context;
        private readonly INotificationService _notifService;
        private readonly IJournalSystemeService _journal;

        public AppelUrgentService(AppDbContext context,
            INotificationService notifService,
            IJournalSystemeService journal)
        {
            _context = context;
            _notifService = notifService;
            _journal = journal;
        }

        public async Task<IEnumerable<AppelUrgentDto>> GetAllAsync()
        {
            return await _context.AppelsUrgents
                .OrderByDescending(a => a.DateAppel)
                .Select(a => ToDto(a))
                .ToListAsync();
        }

        public async Task<AppelUrgentDto?> GetByIdAsync(int id)
        {
            var a = await _context.AppelsUrgents.FindAsync(id);
            return a == null ? null : ToDto(a);
        }

        public async Task<AppelUrgentDto> LancerAppelAsync(AppelUrgentCreateDto dto)
        {
            var appel = new AppelUrgent
            {
                GroupeSanguin = dto.GroupeSanguin,
                Ville = dto.Ville,
                Message = dto.Message,
                Priorite = dto.Priorite,
                EstActif = true,
                DateAppel = DateTime.UtcNow,
                CreeParAdminId = dto.CreeParAdminId
            };

            _context.AppelsUrgents.Add(appel);
            await _context.SaveChangesAsync();

            // Envoyer des notifications aux donneurs (simulé)
            await _notifService.EnvoyerNotificationsGroupeAsync(
                dto.GroupeSanguin, dto.Ville,
                $"[URGENT - {dto.Priorite}] {dto.Message}");

            // Mettre à jour le compteur
            appel.NombreNotificationsEnvoyees = 3; // simulé
            await _context.SaveChangesAsync();

            await _journal.LoggerAsync("Avertissement", "AppelUrgentService",
                $"Appel urgent lancé pour groupe {dto.GroupeSanguin} à {dto.Ville}",
                $"Priorité={dto.Priorite}");

            return ToDto(appel);
        }

        public async Task<bool> DesactiverAsync(int id)
        {
            var appel = await _context.AppelsUrgents.FindAsync(id);
            if (appel == null) return false;
            appel.EstActif = false;
            await _context.SaveChangesAsync();
            return true;
        }

        private static AppelUrgentDto ToDto(AppelUrgent a) => new()
        {
            Id = a.Id,
            DateAppel = a.DateAppel,
            GroupeSanguin = a.GroupeSanguin,
            Ville = a.Ville,
            Message = a.Message,
            Priorite = a.Priorite,
            EstActif = a.EstActif,
            NombreNotificationsEnvoyees = a.NombreNotificationsEnvoyees
        };
    }
}
