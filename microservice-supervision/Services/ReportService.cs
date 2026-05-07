using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;
using System.Text.Json;

namespace BloodFlow.MS3.Services;

public class ReportService
{
    private readonly SupervisionDbContext _context;
    private readonly IMemoryCache _cache;
    private readonly CacheHelper _cacheHelper;
    private readonly IConfiguration _configuration;

    public ReportService(SupervisionDbContext context, IMemoryCache cache, CacheHelper cacheHelper, IConfiguration configuration)
    {
        _context = context;
        _cache = cache;
        _cacheHelper = cacheHelper;
        _configuration = configuration;
    }

    public async Task<ReportSummaryDto> GetSummaryAsync()
    {
        var cacheMinutes = _configuration.GetValue<int>("CacheSettings:ReportsExpirationMinutes", 10);
        if (_cache.TryGetValue(CacheKeys.ReportsSummary, out ReportSummaryDto? cached) && cached is not null)
            return cached;

        var summary = new ReportSummaryDto
        {
            TotalLogs = await _context.SystemLogs.CountAsync(),
            TotalAlerts = await _context.Alerts.CountAsync(),
            CriticalAlerts = await _context.Alerts.CountAsync(x => x.Severity == "High" || x.Severity == "Critical"),
            TotalNotifications = await _context.Notifications.CountAsync(),
            UnreadNotifications = await _context.Notifications.CountAsync(x => !x.IsRead),
            ServicesUp = await _context.SupervisedServices.CountAsync(x => x.Status == "UP"),
            ServicesDown = await _context.SupervisedServices.CountAsync(x => x.Status == "DOWN"),
            GeneratedAt = DateTime.UtcNow
        };

        _cache.Set(CacheKeys.ReportsSummary, summary, _cacheHelper.Options(cacheMinutes));
        return summary;
    }

    public async Task<Report> GenerateReportAsync(ReportCreateDto dto)
    {
        var summary = await GetSummaryAsync();
        var content = JsonSerializer.Serialize(summary, new JsonSerializerOptions { WriteIndented = true });

        var report = new Report
        {
            ReportType = dto.ReportType,
            Content = content,
            Format = "JSON",
            GeneratedByUserId = dto.GeneratedByUserId,
            GeneratedAt = DateTime.UtcNow
        };

        _context.Reports.Add(report);
        _context.SystemLogs.Add(new SystemLog
        {
            ServiceName = "MS3",
            Level = "Information",
            Message = $"Rapport généré : {report.ReportType}",
            CreatedAt = DateTime.UtcNow
        });

        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.LogsAll);
        return report;
    }
}
