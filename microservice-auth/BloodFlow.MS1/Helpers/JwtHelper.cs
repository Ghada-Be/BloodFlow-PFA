using BloodFlow.MS1.Models;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;

namespace BloodFlow.MS1.Helpers
{
    /// <summary>
    /// Helper centralisant toute la logique JWT.
    /// Génère les access tokens et les refresh tokens.
    /// Séparé du service pour respecter le principe Single Responsibility.
    /// </summary>
    public class JwtHelper
    {
        private readonly IConfiguration _config;

        public JwtHelper(IConfiguration config)
        {
            _config = config;
        }

        /// <summary>
        /// Génère un Access Token JWT signé pour un utilisateur.
        /// Le payload contient : userId, email, rôles.
        /// Durée de vie courte (ex: 15 minutes).
        /// </summary>
        public string GenerateAccessToken(User user, List<string> roles)
        {
            var jwtSettings = _config.GetSection("JwtSettings");
            var secretKey   = jwtSettings["SecretKey"]!;
            var issuer      = jwtSettings["Issuer"]!;
            var audience    = jwtSettings["Audience"]!;
            var expiryMinutes = int.Parse(jwtSettings["AccessTokenExpiryMinutes"]!);

            // Clé de signature
            var key   = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            // Claims du token (ce qui sera lisible par les autres microservices)
            var claims = new List<Claim>
            {
                new Claim(JwtRegisteredClaimNames.Sub,   user.Id.ToString()),
                new Claim(JwtRegisteredClaimNames.Email, user.Email),
                new Claim(JwtRegisteredClaimNames.Jti,   Guid.NewGuid().ToString()),
                new Claim("userId",    user.Id.ToString()),
                new Claim("firstName", user.FirstName),
                new Claim("lastName",  user.LastName),
            };

            // Ajouter chaque rôle comme claim séparé
            // MS2 et MS3 liront ces claims pour l'autorisation
            foreach (var role in roles)
            {
                claims.Add(new Claim(ClaimTypes.Role, role));
            }

            // Construction du token
            var token = new JwtSecurityToken(
                issuer:             issuer,
                audience:           audience,
                claims:             claims,
                expires:            DateTime.UtcNow.AddMinutes(expiryMinutes),
                signingCredentials: creds
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        /// <summary>
        /// Génère un Refresh Token : chaîne aléatoire sécurisée de 64 bytes.
        /// Stocké en base de données, longue durée de vie (ex: 7 jours).
        /// </summary>
        public string GenerateRefreshToken()
        {
            var randomBytes = new byte[64];
            using var rng = RandomNumberGenerator.Create();
            rng.GetBytes(randomBytes);
            return Convert.ToBase64String(randomBytes);
        }

        /// <summary>
        /// Retourne la date d'expiration de l'access token.
        /// </summary>
        public DateTime GetAccessTokenExpiry()
        {
            var expiryMinutes = int.Parse(_config["JwtSettings:AccessTokenExpiryMinutes"]!);
            return DateTime.UtcNow.AddMinutes(expiryMinutes);
        }

        /// <summary>
        /// Retourne la date d'expiration du refresh token.
        /// </summary>
        public DateTime GetRefreshTokenExpiry()
        {
            var expiryDays = int.Parse(_config["JwtSettings:RefreshTokenExpiryDays"]!);
            return DateTime.UtcNow.AddDays(expiryDays);
        }

        /// <summary>
        /// Extrait le principal (claims) d'un token expiré.
        /// Utilisé lors du refresh pour récupérer l'userId sans revalider l'expiration.
        /// </summary>
        public ClaimsPrincipal? GetPrincipalFromExpiredToken(string token)
        {
            var jwtSettings = _config.GetSection("JwtSettings");
            var secretKey   = jwtSettings["SecretKey"]!;

            var validationParams = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey         = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey)),
                ValidateIssuer           = true,
                ValidIssuer              = jwtSettings["Issuer"],
                ValidateAudience         = true,
                ValidAudience            = jwtSettings["Audience"],
                // On ignore l'expiration ici, on veut juste les claims
                ValidateLifetime         = false
            };

            try
            {
                var handler   = new JwtSecurityTokenHandler();
                var principal = handler.ValidateToken(token, validationParams, out var validatedToken);

                // Vérifier que c'est bien du HmacSha256
                if (validatedToken is not JwtSecurityToken jwtToken ||
                    !jwtToken.Header.Alg.Equals(SecurityAlgorithms.HmacSha256, StringComparison.InvariantCultureIgnoreCase))
                    return null;

                return principal;
            }
            catch
            {
                return null;
            }
        }
    }
}
