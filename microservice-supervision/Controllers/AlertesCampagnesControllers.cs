using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers
{
    // ═══════════════════════════════════════════════════════════════════════════
    // ALERTES CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Gestion des alertes système.
    /// Seuls les Administrateurs peuvent créer et gérer les alertes.
    /// </summary>
    [ApiController]
    [Route("api/alertes")]
    [Authorize]
    public class AlertesController : ControllerBase
    {
        private readonly IAlerteService _service;

        public AlertesController(IAlerteService service)
        {
            _service = service;
        }

        /// <summary>Récupère toutes les alertes, avec filtres optionnels.</summary>
        [HttpGet]
        public async Task<IActionResult> GetAll(
            [FromQuery] string? etat = null,
            [FromQuery] string? niveau = null)
        {
            var alertes = await _service.GetAllAsync(etat, niveau);
            return Ok(alertes);
        }

        /// <summary>Récupère une alerte par son ID.</summary>
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var alerte = await _service.GetByIdAsync(id);
            return alerte == null ? NotFound($"Alerte #{id} introuvable.") : Ok(alerte);
        }

        /// <summary>Crée une nouvelle alerte manuellement.</summary>
        [HttpPost]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> Create([FromBody] AlerteCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var alerte = await _service.CreateAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = alerte.Id }, alerte);
        }

        /// <summary>Met à jour l'état d'une alerte (ex: Resolue).</summary>
        [HttpPatch("{id:int}/etat")]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> UpdateEtat(int id, [FromBody] AlerteUpdateEtatDto dto)
        {
            var ok = await _service.UpdateEtatAsync(id, dto.Etat);
            return ok ? NoContent() : NotFound($"Alerte #{id} introuvable.");
        }

        /// <summary>Supprime une alerte.</summary>
        [HttpDelete("{id:int}")]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> Delete(int id)
        {
            var ok = await _service.DeleteAsync(id);
            return ok ? NoContent() : NotFound($"Alerte #{id} introuvable.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NOTIFICATIONS CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Gestion des notifications envoyées aux utilisateurs et donneurs.
    /// </summary>
    [ApiController]
    [Route("api/notifications")]
    [Authorize]
    public class NotificationsController : ControllerBase
    {
        private readonly INotificationService _service;

        public NotificationsController(INotificationService service)
        {
            _service = service;
        }

        /// <summary>Récupère toutes les notifications.</summary>
        [HttpGet]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> GetAll()
        {
            return Ok(await _service.GetAllAsync());
        }

        /// <summary>Récupère une notification par ID.</summary>
        [HttpGet("{id:int}")]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> GetById(int id)
        {
            var n = await _service.GetByIdAsync(id);
            return n == null ? NotFound() : Ok(n);
        }

        /// <summary>Envoie une nouvelle notification.</summary>
        [HttpPost]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> Create([FromBody] NotificationCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var notif = await _service.CreateAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = notif.Id }, notif);
        }

        /// <summary>Marque une notification comme envoyée.</summary>
        [HttpPatch("{id:int}/marquer-envoyee")]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> MarquerEnvoyee(int id)
        {
            var ok = await _service.MarquerEnvoyeeAsync(id);
            return ok ? NoContent() : NotFound();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CAMPAGNES CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Gestion des campagnes de sensibilisation.
    /// Les agents promoteurs peuvent créer et gérer leurs campagnes.
    /// </summary>
    [ApiController]
    [Route("api/campagnes")]
    [Authorize]
    public class CampagnesController : ControllerBase
    {
        private readonly ICampagneService _service;

        public CampagnesController(ICampagneService service)
        {
            _service = service;
        }

        /// <summary>Récupère toutes les campagnes, avec filtre optionnel par statut.</summary>
        [HttpGet]
        public async Task<IActionResult> GetAll([FromQuery] string? statut = null)
        {
            return Ok(await _service.GetAllAsync(statut));
        }

        /// <summary>Récupère une campagne par ID.</summary>
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var c = await _service.GetByIdAsync(id);
            return c == null ? NotFound($"Campagne #{id} introuvable.") : Ok(c);
        }

        /// <summary>Crée une nouvelle campagne.</summary>
        [HttpPost]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> Create([FromBody] CampagneCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var c = await _service.CreateAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = c.Id }, c);
        }

        /// <summary>Met à jour une campagne existante.</summary>
        [HttpPut("{id:int}")]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> Update(int id, [FromBody] CampagneUpdateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var c = await _service.UpdateAsync(id, dto);
            return c == null ? NotFound() : Ok(c);
        }

        /// <summary>Change le statut d'une campagne.</summary>
        [HttpPatch("{id:int}/statut")]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> ChangerStatut(int id, [FromBody] string statut)
        {
            var ok = await _service.ChangerStatutAsync(id, statut);
            return ok ? NoContent() : NotFound();
        }

        /// <summary>Supprime une campagne.</summary>
        [HttpDelete("{id:int}")]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> Delete(int id)
        {
            var ok = await _service.DeleteAsync(id);
            return ok ? NoContent() : NotFound();
        }
    }
}
