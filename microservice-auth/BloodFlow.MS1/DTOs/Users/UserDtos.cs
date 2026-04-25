using System.ComponentModel.DataAnnotations;

namespace BloodFlow.MS1.DTOs.Users
{
    // ─────────────────────────────────────────────────────────────────
    // DTO : Profil utilisateur (retourné au client)
    // ─────────────────────────────────────────────────────────────────

    /// <summary>
    /// Représentation d'un utilisateur renvoyée par l'API.
    /// On n'expose jamais le PasswordHash au client.
    /// </summary>
    public class UserDto
    {
        public int Id { get; set; }
        public string FirstName { get; set; } = string.Empty;
        public string LastName { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string? PhoneNumber { get; set; }
        public bool IsActive { get; set; }
        public bool IsEmailVerified { get; set; }
        public DateTime CreatedAt { get; set; }
        public List<string> Roles { get; set; } = new();
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Mise à jour du profil
    // ─────────────────────────────────────────────────────────────────

    /// <summary>
    /// Données que l'utilisateur peut modifier sur son profil.
    /// L'email et le mot de passe sont gérés séparément pour la sécurité.
    /// </summary>
    public class UpdateProfileDto
    {
        [Required]
        [StringLength(100)]
        public string FirstName { get; set; } = string.Empty;

        [Required]
        [StringLength(100)]
        public string LastName { get; set; } = string.Empty;

        [Phone]
        public string? PhoneNumber { get; set; }
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Attribution d'un rôle
    // ─────────────────────────────────────────────────────────────────

    /// <summary>
    /// Données envoyées par l'admin pour attribuer un rôle à un utilisateur.
    /// </summary>
    public class AssignRoleDto
    {
        [Required(ErrorMessage = "Le nom du rôle est obligatoire")]
        public string RoleName { get; set; } = string.Empty;
    }

    // ─────────────────────────────────────────────────────────────────
    // DTO : Liste paginée (simple, sans librairie externe)
    // ─────────────────────────────────────────────────────────────────

    /// <summary>
    /// Paramètres de pagination pour la liste des utilisateurs.
    /// </summary>
    public class PaginationParams
    {
        public int Page { get; set; } = 1;
        public int PageSize { get; set; } = 10;
        public string? Search { get; set; }
    }

    /// <summary>
    /// Résultat paginé.
    /// </summary>
    public class PagedResult<T>
    {
        public List<T> Data { get; set; } = new();
        public int TotalCount { get; set; }
        public int Page { get; set; }
        public int PageSize { get; set; }
        public int TotalPages => (int)Math.Ceiling((double)TotalCount / PageSize);
    }
}
