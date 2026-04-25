namespace BloodFlow.MS1.Models
{
    /// <summary>
    /// Table de jonction entre User et Role.
    /// Relation Many-to-Many : un utilisateur peut avoir plusieurs rôles.
    /// </summary>
    public class UserRole
    {
        public int UserId { get; set; }
        public User User { get; set; } = null!;

        public int RoleId { get; set; }
        public Role Role { get; set; } = null!;

        // Date d'attribution du rôle
        public DateTime AssignedAt { get; set; } = DateTime.UtcNow;
    }
}
