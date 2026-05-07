using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;
using System.Diagnostics;

namespace BloodFlow.MS3.Services;

public class SupervisionService
{
    private readonly SupervisionDbContext _context;
    private readonly IHttpClientFactory _httpClientFactory;
    private readonly IMemoryCache _cache;
    private readonly CacheHelper _cacheHelper;
    private readonly IConfiguration _configuration;

    public SupervisionService(
        SupervisionDbContext context,
        IHttpClientFactory httpClientFactory,
        IMemoryCache cache,
        CacheHelper cacheHelper,
        IConfiguration configuration)
    {
        _context = context;
        _httpClientFactory = httpClientFactory;
        _cache = cache;
        _cacheHelper = cacheHelper;
        _configuration = configuration;
    }

    public async Task<List<ServiceStatusDto>> CheckServicesAsync(bool forceRefresh = false)
    {
        var cacheSeconds = _configuration.GetValue<int>("CacheSettings:ServicesStatusExpirationSeconds", 30);

        if (!forceRefresh && _cache.TryGetValue(CacheKeys.ServicesStatus, out List<ServiceStatusDto>? cached) && cached is not null)
            return cached;

        var services = await _context.SupervisedServices.OrderBy(x => x.Name).ToListAsync();
        var client = _httpClientFactory.CreateClient("health-client");
        var results = new List<ServiceStatusDto>();

        foreach (var service in services)
        {
            var url = service.BaseUrl.TrimEnd('/') + "/" + service.HealthEndpoint.TrimStart('/');
            var watch = Stopwatch.StartNew();
            string status;
            string? error = null;
            int? latency = null;

            try
            {
                var response = await client.GetAsync(url);
                watch.Stop();
                latency = (int)watch.ElapsedMilliseconds;
                status = response.IsSuccessStatusCode ? "UP" : "DOWN";
                if (!response.IsSuccessStatusCode)
                    error = $"HTTP {(int)response.StatusCode}";
            }
            catch (Exception ex)
            {
                watch.Stop();
                status = "DOWN";
                error = ex.Message;
            }

            service.Status = status;
            service.LastCheckAt = DateTime.UtcNow;
            service.ResponseTimeMs = latency;

            results.Add(new ServiceStatusDto
            {
                Id = service.Id,
                Name = service.Name,
                Url = url,
                Status = status,
                ResponseTimeMs = latency,
                CheckedAt = DateTime.UtcNow,
                ErrorMessage = error
            });

            if (status == "DOWN")
            {
                _context.Alerts.Add(new Alert
                {
                    Title = "Service indisponible",
                    Description = $"Le service {service.Name} ne répond pas. Détail : {error}",
                    Severity = "High",
                    SourceService = service.Name,
                    CreatedAt = DateTime.UtcNow
                });

                _context.SystemLogs.Add(new SystemLog
                {
                    ServiceName = service.Name,
                    Level = "Error",
                    Message = $"Health check DOWN : {error}",
                    CreatedAt = DateTime.UtcNow
                });
            }
        }

        await _context.SaveChangesAsync();
        _cache.Set(CacheKeys.ServicesStatus, results, _cacheHelper.Options(seconds: cacheSeconds));
        _cacheHelper.Remove(CacheKeys.AlertsAll, CacheKeys.LogsAll, CacheKeys.ReportsSummary);
        return results;
    }

    public async Task<ServiceStatusDto> AddServiceAsync(SupervisedServiceCreateDto dto)
    {
        var service = new SupervisedService
        {
            Name = dto.Name.Trim(),
            BaseUrl = dto.BaseUrl.Trim(),
            HealthEndpoint = string.IsNullOrWhiteSpace(dto.HealthEndpoint) ? "/api/health" : dto.HealthEndpoint.Trim(),
            Status = "Unknown"
        };

        _context.SupervisedServices.Add(service);
        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.ServicesStatus, CacheKeys.ReportsSummary);

        return new ServiceStatusDto
        {
            Id = service.Id,
            Name = service.Name,
            Url = service.BaseUrl.TrimEnd('/') + "/" + service.HealthEndpoint.TrimStart('/'),
            Status = service.Status,
            CheckedAt = DateTime.UtcNow
        };
    }
}
