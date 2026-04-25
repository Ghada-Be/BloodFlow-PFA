using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS3.Data
{
    /// <summary>
    /// Contexte EF Core principal du Microservice 3.
    /// Il représente la base de données MS3 sur SQL Server.
    /// </summary>
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        // Tables de la base de données
        public DbSet<Administrateur> Administrateurs { get; set; }
        public DbSet<AgentPromoteur> AgentsPromoteurs { get; set; }
        public DbSet<ServiceSurveille> ServicesSurveilles { get; set; }
        public DbSet<Rapport> Rapports { get; set; }
        public DbSet<JournalSysteme> JournalSysteme { get; set; }
        public DbSet<Alerte> Alertes { get; set; }
        public DbSet<Notification> Notifications { get; set; }
        public DbSet<Campagne> Campagnes { get; set; }
        public DbSet<CollecteSang> CollectesSang { get; set; }
        public DbSet<Benevole> Benevoles { get; set; }
        public DbSet<AppelUrgent> AppelsUrgents { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // ─── Administrateur ───────────────────────────────────────────
            modelBuilder.Entity<Administrateur>(entity =>
            {
                entity.HasKey(a => a.Id);
                entity.Property(a => a.Email).HasMaxLength(200).IsRequired();
                entity.Property(a => a.Nom).HasMaxLength(100).IsRequired();
                entity.Property(a => a.Prenom).HasMaxLength(100).IsRequired();
                entity.Property(a => a.UserIdMs1).HasMaxLength(100).IsRequired();
                entity.HasIndex(a => a.Email).IsUnique();
            });

            // ─── AgentPromoteur ───────────────────────────────────────────
            modelBuilder.Entity<AgentPromoteur>(entity =>
            {
                entity.HasKey(a => a.Id);
                entity.Property(a => a.Email).HasMaxLength(200).IsRequired();
                entity.Property(a => a.Nom).HasMaxLength(100).IsRequired();
                entity.Property(a => a.Prenom).HasMaxLength(100).IsRequired();
                entity.Property(a => a.UserIdMs1).HasMaxLength(100).IsRequired();
                entity.HasIndex(a => a.Email).IsUnique();
            });

            // ─── ServiceSurveille ─────────────────────────────────────────
            modelBuilder.Entity<ServiceSurveille>(entity =>
            {
                entity.HasKey(s => s.Id);
                entity.Property(s => s.NomService).HasMaxLength(150).IsRequired();
                entity.Property(s => s.UrlHealthCheck).HasMaxLength(500).IsRequired();
                entity.Property(s => s.Etat).HasMaxLength(50).HasDefaultValue("Inconnu");
            });

            // ─── Rapport ──────────────────────────────────────────────────
            modelBuilder.Entity<Rapport>(entity =>
            {
                entity.HasKey(r => r.Id);
                entity.Property(r => r.Type).HasMaxLength(100).IsRequired();
                entity.Property(r => r.Format).HasMaxLength(20).HasDefaultValue("JSON");
                entity.HasOne(r => r.CreeParAdmin)
                      .WithMany(a => a.Rapports)
                      .HasForeignKey(r => r.CreeParAdminId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            // ─── JournalSysteme ───────────────────────────────────────────
            modelBuilder.Entity<JournalSysteme>(entity =>
            {
                entity.HasKey(j => j.Id);
                entity.Property(j => j.Niveau).HasMaxLength(50).IsRequired();
                entity.Property(j => j.Source).HasMaxLength(200).IsRequired();
                entity.Property(j => j.Message).IsRequired();
            });

            // ─── Alerte ───────────────────────────────────────────────────
            modelBuilder.Entity<Alerte>(entity =>
            {
                entity.HasKey(a => a.Id);
                entity.Property(a => a.NiveauUrgence).HasMaxLength(50).IsRequired();
                entity.Property(a => a.Titre).HasMaxLength(200).IsRequired();
                entity.Property(a => a.Etat).HasMaxLength(50).HasDefaultValue("Ouverte");
                entity.HasOne(a => a.ServiceSurveille)
                      .WithMany(s => s.Alertes)
                      .HasForeignKey(a => a.ServiceSurveilleId)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            // ─── Notification ─────────────────────────────────────────────
            modelBuilder.Entity<Notification>(entity =>
            {
                entity.HasKey(n => n.Id);
                entity.Property(n => n.Type).HasMaxLength(50).IsRequired();
                entity.Property(n => n.Canal).HasMaxLength(50).IsRequired();
                entity.Property(n => n.StatutEnvoi).HasMaxLength(50).HasDefaultValue("EnAttente");
                entity.Property(n => n.Destinataire).HasMaxLength(300).IsRequired();
                entity.HasOne(n => n.Alerte)
                      .WithMany(a => a.Notifications)
                      .HasForeignKey(n => n.AlerteId)
                      .OnDelete(DeleteBehavior.SetNull);
                entity.HasOne(n => n.Campagne)
                      .WithMany(c => c.Notifications)
                      .HasForeignKey(n => n.CampagneId)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            // ─── Campagne ─────────────────────────────────────────────────
            modelBuilder.Entity<Campagne>(entity =>
            {
                entity.HasKey(c => c.Id);
                entity.Property(c => c.Titre).HasMaxLength(200).IsRequired();
                entity.Property(c => c.Statut).HasMaxLength(50).HasDefaultValue("Brouillon");
                entity.HasOne(c => c.AgentPromoteur)
                      .WithMany(a => a.Campagnes)
                      .HasForeignKey(c => c.AgentPromoteurId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            // ─── CollecteSang ─────────────────────────────────────────────
            modelBuilder.Entity<CollecteSang>(entity =>
            {
                entity.HasKey(c => c.Id);
                entity.Property(c => c.Lieu).HasMaxLength(300).IsRequired();
                entity.Property(c => c.Ville).HasMaxLength(100).IsRequired();
                entity.Property(c => c.Statut).HasMaxLength(50).HasDefaultValue("Planifiee");
                entity.HasOne(c => c.Campagne)
                      .WithMany(ca => ca.Collectes)
                      .HasForeignKey(c => c.CampagneId)
                      .OnDelete(DeleteBehavior.SetNull);
                entity.HasOne(c => c.AgentPromoteur)
                      .WithMany(a => a.Collectes)
                      .HasForeignKey(c => c.AgentPromoteurId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            // ─── Benevole ─────────────────────────────────────────────────
            modelBuilder.Entity<Benevole>(entity =>
            {
                entity.HasKey(b => b.Id);
                entity.Property(b => b.Nom).HasMaxLength(100).IsRequired();
                entity.Property(b => b.Prenom).HasMaxLength(100).IsRequired();
                entity.Property(b => b.Email).HasMaxLength(200);
                entity.HasOne(b => b.CollecteSang)
                      .WithMany(c => c.Benevoles)
                      .HasForeignKey(b => b.CollecteSangId)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            // ─── AppelUrgent ──────────────────────────────────────────────
            modelBuilder.Entity<AppelUrgent>(entity =>
            {
                entity.HasKey(a => a.Id);
                entity.Property(a => a.GroupeSanguin).HasMaxLength(10).IsRequired();
                entity.Property(a => a.Ville).HasMaxLength(100).IsRequired();
                entity.Property(a => a.Priorite).HasMaxLength(50).HasDefaultValue("Haute");
            });

            // ─── Seed Data ────────────────────────────────────────────────
            SeedData(modelBuilder);
        }

        private void SeedData(ModelBuilder modelBuilder)
        {
            // Admin de démonstration
            modelBuilder.Entity<Administrateur>().HasData(new Administrateur
            {
                Id = 1,
                Nom = "Benali",
                Prenom = "Fatima",
                Email = "admin@bloodflow.ma",
                Actif = true,
                UserIdMs1 = "user-ms1-001",
                DateCreation = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc)
            });

            // Agent promoteur de démonstration
            modelBuilder.Entity<AgentPromoteur>().HasData(new AgentPromoteur
            {
                Id = 1,
                Nom = "Idrissi",
                Prenom = "Youssef",
                Email = "agent@bloodflow.ma",
                Actif = true,
                UserIdMs1 = "user-ms1-002",
                DateCreation = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc)
            });

            // Services surveillés
            modelBuilder.Entity<ServiceSurveille>().HasData(
                new ServiceSurveille
                {
                    Id = 1,
                    NomService = "Microservice 1 - Utilisateurs",
                    UrlHealthCheck = "http://localhost:5001/health",
                    Etat = "Disponible",
                    DateDerniereVerification = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                    DerniereLatenceMs = 45,
                    MessageEtat = "Service opérationnel"
                },
                new ServiceSurveille
                {
                    Id = 2,
                    NomService = "Microservice 2 - Médical",
                    UrlHealthCheck = "http://localhost:5002/health",
                    Etat = "Disponible",
                    DateDerniereVerification = new DateTime(2024, 1, 1, 0, 0, 0, DateTimeKind.Utc),
                    DerniereLatenceMs = 60,
                    MessageEtat = "Service opérationnel"
                }
            );

            // Alerte de démonstration
            modelBuilder.Entity<Alerte>().HasData(new Alerte
            {
                Id = 1,
                DateAlerte = new DateTime(2024, 1, 15, 10, 0, 0, DateTimeKind.Utc),
                NiveauUrgence = "Élevé",
                Titre = "Stock de sang O- critique",
                Description = "Le stock de sang de groupe O- est descendu sous le seuil critique.",
                Etat = "Ouverte",
                ServiceSurveilleId = 2,
                CreeeParSysteme = true
            });

            // Campagne de démonstration
            modelBuilder.Entity<Campagne>().HasData(new Campagne
            {
                Id = 1,
                Titre = "Campagne Ramadan 2024",
                Objectif = "Collecter 200 poches de sang pendant Ramadan",
                Description = "Campagne nationale de sensibilisation au don de sang pendant le mois sacré.",
                DateDebut = new DateTime(2024, 3, 11, 0, 0, 0, DateTimeKind.Utc),
                DateFin = new DateTime(2024, 4, 9, 0, 0, 0, DateTimeKind.Utc),
                Statut = "Terminee",
                AgentPromoteurId = 1
            });

            // Collecte de démonstration
            modelBuilder.Entity<CollecteSang>().HasData(new CollecteSang
            {
                Id = 1,
                Lieu = "Faculté des Sciences - Amphithéâtre A",
                Ville = "Oujda",
                DateCollecte = new DateTime(2024, 3, 20, 0, 0, 0, DateTimeKind.Utc),
                HeureDebut = new TimeSpan(9, 0, 0),
                HeureFin = new TimeSpan(17, 0, 0),
                ObjectifPoches = 50,
                Statut = "Terminee",
                CampagneId = 1,
                AgentPromoteurId = 1
            });

            // Notification de démonstration
            modelBuilder.Entity<Notification>().HasData(new Notification
            {
                Id = 1,
                DateEnvoi = new DateTime(2024, 1, 15, 11, 0, 0, DateTimeKind.Utc),
                Message = "URGENT : Stock O- critique. Donneurs O- : veuillez vous présenter dès que possible.",
                Type = "Alerte",
                Canal = "Email",
                StatutEnvoi = "Envoyee",
                Destinataire = "donneurs-o-negatif@bloodflow.ma",
                AlerteId = 1,
                CampagneId = null
            });
        }
    }
}
