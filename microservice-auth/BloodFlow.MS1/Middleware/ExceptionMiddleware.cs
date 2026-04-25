using System.Net;
using System.Text.Json;

namespace BloodFlow.MS1.Middleware
{
    /// <summary>
    /// Middleware global de gestion des exceptions non capturées.
    /// Intercepte toutes les exceptions et retourne un JSON propre au client.
    /// Évite d'exposer des détails techniques sensibles (stack trace, etc.).
    /// </summary>
    public class ExceptionMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<ExceptionMiddleware> _logger;

        public ExceptionMiddleware(RequestDelegate next, ILogger<ExceptionMiddleware> logger)
        {
            _next   = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (Exception ex)
            {
                // Journaliser l'erreur complète côté serveur
                _logger.LogError(ex, "Exception non gérée : {Message}", ex.Message);

                // Retourner une réponse générique au client (ne pas exposer les détails)
                await HandleExceptionAsync(context, ex);
            }
        }

        private static Task HandleExceptionAsync(HttpContext context, Exception exception)
        {
            context.Response.ContentType = "application/json";
            context.Response.StatusCode  = (int)HttpStatusCode.InternalServerError;

            var response = new
            {
                statusCode = 500,
                message    = "Une erreur interne s'est produite. Veuillez réessayer."
                // Ne jamais mettre exception.Message ou StackTrace ici en production
            };

            var json = JsonSerializer.Serialize(response);
            return context.Response.WriteAsync(json);
        }
    }
}
