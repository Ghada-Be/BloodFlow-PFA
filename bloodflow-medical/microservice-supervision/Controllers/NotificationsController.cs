using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers;

[Authorize]
[ApiController]
[Route("api/notifications")]
public class NotificationsController : ControllerBase
{
    private readonly NotificationService _service;

    public NotificationsController(NotificationService service)
    {
        _service = service;
    }

    [HttpGet]
    public async Task<ActionResult<List<NotificationDto>>> GetAll()
    {
        return Ok(await _service.GetAllAsync());
    }

    [HttpGet("user/{userId}")]
    public async Task<ActionResult<List<NotificationDto>>> GetByUser(string userId)
    {
        return Ok(await _service.GetByUserAsync(userId));
    }

    [HttpPost]
    public async Task<ActionResult<NotificationDto>> Create(NotificationCreateDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.UserId) || string.IsNullOrWhiteSpace(dto.Title) || string.IsNullOrWhiteSpace(dto.Message))
            return BadRequest("UserId, Title et Message sont obligatoires.");

        var created = await _service.CreateAsync(dto);
        return Ok(created);
    }

    [HttpPatch("{id:int}/read")]
    public async Task<IActionResult> MarkAsRead(int id)
    {
        return await _service.MarkAsReadAsync(id) ? NoContent() : NotFound();
    }
}
