using BloodFlow.MS3.Data;
using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Models;
using BloodFlow.MS3.Hubs;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;

namespace BloodFlow.MS3.Services;

public class NotificationService
{
    private readonly SupervisionDbContext _context;
    private readonly IMemoryCache _cache;
    private readonly CacheHelper _cacheHelper;
    private readonly IConfiguration _configuration;
    private readonly IHubContext<NotificationHub> _hubContext;

    public NotificationService(
        SupervisionDbContext context,
        IMemoryCache cache,
        CacheHelper cacheHelper,
        IConfiguration configuration,
        IHubContext<NotificationHub> hubContext)
    {
        _context = context;
        _cache = cache;
        _cacheHelper = cacheHelper;
        _configuration = configuration;
        _hubContext = hubContext;
    }

    public async Task<List<NotificationDto>> GetAllAsync()
    {
        var cacheMinutes = _configuration.GetValue<int>("CacheSettings:NotificationsExpirationMinutes", 2);
        if (_cache.TryGetValue(CacheKeys.NotificationsAll, out List<NotificationDto>? cached) && cached is not null)
            return cached;

        var items = await _context.Notifications
            .OrderByDescending(x => x.CreatedAt)
            .Select(x => ToDto(x))
            .ToListAsync();

        _cache.Set(CacheKeys.NotificationsAll, items, _cacheHelper.Options(cacheMinutes));
        return items;
    }

    public async Task<List<NotificationDto>> GetByUserAsync(string userId)
    {
        var key = CacheKeys.NotificationsByUser(userId);
        var cacheMinutes = _configuration.GetValue<int>("CacheSettings:NotificationsExpirationMinutes", 2);

        if (_cache.TryGetValue(key, out List<NotificationDto>? cached) && cached is not null)
            return cached;

        var items = await _context.Notifications
            .Where(x => x.UserId == userId)
            .OrderByDescending(x => x.CreatedAt)
            .Select(x => ToDto(x))
            .ToListAsync();

        _cache.Set(key, items, _cacheHelper.Options(cacheMinutes));
        return items;
    }

    public async Task<NotificationDto> CreateAsync(NotificationCreateDto dto)
    {
        var notification = new Notification
        {
            UserId = dto.UserId.Trim(),
            TargetRole = dto.TargetRole,
            Title = dto.Title.Trim(),
            Message = dto.Message.Trim(),
            Type = string.IsNullOrWhiteSpace(dto.Type) ? "Information" : dto.Type.Trim(),
            Priority = string.IsNullOrWhiteSpace(dto.Priority) ? "Normal" : dto.Priority.Trim(),
            CreatedAt = DateTime.UtcNow,
            IsRead = false
        };

        _context.Notifications.Add(notification);
        _context.SystemLogs.Add(new SystemLog
        {
            ServiceName = "MS3",
            Level = "Information",
            Message = $"Notification envoyée à {notification.UserId} : {notification.Title}",
            CreatedAt = DateTime.UtcNow
        });

        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.NotificationsAll, CacheKeys.NotificationsByUser(notification.UserId), CacheKeys.LogsAll, CacheKeys.ReportsSummary);

        var notificationDto = ToDto(notification);

        // Real-time push to the connected user.
        await _hubContext.Clients
            .Group(NotificationHub.UserGroup(notification.UserId))
            .SendAsync("ReceiveNotification", notificationDto);

        // Optional role-based push, useful for urgent alerts to all ADMIN/PROMOTER/etc. dashboards.
        if (!string.IsNullOrWhiteSpace(notification.TargetRole))
        {
            await _hubContext.Clients
                .Group(NotificationHub.RoleGroup(notification.TargetRole))
                .SendAsync("ReceiveNotification", notificationDto);
        }

        return notificationDto;
    }

    public async Task<bool> MarkAsReadAsync(int id)
    {
        var notification = await _context.Notifications.FindAsync(id);
        if (notification is null) return false;

        notification.IsRead = true;
        await _context.SaveChangesAsync();
        _cacheHelper.Remove(CacheKeys.NotificationsAll, CacheKeys.NotificationsByUser(notification.UserId), CacheKeys.ReportsSummary);
        return true;
    }

    private static NotificationDto ToDto(Notification notification) => new()
    {
        Id = notification.Id,
        UserId = notification.UserId,
        TargetRole = notification.TargetRole,
        Title = notification.Title,
        Message = notification.Message,
        Type = notification.Type,
        Priority = notification.Priority,
        IsRead = notification.IsRead,
        CreatedAt = notification.CreatedAt
    };
}
