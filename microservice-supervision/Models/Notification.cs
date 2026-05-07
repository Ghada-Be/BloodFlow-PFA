namespace BloodFlow.MS3.Models;

public class Notification
{
    public int Id { get; set; }
    public string UserId { get; set; } = string.Empty;
    public string? TargetRole { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public string Type { get; set; } = "Information";
    public string Priority { get; set; } = "Normal";
    public bool IsRead { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
