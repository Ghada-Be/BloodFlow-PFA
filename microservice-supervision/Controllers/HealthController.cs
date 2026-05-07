using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers;

[AllowAnonymous]
[ApiController]
[Route("api/health")]
public class HealthController : ControllerBase
{
    [HttpGet]
    public IActionResult Get()
    {
        return Ok(new
        {
            service = "BloodFlow.MS3.Supervision",
            status = "UP",
            checkedAt = DateTime.UtcNow
        });
    }
}
