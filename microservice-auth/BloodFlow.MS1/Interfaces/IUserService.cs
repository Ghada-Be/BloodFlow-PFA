using BloodFlow.MS1.DTOs.Users;

namespace BloodFlow.MS1.Interfaces
{
    /// <summary>
    /// Interface du service de gestion des utilisateurs.
    /// Couvre : consultation, modification profil, activation/désactivation.
    /// </summary>
    public interface IUserService
    {
        /// <summary>Récupère la liste paginée des utilisateurs (Admin).</summary>
        Task<PagedResult<UserDto>> GetAllUsersAsync(PaginationParams pagination);

        /// <summary>Récupère un utilisateur par son Id.</summary>
        Task<UserDto?> GetUserByIdAsync(int userId);

        /// <summary>Récupère le profil de l'utilisateur connecté.</summary>
        Task<UserDto?> GetCurrentUserAsync(int userId);

        /// <summary>Met à jour le profil de l'utilisateur connecté.</summary>
        Task<(bool Success, string Message)> UpdateProfileAsync(int userId, UpdateProfileDto dto);

        /// <summary>Active ou désactive un compte utilisateur (Admin).</summary>
        Task<(bool Success, string Message)> SetUserActiveStatusAsync(int userId, bool isActive);

        /// <summary>Attribue un rôle à un utilisateur (Admin).</summary>
        Task<(bool Success, string Message)> AssignRoleAsync(int userId, AssignRoleDto dto);

        /// <summary>Révoque un rôle d'un utilisateur (Admin).</summary>
        Task<(bool Success, string Message)> RevokeRoleAsync(int userId, string roleName);
    }
}
