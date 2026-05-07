namespace BloodFlow.MS3.Services;

public static class CacheKeys
{
    public const string LogsAll = "logs:all";
    public const string AlertsAll = "alerts:all";
    public const string NotificationsAll = "notifications:all";
    public const string ReportsSummary = "reports:summary";
    public const string ServicesStatus = "services:status";

    public static string NotificationsByUser(string userId) => $"notifications:user:{userId}";
}
