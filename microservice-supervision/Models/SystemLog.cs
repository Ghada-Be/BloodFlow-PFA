namespace BloodFlow.MS3.Models;

public class SystemLog
{
    public int Id { get; set; }
    public string ServiceName { get; set; } = string.Empty;
    public string Level { get; set; } = "Information";
    public string Message { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
