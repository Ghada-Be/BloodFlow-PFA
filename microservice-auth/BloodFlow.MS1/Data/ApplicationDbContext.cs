using BloodFlow.MS1.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS1.Data
{
    /// <summary>
    /// Contexte Entity Framework Core pour la base de données MS1.
    /// Contient toutes les tables du microservice d'authentification.
    /// </summary>
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options) { }

        // ─────────── DbSets (Tables) ───────────
        public DbSet<User> Users { get; set; }
        public DbSet<Role> Roles { get; set; }
        public DbSet<UserRole> UserRoles { get; set; }
        public DbSet<RefreshToken> RefreshTokens { get; set; }
        public DbSet<LoginLog> LoginLogs { get; set; }
        public DbSet<PasswordResetToken> PasswordResetTokens { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // ─── User ───
            modelBuilder.Entity<User>(entity =>
            {
                entity.HasKey(u => u.Id);
                entity.HasIndex(u => u.Email).IsUnique(); // email unique
                entity.Property(u => u.Email).IsRequired().HasMaxLength(150);
                entity.Property(u => u.FirstName).IsRequired().HasMaxLength(100);
                entity.Property(u => u.LastName).IsRequired().HasMaxLength(100);
                entity.Property(u => u.PasswordHash).IsRequired();
                entity.Property(u => u.PhoneNumber).HasMaxLength(20);
            });

            // ─── Role ───
            modelBuilder.Entity<Role>(entity =>
            {
                entity.HasKey(r => r.Id);
                entity.HasIndex(r => r.Name).IsUnique();
                entity.Property(r => r.Name).IsRequired().HasMaxLength(50);
                entity.Property(r => r.Description).HasMaxLength(200);
            });

            // ─── UserRole (clé composite) ───
            modelBuilder.Entity<UserRole>(entity =>
            {
                entity.HasKey(ur => new { ur.UserId, ur.RoleId });

                entity.HasOne(ur => ur.User)
                      .WithMany(u => u.UserRoles)
                      .HasForeignKey(ur => ur.UserId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(ur => ur.Role)
                      .WithMany(r => r.UserRoles)
                      .HasForeignKey(ur => ur.RoleId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // ─── RefreshToken ───
            modelBuilder.Entity<RefreshToken>(entity =>
            {
                entity.HasKey(rt => rt.Id);
                entity.Property(rt => rt.Token).IsRequired().HasMaxLength(512);

                entity.HasOne(rt => rt.User)
                      .WithMany(u => u.RefreshTokens)
                      .HasForeignKey(rt => rt.UserId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // ─── LoginLog ───
            modelBuilder.Entity<LoginLog>(entity =>
            {
                entity.HasKey(l => l.Id);
                entity.Property(l => l.Email).IsRequired().HasMaxLength(150);
                entity.Property(l => l.IpAddress).HasMaxLength(45);
                entity.Property(l => l.FailureReason).HasMaxLength(200);

                entity.HasOne(l => l.User)
                      .WithMany(u => u.LoginLogs)
                      .HasForeignKey(l => l.UserId)
                      .IsRequired(false)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            // ─── PasswordResetToken ───
            modelBuilder.Entity<PasswordResetToken>(entity =>
            {
                entity.HasKey(p => p.Id);
                entity.Property(p => p.Token).IsRequired().HasMaxLength(512);

                entity.HasOne(p => p.User)
                      .WithMany(u => u.PasswordResetTokens)
                      .HasForeignKey(p => p.UserId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // ─── Seed des rôles du projet BloodFlow ───
            // Ces rôles sont insérés automatiquement via migration
            modelBuilder.Entity<Role>().HasData(
                new Role { Id = 1, Name = "Admin",         Description = "Administrateur du système" },
                new Role { Id = 2, Name = "Donor",         Description = "Donneur de sang" },
                new Role { Id = 3, Name = "Patient",       Description = "Patient receveur" },
                new Role { Id = 4, Name = "Doctor",        Description = "Médecin" },
                new Role { Id = 5, Name = "Staff",         Description = "Personnel hospitalier" },
                new Role { Id = 6, Name = "LabTechnician", Description = "Technicien de laboratoire" },
                new Role { Id = 7, Name = "Biologist",     Description = "Biologiste" },
                new Role { Id = 8, Name = "DeliveryAgent", Description = "Agent de livraison" },
                new Role { Id = 9, Name = "Promoter",      Description = "Promoteur de campagnes de don" }
            );
        }
    }
}
