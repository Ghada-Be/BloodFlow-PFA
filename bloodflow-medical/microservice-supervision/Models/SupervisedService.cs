namespace BloodFlow.MS3.Models;

public class SupervisedService
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string BaseUrl { get; set; } = string.Empty;
    public string HealthEndpoint { get; set; } = "/api/health";
    public string Status { get; set; } = "Unknown";
    public DateTime? LastCheckAt { get; set; }
    public int? ResponseTimeMs { get; set; }
}
