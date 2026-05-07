using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers;

[Authorize(Roles = "ADMIN")]
[ApiController]
[Route("api/system-logs")]
public class SystemLogsController : ControllerBase
{
    private readonly SystemLogService _service;

    public SystemLogsController(SystemLogService service)
    {
        _service = service;
    }

    [HttpGet]
    public async Task<ActionResult<List<SystemLogDto>>> GetAll()
    {
        return Ok(await _service.GetAllAsync());
    }

    [HttpPost]
    public async Task<ActionResult<SystemLogDto>> Create(SystemLogCreateDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.ServiceName) || string.IsNullOrWhiteSpace(dto.Message))
            return BadRequest("ServiceName et Message sont obligatoires.");

        var created = await _service.CreateAsync(dto);
        return CreatedAtAction(nameof(GetAll), new { id = created.Id }, created);
    }
}
