using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers;

[Authorize]
[ApiController]
[Route("api/alerts")]
public class AlertsController : ControllerBase
{
    private readonly AlertService _service;

    public AlertsController(AlertService service)
    {
        _service = service;
    }

    [HttpGet]
    public async Task<ActionResult<List<AlertDto>>> GetAll()
    {
        return Ok(await _service.GetAllAsync());
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<AlertDto>> GetById(int id)
    {
        var alert = await _service.GetByIdAsync(id);
        return alert is null ? NotFound() : Ok(alert);
    }

    [HttpPost]
    public async Task<ActionResult<AlertDto>> Create(AlertCreateDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Title) || string.IsNullOrWhiteSpace(dto.Description))
            return BadRequest("Title et Description sont obligatoires.");

        var created = await _service.CreateAsync(dto);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPatch("{id:int}/read")]
    public async Task<IActionResult> MarkAsRead(int id)
    {
        return await _service.MarkAsReadAsync(id) ? NoContent() : NotFound();
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> Delete(int id)
    {
        return await _service.DeleteAsync(id) ? NoContent() : NotFound();
    }
}
