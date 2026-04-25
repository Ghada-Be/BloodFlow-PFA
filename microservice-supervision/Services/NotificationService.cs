using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS3.Services
{
    /// <summary>
    /// Service de gestion des notifications.
    /// Dans ce projet étudiant, l'envoi réel (Email/SMS) est simulé.
    /// En production, on brancherait un service comme SendGrid ou Twilio.
    /// </summary>
    public class NotificationService : INotificationService
    {
        private readonly AppDbContext _context;
        private readonly IJournalSystemeService _journal;

        public NotificationService(AppDbContext context, IJournalSystemeService journal)
        {
            _context = context;
            _journal = journal;
        }

        public async Task<IEnumerable<NotificationDto>> GetAllAsync()
        {
            return await _context.Notifications
                .OrderByDescending(n => n.DateEnvoi)
                .Select(n => ToDto(n))
                .ToListAsync();
        }

        public async Task<NotificationDto?> GetByIdAsync(int id)
        {
            var n = await _context.Notifications.FindAsync(id);
            return n == null ? null : ToDto(n);
        }

        public async Task<NotificationDto> CreateAsync(NotificationCreateDto dto)
        {
            var notif = new Notification
            {
                Message = dto.Message,
                Type = dto.Type,
                Canal = dto.Canal,
                Destinataire = dto.Destinataire,
                StatutEnvoi = "EnAttente",
                DateEnvoi = DateTime.UtcNow,
                AlerteId = dto.AlerteId,
                CampagneId = dto.CampagneId
            };

            _context.Notifications.Add(notif);
            await _context.SaveChangesAsync();

            // Simulation de l'envoi
            await SimulerEnvoiAsync(notif);

            return ToDto(notif);
        }

        public async Task<bool> MarquerEnvoyeeAsync(int id)
        {
            var notif = await _context.Notifications.FindAsync(id);
            if (notif == null) return false;

            notif.StatutEnvoi = "Envoyee";
            await _context.SaveChangesAsync();
            return true;
        }

        /// <summary>
        /// Envoie des notifications à tous les donneurs simulés d'un groupe sanguin.
        /// REMARQUE : Dans ce projet, la liste de donneurs est MOCKÉE.
        /// En réalité, on ferait un appel HTTP vers MS1 ou MS2 pour récupérer
        /// la liste réelle des donneurs par groupe sanguin et ville.
        /// </summary>
        public async Task EnvoyerNotificationsGroupeAsync(string groupeSanguin, string ville, string message)
        {
            // MOCK : simulation de 3 destinataires pour la démonstration
            var destinatairesMockes = new List<string>
            {
                $"donneur1-{groupeSanguin.ToLower().Replace("+","p").Replace("-","n")}@example.com",
                $"donneur2-{groupeSanguin.ToLower().Replace("+","p").Replace("-","n")}@example.com",
                $"donneur3-{groupeSanguin.ToLower().Replace("+","p").Replace("-","n")}@example.com"
            };

            foreach (var dest in destinatairesMockes)
            {
                var notif = new Notification
                {
                    Message = message,
                    Type = "AppelUrgent",
                    Canal = "Email",
                    Destinataire = dest,
                    StatutEnvoi = "Envoyee", // Simulé comme envoyé directement
                    DateEnvoi = DateTime.UtcNow
                };
                _context.Notifications.Add(notif);
            }

            await _context.SaveChangesAsync();
            await _journal.LoggerAsync("Info", "NotificationService",
                $"Notifications urgentes envoyées pour groupe {groupeSanguin} à {ville}",
                $"Nombre destinataires simulés : {destinatairesMockes.Count}");
        }

        private async Task SimulerEnvoiAsync(Notification notif)
        {
            // Simulation : on marque directement comme envoyée
            // En production : appel SMTP, API SMS, etc.
            notif.StatutEnvoi = "Envoyee";
            await _context.SaveChangesAsync();
            await _journal.LoggerAsync("Info", "NotificationService",
                $"Notification simulée vers {notif.Destinataire} via {notif.Canal}");
        }

        private static NotificationDto ToDto(Notification n) => new()
        {
            Id = n.Id,
            DateEnvoi = n.DateEnvoi,
            Message = n.Message,
            Type = n.Type,
            Canal = n.Canal,
            StatutEnvoi = n.StatutEnvoi,
            Destinataire = n.Destinataire,
            AlerteId = n.AlerteId,
            CampagneId = n.CampagneId
        };
    }
}
