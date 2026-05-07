namespace BloodFlow.MS3.DTOs;

public class SystemLogCreateDto
{
    public string ServiceName { get; set; } = string.Empty;
    public string Level { get; set; } = "Information";
    public string Message { get; set; } = string.Empty;
}

public class SystemLogDto
{
    public int Id { get; set; }
    public string ServiceName { get; set; } = string.Empty;
    public string Level { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; }
}

public class AlertCreateDto
{
    public string Title { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
    public string Severity { get; set; } = "Medium";
    public string SourceService { get; set; } = string.Empty;
}

public class AlertDto
{
    public int Id { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
    public string Severity { get; set; } = string.Empty;
    public string SourceService { get; set; } = string.Empty;
    public bool IsRead { get; set; }
    public DateTime CreatedAt { get; set; }
}

public class NotificationCreateDto
{
    public string UserId { get; set; } = string.Empty;
    public string? TargetRole { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public string Type { get; set; } = "Information";
    public string Priority { get; set; } = "Normal";
}

public class NotificationDto
{
    public int Id { get; set; }
    public string UserId { get; set; } = string.Empty;
    public string? TargetRole { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public string Type { get; set; } = string.Empty;
    public string Priority { get; set; } = string.Empty;
    public bool IsRead { get; set; }
    public DateTime CreatedAt { get; set; }
}

public class SupervisedServiceCreateDto
{
    public string Name { get; set; } = string.Empty;
    public string BaseUrl { get; set; } = string.Empty;
    public string HealthEndpoint { get; set; } = "/api/health";
}

public class ServiceStatusDto
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Url { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
    public int? ResponseTimeMs { get; set; }
    public DateTime CheckedAt { get; set; }
    public string? ErrorMessage { get; set; }
}

public class ReportSummaryDto
{
    public int TotalLogs { get; set; }
    public int TotalAlerts { get; set; }
    public int CriticalAlerts { get; set; }
    public int TotalNotifications { get; set; }
    public int UnreadNotifications { get; set; }
    public int ServicesUp { get; set; }
    public int ServicesDown { get; set; }
    public DateTime GeneratedAt { get; set; }
}

public class ReportCreateDto
{
    public string ReportType { get; set; } = "Supervision";
    public string? GeneratedByUserId { get; set; }
}
