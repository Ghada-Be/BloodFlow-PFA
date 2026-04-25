using BloodFlow.MS3.Interfaces;

namespace BloodFlow.MS3.Services
{
    /// <summary>
    /// Service d'arrière-plan qui surveille automatiquement les microservices toutes les 60 secondes.
    /// Il tourne en parallèle du reste de l'API sans bloquer les requêtes.
    /// C'est un "Worker" natif d'ASP.NET Core.
    /// </summary>
    public class HealthCheckBackgroundService : BackgroundService
    {
        private readonly IServiceScopeFactory _scopeFactory;
        private readonly ILogger<HealthCheckBackgroundService> _logger;

        // Intervalle entre deux vérifications (modifiable dans appsettings)
        private const int IntervalleSecondes = 60;

        public HealthCheckBackgroundService(
            IServiceScopeFactory scopeFactory,
            ILogger<HealthCheckBackgroundService> logger)
        {
            _scopeFactory = scopeFactory;
            _logger = logger;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _logger.LogInformation("HealthCheckBackgroundService démarré.");

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    // On crée un scope car les services EF Core sont "Scoped"
                    using var scope = _scopeFactory.CreateScope();
                    var serviceSurveille = scope.ServiceProvider
                        .GetRequiredService<IServiceSurveilleService>();

                    await serviceSurveille.VerifierTousLesServicesAsync();
                    _logger.LogInformation("Vérification santé des services effectuée à {Heure}", DateTime.UtcNow);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Erreur lors de la vérification santé des services.");
                }

                await Task.Delay(TimeSpan.FromSeconds(IntervalleSecondes), stoppingToken);
            }

            _logger.LogInformation("HealthCheckBackgroundService arrêté.");
        }
    }
}
