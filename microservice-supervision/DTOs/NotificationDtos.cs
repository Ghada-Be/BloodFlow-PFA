namespace BloodFlow.MS3.DTOs
{
    public class NotificationDto
    {
        public int Id { get; set; }
        public DateTime DateEnvoi { get; set; }
        public string Message { get; set; } = string.Empty;
        public string Type { get; set; } = string.Empty;
        public string Canal { get; set; } = string.Empty;
        public string StatutEnvoi { get; set; } = string.Empty;
        public string Destinataire { get; set; } = string.Empty;
        public int? AlerteId { get; set; }
        public int? CampagneId { get; set; }
    }

    public class NotificationCreateDto
    {
        public string Message { get; set; } = string.Empty;
        public string Type { get; set; } = "Information";
        public string Canal { get; set; } = "Email";
        public string Destinataire { get; set; } = string.Empty;
        public int? AlerteId { get; set; }
        public int? CampagneId { get; set; }
    }
}
