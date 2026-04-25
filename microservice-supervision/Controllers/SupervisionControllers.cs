using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers
{
    // ═══════════════════════════════════════════════════════════════════════════
    // SERVICES SURVEILLES CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Supervision des microservices : MS1, MS2 et tout autre service enregistré.
    /// Permet de déclencher manuellement une vérification et de voir les états.
    /// </summary>
    [ApiController]
    [Route("api/services")]
    [Authorize(Roles = "Administrateur")]
    public class ServicesSurveillesController : ControllerBase
    {
        private readonly IServiceSurveilleService _service;

        public ServicesSurveillesController(IServiceSurveilleService service)
        {
            _service = service;
        }

        /// <summary>Récupère l'état de tous les services surveillés.</summary>
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            return Ok(await _service.GetAllAsync());
        }

        /// <summary>Récupère un service surveillé par ID.</summary>
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var s = await _service.GetByIdAsync(id);
            return s == null ? NotFound() : Ok(s);
        }

        /// <summary>Enregistre un nouveau service à surveiller.</summary>
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] ServiceSurveilleCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var s = await _service.CreateAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = s.Id }, s);
        }

        /// <summary>Déclenche une vérification manuelle de TOUS les services.</summary>
        [HttpPost("verifier-tous")]
        public async Task<IActionResult> VerifierTous()
        {
            await _service.VerifierTousLesServicesAsync();
            return Ok(new { message = "Vérification effectuée.", timestamp = DateTime.UtcNow });
        }

        /// <summary>Déclenche une vérification manuelle d'UN service.</summary>
        [HttpPost("{id:int}/verifier")]
        public async Task<IActionResult> Verifier(int id)
        {
            var s = await _service.VerifierServiceAsync(id);
            return s == null ? NotFound() : Ok(s);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // JOURNAL SYSTEME CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Consultation du journal système pour traçabilité et diagnostic.
    /// Filtrable par niveau (Info, Avertissement, Erreur, Critique) et source.
    /// </summary>
    [ApiController]
    [Route("api/journal")]
    [Authorize(Roles = "Administrateur")]
    public class JournalSystemeController : ControllerBase
    {
        private readonly IJournalSystemeService _service;

        public JournalSystemeController(IJournalSystemeService service)
        {
            _service = service;
        }

        /// <summary>
        /// Récupère les entrées du journal avec filtres optionnels.
        /// Limité aux 500 dernières entrées.
        /// </summary>
        [HttpGet]
        public async Task<IActionResult> GetAll(
            [FromQuery] string? niveau = null,
            [FromQuery] string? source = null,
            [FromQuery] DateTime? depuis = null)
        {
            return Ok(await _service.GetAllAsync(niveau, source, depuis));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // APPELS URGENTS CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    /// <summary>
    /// Lancement d'appels urgents aux donneurs de sang.
    /// Génère automatiquement des notifications ciblées par groupe sanguin et ville.
    /// IMPORTANT : La liste des donneurs est simulée dans ce projet étudiant.
    /// En production, elle serait récupérée depuis MS1 ou MS2 via HTTP.
    /// </summary>
    [ApiController]
    [Route("api/appels-urgents")]
    [Authorize(Roles = "Administrateur")]
    public class AppelsUrgentsController : ControllerBase
    {
        private readonly IAppelUrgentService _service;

        public AppelsUrgentsController(IAppelUrgentService service)
        {
            _service = service;
        }

        /// <summary>Récupère tous les appels urgents.</summary>
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            return Ok(await _service.GetAllAsync());
        }

        /// <summary>Récupère un appel urgent par ID.</summary>
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var a = await _service.GetByIdAsync(id);
            return a == null ? NotFound() : Ok(a);
        }

        /// <summary>
        /// Lance un appel urgent : envoie des notifications simulées aux donneurs ciblés.
        /// </summary>
        [HttpPost("lancer")]
        public async Task<IActionResult> Lancer([FromBody] AppelUrgentCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var a = await _service.LancerAppelAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = a.Id }, a);
        }

        /// <summary>Désactive un appel urgent actif.</summary>
        [HttpPatch("{id:int}/desactiver")]
        public async Task<IActionResult> Desactiver(int id)
        {
            var ok = await _service.DesactiverAsync(id);
            return ok ? NoContent() : NotFound();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMINISTRATEURS CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    [ApiController]
    [Route("api/administrateurs")]
    [Authorize(Roles = "Administrateur")]
    public class AdministrateursController : ControllerBase
    {
        private readonly BloodFlow.MS3.Data.AppDbContext _context;

        public AdministrateursController(BloodFlow.MS3.Data.AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            var admins = _context.Administrateurs
                .Where(a => a.Actif)
                .Select(a => new AdministrateurDto
                {
                    Id = a.Id, Nom = a.Nom, Prenom = a.Prenom,
                    Email = a.Email, Actif = a.Actif, UserIdMs1 = a.UserIdMs1
                }).ToList();
            return Ok(admins);
        }

        [HttpGet("{id:int}")]
        public IActionResult GetById(int id)
        {
            var a = _context.Administrateurs.Find(id);
            if (a == null) return NotFound();
            return Ok(new AdministrateurDto
            {
                Id = a.Id, Nom = a.Nom, Prenom = a.Prenom,
                Email = a.Email, Actif = a.Actif, UserIdMs1 = a.UserIdMs1
            });
        }

        [HttpPost]
        public IActionResult Create([FromBody] AdministrateurCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var admin = new BloodFlow.MS3.Models.Administrateur
            {
                Nom = dto.Nom, Prenom = dto.Prenom,
                Email = dto.Email, UserIdMs1 = dto.UserIdMs1,
                Actif = true, DateCreation = DateTime.UtcNow
            };
            _context.Administrateurs.Add(admin);
            _context.SaveChanges();
            return CreatedAtAction(nameof(GetById), new { id = admin.Id }, admin);
        }

        [HttpDelete("{id:int}")]
        public IActionResult Delete(int id)
        {
            var a = _context.Administrateurs.Find(id);
            if (a == null) return NotFound();
            a.Actif = false; // Soft delete
            _context.SaveChanges();
            return NoContent();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AGENTS PROMOTEURS CONTROLLER
    // ═══════════════════════════════════════════════════════════════════════════

    [ApiController]
    [Route("api/agents-promoteurs")]
    [Authorize(Roles = "Administrateur")]
    public class AgentsPromoteursController : ControllerBase
    {
        private readonly BloodFlow.MS3.Data.AppDbContext _context;

        public AgentsPromoteursController(BloodFlow.MS3.Data.AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            var agents = _context.AgentsPromoteurs
                .Where(a => a.Actif)
                .Select(a => new AgentPromoteurDto
                {
                    Id = a.Id, Nom = a.Nom, Prenom = a.Prenom,
                    Email = a.Email, Actif = a.Actif, UserIdMs1 = a.UserIdMs1
                }).ToList();
            return Ok(agents);
        }

        [HttpGet("{id:int}")]
        public IActionResult GetById(int id)
        {
            var a = _context.AgentsPromoteurs.Find(id);
            if (a == null) return NotFound();
            return Ok(new AgentPromoteurDto
            {
                Id = a.Id, Nom = a.Nom, Prenom = a.Prenom,
                Email = a.Email, Actif = a.Actif, UserIdMs1 = a.UserIdMs1
            });
        }

        [HttpPost]
        public IActionResult Create([FromBody] AgentPromoteurCreateDto dto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            var agent = new BloodFlow.MS3.Models.AgentPromoteur
            {
                Nom = dto.Nom, Prenom = dto.Prenom,
                Email = dto.Email, UserIdMs1 = dto.UserIdMs1,
                Actif = true, DateCreation = DateTime.UtcNow
            };
            _context.AgentsPromoteurs.Add(agent);
            _context.SaveChanges();
            return CreatedAtAction(nameof(GetById), new { id = agent.Id }, agent);
        }

        [HttpDelete("{id:int}")]
        public IActionResult Delete(int id)
        {
            var a = _context.AgentsPromoteurs.Find(id);
            if (a == null) return NotFound();
            a.Actif = false;
            _context.SaveChanges();
            return NoContent();
        }
    }
}
