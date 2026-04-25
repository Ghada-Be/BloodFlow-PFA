using BloodFlow.MS1.Data;
using BloodFlow.MS1.Helpers;
using BloodFlow.MS1.Interfaces;
using BloodFlow.MS1.Middleware;
using BloodFlow.MS1.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// ═══════════════════════════════════════════════════════════════════
// 1. BASE DE DONNÉES — Entity Framework Core + SQL Server
// ═══════════════════════════════════════════════════════════════════

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

// ═══════════════════════════════════════════════════════════════════
// 2. INJECTION DE DÉPENDANCES — Services et Helpers
// ═══════════════════════════════════════════════════════════════════

builder.Services.AddSingleton<JwtHelper>();

builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IUserService, UserService>();

// ═══════════════════════════════════════════════════════════════════
// 3. AUTHENTIFICATION JWT BEARER
// ═══════════════════════════════════════════════════════════════════

var jwtSettings = builder.Configuration.GetSection("JwtSettings");
var secretKey = jwtSettings["SecretKey"]!;

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey)),

        ValidateIssuer = true,
        ValidIssuer = jwtSettings["Issuer"],

        ValidateAudience = true,
        ValidAudience = jwtSettings["Audience"],

        ValidateLifetime = true,
        ClockSkew = TimeSpan.Zero
    };

    options.Events = new JwtBearerEvents
    {
        OnChallenge = context =>
        {
            context.HandleResponse();
            context.Response.StatusCode = 401;
            context.Response.ContentType = "application/json";
            return context.Response.WriteAsync(
                "{\"message\":\"Authentification requise. Veuillez vous connecter.\"}");
        },
        OnForbidden = context =>
        {
            context.Response.StatusCode = 403;
            context.Response.ContentType = "application/json";
            return context.Response.WriteAsync(
                "{\"message\":\"Accès refusé. Vous n'avez pas les permissions nécessaires.\"}");
        }
    };
});

// ═══════════════════════════════════════════════════════════════════
// 4. AUTORISATION
// ═══════════════════════════════════════════════════════════════════

builder.Services.AddAuthorization();

// ═══════════════════════════════════════════════════════════════════
// 5. CONTRÔLEURS
// ═══════════════════════════════════════════════════════════════════

builder.Services.AddControllers();

// ═══════════════════════════════════════════════════════════════════
// 6. SWAGGER / OPENAPI — Documentation de l'API
// ═══════════════════════════════════════════════════════════════════

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "BloodFlow MS1 - Auth & Users API",
        Version = "v1",
        Description = "Microservice 1 : Authentification, Autorisation et Gestion des Utilisateurs\n\n" +
                      "Rôles disponibles : Admin, Donor, Patient, Doctor, Staff, " +
                      "LabTechnician, Biologist, DeliveryAgent, Promoter",
        Contact = new OpenApiContact
        {
            Name = "Équipe BloodFlow",
            Email = "contact@bloodflow.ma"
        }
    });

    options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Entrez : Bearer {votre_token_jwt}\n\nExemple: Bearer eyJhbGci..."
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

// ═══════════════════════════════════════════════════════════════════
// 7. CORS — Autoriser explicitement le front-end local
// ═══════════════════════════════════════════════════════════════════

builder.Services.AddCors(options =>
{
    options.AddPolicy("BloodFlowPolicy", policy =>
    {
        policy.WithOrigins(
                "http://localhost:3000",
                "https://localhost:3000",
                "http://localhost:5173",
                "https://localhost:5173"
              )
              .AllowAnyHeader()
              .AllowAnyMethod();
    });
});

// ═══════════════════════════════════════════════════════════════════
// BUILD DE L'APPLICATION
// ═══════════════════════════════════════════════════════════════════

var app = builder.Build();

// ═══════════════════════════════════════════════════════════════════
// 8. MIGRATIONS AUTOMATIQUES + SEED AU DÉMARRAGE
// ═══════════════════════════════════════════════════════════════════

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();

    db.Database.Migrate();

    await DbSeeder.SeedAdminAsync(db);
}

// ═══════════════════════════════════════════════════════════════════
// 9. PIPELINE DE MIDDLEWARES (ordre important !)
// ═══════════════════════════════════════════════════════════════════

app.UseMiddleware<ExceptionMiddleware>();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(options =>
    {
        options.SwaggerEndpoint("/swagger/v1/swagger.json", "BloodFlow MS1 v1");
        options.RoutePrefix = string.Empty;
        options.DocumentTitle = "BloodFlow MS1 - API Documentation";
    });
}

app.UseHttpsRedirection();

app.UseCors("BloodFlowPolicy");

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();