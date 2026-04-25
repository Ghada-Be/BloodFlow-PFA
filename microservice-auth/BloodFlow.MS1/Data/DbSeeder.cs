using BloodFlow.MS1.Data;
using BloodFlow.MS1.Models;

namespace BloodFlow.MS1.Data
{
    /// <summary>
    /// Crée automatiquement le compte Admin initial au démarrage si il n'existe pas.
    /// Appelé depuis Program.cs lors du lancement de l'application.
    /// </summary>
    public static class DbSeeder
    {
        public static async Task SeedAdminAsync(ApplicationDbContext context)
        {
            // Vérifier si un admin existe déjà
            if (context.Users.Any(u => u.UserRoles.Any(ur => ur.Role.Name == "Admin")))
                return;

            // Récupérer le rôle Admin (déjà seedé via HasData)
            var adminRole = context.Roles.FirstOrDefault(r => r.Name == "Admin");
            if (adminRole == null) return;

            // Créer l'utilisateur Admin initial
            var adminUser = new User
            {
                FirstName    = "Admin",
                LastName     = "BloodFlow",
                Email        = "admin@bloodflow.ma",
                // Mot de passe hashé : "Admin@1234"
                PasswordHash = BCrypt.Net.BCrypt.HashPassword("Admin@1234"),
                IsActive     = true,
                IsEmailVerified = true,
                CreatedAt    = DateTime.UtcNow
            };

            context.Users.Add(adminUser);
            await context.SaveChangesAsync();

            // Attribuer le rôle Admin
            var userRole = new UserRole
            {
                UserId     = adminUser.Id,
                RoleId     = adminRole.Id,
                AssignedAt = DateTime.UtcNow
            };

            context.UserRoles.Add(userRole);
            await context.SaveChangesAsync();

            Console.WriteLine("✅ Compte Admin initial créé : admin@bloodflow.ma / Admin@1234");
        }
    }
}
