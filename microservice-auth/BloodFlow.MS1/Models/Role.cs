namespace BloodFlow.MS1.Models
{
    /// <summary>
    /// Représente un rôle dans le système BloodFlow.
    /// Les rôles sont : Admin, Donor, Patient, Doctor, Staff,
    /// LabTechnician, Biologist, DeliveryAgent, Promoter.
    /// </summary>
    public class Role
    {
        public int Id { get; set; }

        // Nom unique du rôle (ex: "Admin", "Donor", etc.)
        public string Name { get; set; } = string.Empty;

        // Description optionnelle du rôle
        public string? Description { get; set; }

        // Navigation : les utilisateurs ayant ce rôle
        public ICollection<UserRole> UserRoles { get; set; } = new List<UserRole>();
    }
}
