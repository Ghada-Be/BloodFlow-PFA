using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;

namespace BloodFlow.MS3.Services;

public class SystemLogService
{
    private readonly SupervisionDbContext _context;
    private readonly IMemoryCache _cache;
    private readonly CacheHelper _cacheHelper;
    private readonly IConfiguration _configuration;

    public SystemLogService(SupervisionDbContext context, IMemoryCache cache, CacheHelper cacheHelper, IConfiguration configuration)
    {
        _context = context;
        _cache = cache;
        _cacheHelper = cacheHelper;
        _configuration = configuration;
    }

    public async Task<List<SystemLogDto>> GetAllAsync()
    {
        var cacheMinutes = _configuration.GetValue<int>("CacheSettings:SystemLogsExpirationMinutes", 3);

        if (_cache.TryGetValue(CacheKeys.LogsAll, out List<SystemLogDto>? cachedLogs) && cachedLogs is not null)
            return cachedLogs;

        var logs = await _context.SystemLogs
            .OrderByDescending(x => x.CreatedAt)
            .Take(100)
            .Select(x => new SystemLogDto
            {
                Id = x.Id,
                ServiceName = x.ServiceName,
                Level = x.Level,
                Message = x.Message,
                CreatedAt = x.CreatedAt
            })
            .ToListAsync();

        _cache.Set(CacheKeys.LogsAll, logs, _cacheHelper.Options(cacheMinutes));
        return logs;
    }

    public async Task<SystemLogDto> CreateAsync(SystemLogCreateDto dto)
    {
        var log = new SystemLog
        {
            ServiceName = dto.ServiceName.Trim(),
            Level = string.IsNullOrWhiteSpace(dto.Level) ? "Information" : dto.Level.Trim(),
            Message = dto.Message.Trim(),
            CreatedAt = DateTime.UtcNow
        };

        _context.SystemLogs.Add(log);
        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.LogsAll, CacheKeys.ReportsSummary);

        return new SystemLogDto
        {
            Id = log.Id,
            ServiceName = log.ServiceName,
            Level = log.Level,
            Message = log.Message,
            CreatedAt = log.CreatedAt
        };
    }
}
