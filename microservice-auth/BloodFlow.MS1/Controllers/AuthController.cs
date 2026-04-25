using BloodFlow.MS1.DTOs.Auth;
using BloodFlow.MS1.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace BloodFlow.MS1.Controllers
{
    /// <summary>
    /// Contrôleur d'authentification.
    /// Endpoints : inscription, connexion, refresh, déconnexion,
    /// changement de mot de passe, mot de passe oublié / reset.
    ///
    /// Le contrôleur ne contient PAS de logique métier :
    /// il délègue tout à IAuthService.
    /// </summary>
    [ApiController]
    [Route("api/auth")]
    [Produces("application/json")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/auth/register
        // ─────────────────────────────────────────────────────────────────

        /// <summary>Inscrit un nouvel utilisateur dans le système.</summary>
        [HttpPost("register")]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status409Conflict)]
        public async Task<IActionResult> Register([FromBody] RegisterDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var ip = GetClientIp();
            var (success, message) = await _authService.RegisterAsync(dto, ip);

            if (!success)
            {
                // Email déjà utilisé → 409 Conflict
                if (message.Contains("existe déjà"))
                    return Conflict(new { message });

                return BadRequest(new { message });
            }

            return StatusCode(201, new { message });
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/auth/login
        // ─────────────────────────────────────────────────────────────────

        /// <summary>Authentifie un utilisateur et retourne les tokens JWT.</summary>
        [HttpPost("login")]
        [ProducesResponseType(typeof(TokenResponseDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<IActionResult> Login([FromBody] LoginDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var ip = GetClientIp();
            var (success, message, token) = await _authService.LoginAsync(dto, ip);

            if (!success)
                return Unauthorized(new { message });

            return Ok(token);
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/auth/refresh-token
        // ─────────────────────────────────────────────────────────────────

        /// <summary>Renouvelle l'access token via le refresh token.</summary>
        [HttpPost("refresh-token")]
        [ProducesResponseType(typeof(TokenResponseDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<IActionResult> RefreshToken([FromBody] RefreshTokenRequestDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var ip = GetClientIp();
            var (success, message, token) = await _authService.RefreshTokenAsync(dto.RefreshToken, ip);

            if (!success)
                return Unauthorized(new { message });

            return Ok(token);
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/auth/logout
        // ─────────────────────────────────────────────────────────────────

        /// <summary>Déconnecte l'utilisateur en révoquant son refresh token.</summary>
        [HttpPost("logout")]
        [Authorize] // Nécessite d'être authentifié
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<IActionResult> Logout([FromBody] LogoutDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var ip = GetClientIp();
            var (success, message) = await _authService.LogoutAsync(dto.RefreshToken, ip);

            if (!success)
                return BadRequest(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/auth/change-password
        // ─────────────────────────────────────────────────────────────────

        /// <summary>Change le mot de passe de l'utilisateur connecté.</summary>
        [HttpPost("change-password")]
        [Authorize]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var userId = GetCurrentUserId();
            if (userId == 0)
                return Unauthorized();

            var (success, message) = await _authService.ChangePasswordAsync(userId, dto);

            if (!success)
                return BadRequest(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/auth/forgot-password
        // ─────────────────────────────────────────────────────────────────

        /// <summary>
        /// Demande de réinitialisation du mot de passe.
        /// Dans un vrai projet, envoie un email. Ici, retourne le token pour les tests.
        /// </summary>
        [HttpPost("forgot-password")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async Task<IActionResult> ForgotPassword([FromBody] ForgotPasswordDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var (success, message, resetToken) = await _authService.ForgotPasswordAsync(dto);

            // On retourne toujours 200 même si l'email n'existe pas (sécurité)
            return Ok(new
            {
                message,
                // En production, ne PAS retourner le token ici — l'envoyer par email
                // Pour le PFA / tests, on le retourne dans la réponse
                resetToken
            });
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/auth/reset-password
        // ─────────────────────────────────────────────────────────────────

        /// <summary>Réinitialise le mot de passe avec le token reçu.</summary>
        [HttpPost("reset-password")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<IActionResult> ResetPassword([FromBody] ResetPasswordDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var (success, message) = await _authService.ResetPasswordAsync(dto);

            if (!success)
                return BadRequest(new { message });

            return Ok(new { message });
        }

        // ─────────────────────────────────────────────────────────────────
        // Helpers privés
        // ─────────────────────────────────────────────────────────────────

        /// <summary>Extrait l'userId depuis le JWT (claim "userId").</summary>
        private int GetCurrentUserId()
        {
            var claim = User.FindFirst("userId")?.Value;
            return int.TryParse(claim, out var id) ? id : 0;
        }

        /// <summary>Récupère l'adresse IP du client.</summary>
        private string? GetClientIp()
            => HttpContext.Connection.RemoteIpAddress?.ToString();
    }
}
