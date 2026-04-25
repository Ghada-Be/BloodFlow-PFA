using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;
using System.Diagnostics;

namespace BloodFlow.MS3.Services
{
    /// <summary>
    /// Service de surveillance des microservices.
    /// Effectue des requêtes HTTP vers les endpoints /health de chaque service
    /// et met à jour leur état dans la base de données.
    /// Si un service est dégradé ou indisponible, une alerte est créée automatiquement.
    /// </summary>
    public class ServiceSurveilleService : IServiceSurveilleService
    {
        private readonly AppDbContext _context;
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly IJournalSystemeService _journal;
        private readonly AlerteService _alerteService;

        public ServiceSurveilleService(
            AppDbContext context,
            IHttpClientFactory httpClientFactory,
            IJournalSystemeService journal,
            AlerteService alerteService)
        {
            _context = context;
            _httpClientFactory = httpClientFactory;
            _journal = journal;
            _alerteService = alerteService;
        }

        public async Task<IEnumerable<ServiceSurveilleDto>> GetAllAsync()
        {
            return await _context.ServicesSurveilles
                .Select(s => ToDto(s))
                .ToListAsync();
        }

        public async Task<ServiceSurveilleDto?> GetByIdAsync(int id)
        {
            var s = await _context.ServicesSurveilles.FindAsync(id);
            return s == null ? null : ToDto(s);
        }

        public async Task<ServiceSurveilleDto> CreateAsync(ServiceSurveilleCreateDto dto)
        {
            var service = new ServiceSurveille
            {
                NomService = dto.NomService,
                UrlHealthCheck = dto.UrlHealthCheck,
                Etat = "Inconnu"
            };
            _context.ServicesSurveilles.Add(service);
            await _context.SaveChangesAsync();
            return ToDto(service);
        }

        /// <summary>
        /// Vérifie l'état de TOUS les services enregistrés.
        /// Appelé par le BackgroundService périodique.
        /// </summary>
        public async Task VerifierTousLesServicesAsync()
        {
            var services = await _context.ServicesSurveilles.ToListAsync();
            foreach (var service in services)
            {
                await VerifierEtMettreAJourAsync(service);
            }
            await _context.SaveChangesAsync();
        }

        /// <summary>
        /// Vérifie l'état d'UN service spécifique.
        /// </summary>
        public async Task<ServiceSurveilleDto?> VerifierServiceAsync(int id)
        {
            var service = await _context.ServicesSurveilles.FindAsync(id);
            if (service == null) return null;

            await VerifierEtMettreAJourAsync(service);
            await _context.SaveChangesAsync();
            return ToDto(service);
        }

        // ─── Logique interne de vérification ─────────────────────────────────
        private async Task VerifierEtMettreAJourAsync(ServiceSurveille service)
        {
            var etatPrecedent = service.Etat;
            var chrono = Stopwatch.StartNew();

            try
            {
                var client = _httpClientFactory.CreateClient("HealthCheck");
                // Timeout court pour ne pas bloquer
                var cts = new CancellationTokenSource(TimeSpan.FromSeconds(5));
                var response = await client.GetAsync(service.UrlHealthCheck, cts.Token);
                chrono.Stop();

                service.DerniereLatenceMs = (int)chrono.ElapsedMilliseconds;
                service.DateDerniereVerification = DateTime.UtcNow;

                if (response.IsSuccessStatusCode)
                {
                    service.Etat = service.DerniereLatenceMs > 2000 ? "Dégradé" : "Disponible";
                    service.MessageEtat = $"HTTP {(int)response.StatusCode} - {service.DerniereLatenceMs}ms";
                }
                else
                {
                    service.Etat = "Dégradé";
                    service.MessageEtat = $"HTTP {(int)response.StatusCode}";
                }
            }
            catch (Exception ex)
            {
                chrono.Stop();
                service.Etat = "Indisponible";
                service.DateDerniereVerification = DateTime.UtcNow;
                service.MessageEtat = ex.Message.Length > 200
                    ? ex.Message[..200] : ex.Message;
            }

            // Créer une alerte automatique si le service n'est plus disponible
            if (etatPrecedent == "Disponible" && service.Etat != "Disponible")
            {
                await _alerteService.CreerAlerteAutomatiqueAsync(
                    titre: $"Service {service.NomService} : {service.Etat}",
                    description: $"Le service est passé de '{etatPrecedent}' à '{service.Etat}'. Message : {service.MessageEtat}",
                    niveau: service.Etat == "Indisponible" ? "Critique" : "Élevé",
                    serviceId: service.Id
                );
            }

            await _journal.LoggerAsync("Info", "ServiceSurveilleService",
                $"[{service.NomService}] Etat : {service.Etat} ({service.DerniereLatenceMs}ms)");
        }

        private static ServiceSurveilleDto ToDto(ServiceSurveille s) => new()
        {
            Id = s.Id,
            NomService = s.NomService,
            UrlHealthCheck = s.UrlHealthCheck,
            Etat = s.Etat,
            DateDerniereVerification = s.DateDerniereVerification,
            DerniereLatenceMs = s.DerniereLatenceMs,
            MessageEtat = s.MessageEtat
        };
    }
}
