using BloodFlow.MS3.DTOs;
using BloodFlow.MS3.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BloodFlow.MS3.Controllers;

[Authorize(Roles = "ADMIN")]
[ApiController]
[Route("api/reports")]
public class ReportsController : ControllerBase
{
    private readonly ReportService _service;

    public ReportsController(ReportService service)
    {
        _service = service;
    }

    [HttpGet("summary")]
    public async Task<ActionResult<ReportSummaryDto>> GetSummary()
    {
        return Ok(await _service.GetSummaryAsync());
    }

    [HttpPost("generate")]
    public async Task<IActionResult> Generate(ReportCreateDto dto)
    {
        var report = await _service.GenerateReportAsync(dto);
        return Ok(new
        {
            report.Id,
            report.ReportType,
            report.Format,
            report.GeneratedAt,
            report.Content
        });
    }
}
