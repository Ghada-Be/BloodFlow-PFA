using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers
{
    // ═══════════════════════════════════════════════════════════════════════════
    // COLLECTES SANG CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Gestion des collectes de sang.
    /// </summary>
    [ApiController]
    [Route("api/collectes")]
    [Authorize]
    public class CollectesSangController : ControllerBase
    {
        private readonly ICollecteSangService _service;

        public CollectesSangController(ICollecteSangService service)
        {
            _service = service;
        }

        /// <summary>Récupère toutes les collectes, avec filtres optionnels.</summary>
        [HttpGet]
        public async Task<IActionResult> GetAll(
            [FromQuery] string? ville = null,
            [FromQuery] string? statut = null)
        {
            return Ok(await _service.GetAllAsync(ville, statut));
        }

        /// <summary>Récupère une collecte par ID.</summary>
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var c = await _service.GetByIdAsync(id);
            return c == null ? NotFound($"Collecte #{id} introuvable.") : Ok(c);
        }

        /// <summary>Crée une nouvelle collecte de sang.</summary>
        [HttpPost]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> Create([FromBody] CollecteSangCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var c = await _service.CreateAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = c.Id }, c);
        }

        /// <summary>Met à jour une collecte existante.</summary>
        [HttpPut("{id:int}")]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> Update(int id, [FromBody] CollecteSangUpdateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var c = await _service.UpdateAsync(id, dto);
            return c == null ? NotFound() : Ok(c);
        }

        /// <summary>Supprime une collecte.</summary>
        [HttpDelete("{id:int}")]
        [Authorize(Roles = "Administrateur")]
        public async Task<IActionResult> Delete(int id)
        {
            var ok = await _service.DeleteAsync(id);
            return ok ? NoContent() : NotFound();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BENEVOLES CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Gestion des bénévoles pour les collectes de sang.
    /// </summary>
    [ApiController]
    [Route("api/benevoles")]
    [Authorize]
    public class BenevolesController : ControllerBase
    {
        private readonly IBenevoleService _service;

        public BenevolesController(IBenevoleService service)
        {
            _service = service;
        }

        /// <summary>Récupère tous les bénévoles, filtrable par collecte.</summary>
        [HttpGet]
        public async Task<IActionResult> GetAll([FromQuery] int? collecteId = null)
        {
            return Ok(await _service.GetAllAsync(collecteId));
        }

        /// <summary>Récupère un bénévole par ID.</summary>
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var b = await _service.GetByIdAsync(id);
            return b == null ? NotFound() : Ok(b);
        }

        /// <summary>Inscrit un nouveau bénévole.</summary>
        [HttpPost]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> Create([FromBody] BenevoleCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var b = await _service.CreateAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = b.Id }, b);
        }

        /// <summary>Affecte un bénévole à une collecte.</summary>
        [HttpPatch("{id:int}/affecter/{collecteId:int}")]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> Affecter(int id, int collecteId)
        {
            var ok = await _service.AffecterACollecteAsync(id, collecteId);
            return ok ? NoContent() : NotFound("Bénévole ou collecte introuvable.");
        }

        /// <summary>Supprime un bénévole.</summary>
        [HttpDelete("{id:int}")]
        [Authorize(Roles = "AgentPromoteur,Administrateur")]
        public async Task<IActionResult> Delete(int id)
        {
            var ok = await _service.DeleteAsync(id);
            return ok ? NoContent() : NotFound();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // RAPPORTS CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Génération et consultation des rapports système.
    /// Réservé aux Administrateurs.
    /// </summary>
    [ApiController]
    [Route("api/rapports")]
    [Authorize(Roles = "Administrateur")]
    public class RapportsController : ControllerBase
    {
        private readonly IRapportService _service;

        public RapportsController(IRapportService service)
        {
            _service = service;
        }

        /// <summary>Récupère tous les rapports générés.</summary>
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            return Ok(await _service.GetAllAsync());
        }

        /// <summary>Récupère un rapport par ID.</summary>
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var r = await _service.GetByIdAsync(id);
            return r == null ? NotFound() : Ok(r);
        }

        /// <summary>
        /// Génère un rapport à la demande.
        /// Types disponibles : Global, Campagne, Collecte, Alertes, Système
        /// </summary>
        [HttpPost("generer")]
        public async Task<IActionResult> Generer([FromBody] RapportGenerateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var r = await _service.GenererAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = r.Id }, r);
        }
    }
}
