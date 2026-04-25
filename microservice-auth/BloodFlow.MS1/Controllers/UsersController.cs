using BloodFlow.MS1.DTOs.Users;
using BloodFlow.MS1.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace BloodFlow.MS1.Controllers
{
    [ApiController]
    [Route("api/users")]
    [Authorize]
    [Produces("application/json")]
    public class UsersController : ControllerBase
    {
        private readonly IUserService _userService;

        public UsersController(IUserService userService)
        {
            _userService = userService;
        }

        // ─────────────────────────────────────────────
        // 🔴 CREATE STAFF (ADMIN ONLY)
        // POST /api/users/staff
        // ─────────────────────────────────────────────
        [HttpPost("staff")]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> CreateStaff([FromBody] CreateStaffUserDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var result = await _userService.CreateStaffAsync(dto);

            if (!result.Success)
                return BadRequest(new { message = result.Message });

            return Ok(result.Data);
        }

        // ─────────────────────────────────────────────
        // GET CURRENT USER
        // ─────────────────────────────────────────────
        [HttpGet("me")]
        public async Task<IActionResult> GetCurrentUser()
        {
            var userId = GetCurrentUserId();
            if (userId == 0) return Unauthorized();

            var user = await _userService.GetCurrentUserAsync(userId);
            if (user == null) return NotFound(new { message = "Utilisateur non trouvé." });

            return Ok(user);
        }

        // ─────────────────────────────────────────────
        // UPDATE PROFILE
        // ─────────────────────────────────────────────
        [HttpPut("me")]
        public async Task<IActionResult> UpdateProfile([FromBody] UpdateProfileDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);

            var userId = GetCurrentUserId();
            if (userId == 0) return Unauthorized();

            var (success, message) = await _userService.UpdateProfileAsync(userId, dto);

            if (!success) return BadRequest(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────
        // GET ALL USERS (ADMIN)
        // ─────────────────────────────────────────────
        [HttpGet]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> GetAllUsers([FromQuery] PaginationParams pagination)
        {
            var result = await _userService.GetAllUsersAsync(pagination);
            return Ok(result);
        }

        // ─────────────────────────────────────────────
        // GET USER BY ID
        // ─────────────────────────────────────────────
        [HttpGet("{id:int}")]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> GetUserById(int id)
        {
            var user = await _userService.GetUserByIdAsync(id);

            if (user == null)
                return NotFound(new { message = "Utilisateur non trouvé." });

            return Ok(user);
        }

        // ─────────────────────────────────────────────
        // ACTIVATE USER
        // ─────────────────────────────────────────────
        [HttpPatch("{id:int}/activate")]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> ActivateUser(int id)
        {
            var (success, message) = await _userService.SetUserActiveStatusAsync(id, true);

            if (!success)
                return NotFound(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────
        // DEACTIVATE USER
        // ─────────────────────────────────────────────
        [HttpPatch("{id:int}/deactivate")]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> DeactivateUser(int id)
        {
            var (success, message) = await _userService.SetUserActiveStatusAsync(id, false);

            if (!success)
                return BadRequest(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────
        // ASSIGN ROLE
        // ─────────────────────────────────────────────
        [HttpPost("{id:int}/roles")]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> AssignRole(int id, [FromBody] AssignRoleDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var (success, message) = await _userService.AssignRoleAsync(id, dto);

            if (!success)
                return BadRequest(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────
        // REMOVE ROLE
        // ─────────────────────────────────────────────
        [HttpDelete("{id:int}/roles/{roleName}")]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> RevokeRole(int id, string roleName)
        {
            var (success, message) = await _userService.RevokeRoleAsync(id, roleName);

            if (!success)
                return BadRequest(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────
        // GET ROLES
        // ─────────────────────────────────────────────
        [HttpGet("roles")]
        [Authorize(Roles = "Admin")]
        public IActionResult GetAvailableRoles()
        {
            var roles = new[]
            {
                "Admin", "Donor", "Patient", "Doctor",
                "Staff", "LabTechnician", "Biologist",
                "DeliveryAgent", "Promoter"
            };

            return Ok(roles);
        }

        // ─────────────────────────────────────────────
        // HELPER
        // ─────────────────────────────────────────────
        private int GetCurrentUserId()
        {
            var claim = User.FindFirst("userId")?.Value;
            return int.TryParse(claim, out var id) ? id : 0;
        }
    }
}