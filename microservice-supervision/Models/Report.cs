namespace BloodFlow.MS3.Models;

public class Report
{
    public int Id { get; set; }
    public string ReportType { get; set; } = "Supervision";
    public string Content { get; set; } = string.Empty;
    public string Format { get; set; } = "JSON";
    public string? GeneratedByUserId { get; set; }
    public DateTime GeneratedAt { get; set; } = DateTime.UtcNow;
}
