using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;

namespace BloodFlow.MS3.Services;

public class AlertService
{
    private readonly SupervisionDbContext _context;
    private readonly IMemoryCache _cache;
    private readonly CacheHelper _cacheHelper;
    private readonly IConfiguration _configuration;

    public AlertService(SupervisionDbContext context, IMemoryCache cache, CacheHelper cacheHelper, IConfiguration configuration)
    {
        _context = context;
        _cache = cache;
        _cacheHelper = cacheHelper;
        _configuration = configuration;
    }

    public async Task<List<AlertDto>> GetAllAsync()
    {
        var cacheMinutes = _configuration.GetValue<int>("CacheSettings:AlertsExpirationMinutes", 2);
        if (_cache.TryGetValue(CacheKeys.AlertsAll, out List<AlertDto>? cached) && cached is not null)
            return cached;

        var alerts = await _context.Alerts
            .OrderByDescending(x => x.CreatedAt)
            .Select(x => ToDto(x))
            .ToListAsync();

        _cache.Set(CacheKeys.AlertsAll, alerts, _cacheHelper.Options(cacheMinutes));
        return alerts;
    }

    public async Task<AlertDto?> GetByIdAsync(int id)
    {
        var alert = await _context.Alerts.FindAsync(id);
        return alert is null ? null : ToDto(alert);
    }

    public async Task<AlertDto> CreateAsync(AlertCreateDto dto)
    {
        var alert = new Alert
        {
            Title = dto.Title.Trim(),
            Description = dto.Description.Trim(),
            Severity = string.IsNullOrWhiteSpace(dto.Severity) ? "Medium" : dto.Severity.Trim(),
            SourceService = string.IsNullOrWhiteSpace(dto.SourceService) ? "MS3" : dto.SourceService.Trim(),
            CreatedAt = DateTime.UtcNow,
            IsRead = false
        };

        _context.Alerts.Add(alert);
        _context.SystemLogs.Add(new SystemLog
        {
            ServiceName = "MS3",
            Level = "Warning",
            Message = $"Alerte créée : {alert.Title}",
            CreatedAt = DateTime.UtcNow
        });

        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.AlertsAll, CacheKeys.LogsAll, CacheKeys.ReportsSummary);
        return ToDto(alert);
    }

    public async Task<bool> MarkAsReadAsync(int id)
    {
        var alert = await _context.Alerts.FindAsync(id);
        if (alert is null) return false;

        alert.IsRead = true;
        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.AlertsAll, CacheKeys.ReportsSummary);
        return true;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var alert = await _context.Alerts.FindAsync(id);
        if (alert is null) return false;

        _context.Alerts.Remove(alert);
        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.AlertsAll, CacheKeys.ReportsSummary);
        return true;
    }

    private static AlertDto ToDto(Alert alert) => new()
    {
        Id = alert.Id,
        Title = alert.Title,
        Description = alert.Description,
        Severity = alert.Severity,
        SourceService = alert.SourceService,
        IsRead = alert.IsRead,
        CreatedAt = alert.CreatedAt
    };
}
