using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers;

[Authorize(Roles = "ADMIN")]
[ApiController]
[Route("api/supervision")]
public class SupervisionController : ControllerBase
{
    private readonly SupervisionService _service;

    public SupervisionController(SupervisionService service)
    {
        _service = service;
    }

    [HttpGet("services-status")]
    public async Task<ActionResult<List<ServiceStatusDto>>> CheckServices([FromQuery] bool forceRefresh = false)
    {
        return Ok(await _service.CheckServicesAsync(forceRefresh));
    }

    [HttpPost("services")]
    public async Task<ActionResult<ServiceStatusDto>> AddService(SupervisedServiceCreateDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Name) || string.IsNullOrWhiteSpace(dto.BaseUrl))
            return BadRequest("Name et BaseUrl sont obligatoires.");

        return Ok(await _service.AddServiceAsync(dto));
    }
}
