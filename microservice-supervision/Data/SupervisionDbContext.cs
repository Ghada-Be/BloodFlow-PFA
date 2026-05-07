using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;

namespace BloodFlow.MS3.Data;

public class SupervisionDbContext : DbContext
{
    public SupervisionDbContext(DbContextOptions<SupervisionDbContext> options) : base(options) { }

    public DbSet<SystemLog> SystemLogs => Set<SystemLog>();
    public DbSet<Alert> Alerts => Set<Alert>();
    public DbSet<Notification> Notifications => Set<Notification>();
    public DbSet<Report> Reports => Set<Report>();
    public DbSet<SupervisedService> SupervisedServices => Set<SupervisedService>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<SystemLog>(entity =>
        {
            entity.ToTable("SystemLogs");
            entity.HasKey(x => x.Id);
            entity.Property(x => x.ServiceName).HasMaxLength(120).IsRequired();
            entity.Property(x => x.Level).HasMaxLength(50).IsRequired();
            entity.Property(x => x.Message).HasMaxLength(1000).IsRequired();
            entity.Property(x => x.CreatedAt).IsRequired();
            entity.HasIndex(x => x.ServiceName);
            entity.HasIndex(x => x.CreatedAt);
        });

        modelBuilder.Entity<Alert>(entity =>
        {
            entity.ToTable("Alerts");
            entity.HasKey(x => x.Id);
            entity.Property(x => x.Title).HasMaxLength(200).IsRequired();
            entity.Property(x => x.Description).HasMaxLength(1000).IsRequired();
            entity.Property(x => x.Severity).HasMaxLength(30).IsRequired();
            entity.Property(x => x.SourceService).HasMaxLength(120).IsRequired();
            entity.HasIndex(x => x.CreatedAt);
        });

        modelBuilder.Entity<Notification>(entity =>
        {
            entity.ToTable("Notifications");
            entity.HasKey(x => x.Id);
            entity.Property(x => x.UserId).HasMaxLength(100).IsRequired();
            entity.Property(x => x.TargetRole).HasMaxLength(80);
            entity.Property(x => x.Title).HasMaxLength(200).IsRequired();
            entity.Property(x => x.Message).HasMaxLength(1000).IsRequired();
            entity.Property(x => x.Type).HasMaxLength(50).IsRequired();
            entity.Property(x => x.Priority).HasMaxLength(30).IsRequired();
            entity.HasIndex(x => x.UserId);
            entity.HasIndex(x => x.CreatedAt);
        });

        modelBuilder.Entity<Report>(entity =>
        {
            entity.ToTable("Reports");
            entity.HasKey(x => x.Id);
            entity.Property(x => x.ReportType).HasMaxLength(100).IsRequired();
            entity.Property(x => x.Content).IsRequired();
            entity.Property(x => x.Format).HasMaxLength(20).IsRequired();
        });

        modelBuilder.Entity<SupervisedService>(entity =>
        {
            entity.ToTable("SupervisedServices");
            entity.HasKey(x => x.Id);
            entity.Property(x => x.Name).HasMaxLength(120).IsRequired();
            entity.Property(x => x.BaseUrl).HasMaxLength(300).IsRequired();
            entity.Property(x => x.HealthEndpoint).HasMaxLength(200).IsRequired();
            entity.Property(x => x.Status).HasMaxLength(30).IsRequired();
        });

        modelBuilder.Entity<SupervisedService>().HasData(
            new SupervisedService { Id = 1, Name = "MS1 Auth", BaseUrl = "http://bloodflow-auth-service:8081", HealthEndpoint = "/api/health", Status = "Unknown" },
            new SupervisedService { Id = 2, Name = "MS2 Medical", BaseUrl = "http://bloodflow-medical-service:8082", HealthEndpoint = "/api/health", Status = "Unknown" }
        );
    }
}
