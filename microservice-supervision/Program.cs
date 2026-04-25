using BloodFlow.MS3.Data;
using BloodFlow.MS3.Interfaces;
using BloodFlow.MS3.Middleware;
using BloodFlow.MS3.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// ─── 1. BASE DE DONNÉES ──────────────────────────────────────────────────────
// Connexion SQL Server locale avec Windows Authentication
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

// ─── 2. AUTHENTIFICATION JWT ─────────────────────────────────────────────────
// MS3 ne génère pas de tokens JWT. Il les VALIDE seulement.
// Les tokens sont émis par le Microservice 1 (authentification).
var jwtSettings = builder.Configuration.GetSection("JwtSettings");
var secretKey = jwtSettings["SecretKey"] ?? throw new InvalidOperationException("JWT SecretKey manquante.");

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = jwtSettings["Issuer"],
        ValidAudience = jwtSettings["Audience"],
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey))
    };
});

builder.Services.AddAuthorization();

// ─── 3. INJECTION DES SERVICES ───────────────────────────────────────────────
// JournalSystemeService en premier car les autres en dépendent
builder.Services.AddScoped<IJournalSystemeService, JournalSystemeService>();
builder.Services.AddScoped<AlerteService>(); // Concret pour ServiceSurveilleService
builder.Services.AddScoped<IAlerteService>(sp => sp.GetRequiredService<AlerteService>());
builder.Services.AddScoped<INotificationService, NotificationService>();
builder.Services.AddScoped<ICampagneService, CampagneService>();
builder.Services.AddScoped<ICollecteSangService, CollecteSangService>();
builder.Services.AddScoped<IBenevoleService, BenevoleService>();
builder.Services.AddScoped<IRapportService, RapportService>();
builder.Services.AddScoped<IAppelUrgentService, AppelUrgentService>();
builder.Services.AddScoped<IServiceSurveilleService, ServiceSurveilleService>();

// ─── 4. CLIENT HTTP pour vérification des services (health checks) ────────────
builder.Services.AddHttpClient("HealthCheck", client =>
{
    client.Timeout = TimeSpan.FromSeconds(10);
});

// ─── 5. BACKGROUND SERVICE (surveillance automatique toutes les 60s) ─────────
builder.Services.AddHostedService<HealthCheckBackgroundService>();

// ─── 6. CONTROLLERS + SWAGGER ────────────────────────────────────────────────
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Configuration Swagger avec support JWT
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "BloodFlow - Microservice 3 : Supervision & Coordination",
        Version = "v1",
        Description = "API de supervision, communication et coordination du système BloodFlow. " +
                      "Gère les alertes, notifications, campagnes, collectes, bénévoles et appels urgents.",
        Contact = new OpenApiContact
        {
            Name = "BloodFlow PFA",
            Email = "admin@bloodflow.ma"
        }
    });

    // Ajout du bouton "Authorize" dans Swagger pour tester avec JWT
    options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Entrez : Bearer {votre_token_jwt}\n\n" +
                      "Exemple : Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    });

    options.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });
});

// ─── 7. CORS (pour éventuellement brancher un front React plus tard) ─────────
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

// ─── 8. LOGGING ──────────────────────────────────────────────────────────────
builder.Logging.ClearProviders();
builder.Logging.AddConsole();
builder.Logging.AddDebug();

// ════════════════════════════════════════════════════════════════════════════
var app = builder.Build();
// ════════════════════════════════════════════════════════════════════════════

// ─── 9. MIGRATION AUTOMATIQUE (optionnel, pratique en développement) ─────────
// Crée la base de données et applique les migrations au démarrage si nécessaire.
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.Migrate();
}

// ─── 10. MIDDLEWARE PIPELINE ─────────────────────────────────────────────────

// Gestion globale des erreurs (doit être en premier)
app.UseMiddleware<ErrorHandlingMiddleware>();

// Swagger disponible en développement ET en production pour le PFA
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "BloodFlow MS3 v1");
    c.RoutePrefix = string.Empty; // Swagger accessible à la racine : http://localhost:5003/
    c.DocumentTitle = "BloodFlow MS3 - API Documentation";
});

app.UseHttpsRedirection();
app.UseCors("AllowAll");
app.UseAuthentication(); // Vérification du JWT
app.UseAuthorization();  // Vérification des rôles
app.MapControllers();

// ─── 11. ENDPOINT HEALTH CHECK de MS3 lui-même ───────────────────────────────
app.MapGet("/health", () => Results.Ok(new
{
    service = "BloodFlow.MS3",
    statut = "Disponible",
    timestamp = DateTime.UtcNow,
    version = "1.0.0"
})).AllowAnonymous();

app.Run();
