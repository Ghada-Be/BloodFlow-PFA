using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.SignalR;
using System.Security.Claims;

namespace BloodFlow.MS3.Hubs;

[Authorize]
public class NotificationHub : Hub
{
    public override async Task OnConnectedAsync()
    {
        var userId = GetCurrentUserId();
        if (!string.IsNullOrWhiteSpace(userId))
        {
            await Groups.AddToGroupAsync(Context.ConnectionId, UserGroup(userId));
        }

        foreach (var role in GetCurrentUserRoles())
        {
            await Groups.AddToGroupAsync(Context.ConnectionId, RoleGroup(role));
        }

        await base.OnConnectedAsync();
    }

    public async Task JoinUserGroup(string userId)
    {
        var currentUserId = GetCurrentUserId();
        var isAdmin = GetCurrentUserRoles().Contains("ADMIN", StringComparer.OrdinalIgnoreCase);

        if (!isAdmin && !string.Equals(currentUserId, userId, StringComparison.OrdinalIgnoreCase))
        {
            throw new HubException("You can only join your own notification group.");
        }

        await Groups.AddToGroupAsync(Context.ConnectionId, UserGroup(userId));
    }

    public async Task LeaveUserGroup(string userId)
    {
        await Groups.RemoveFromGroupAsync(Context.ConnectionId, UserGroup(userId));
    }

    public async Task JoinRoleGroup(string role)
    {
        var roles = GetCurrentUserRoles();
        if (!roles.Contains(role, StringComparer.OrdinalIgnoreCase) && !roles.Contains("ADMIN", StringComparer.OrdinalIgnoreCase))
        {
            throw new HubException("You cannot join a role group that does not belong to you.");
        }

        await Groups.AddToGroupAsync(Context.ConnectionId, RoleGroup(role));
    }

    public async Task LeaveRoleGroup(string role)
    {
        await Groups.RemoveFromGroupAsync(Context.ConnectionId, RoleGroup(role));
    }

    public static string UserGroup(string userId) => $"user-{userId}";
    public static string RoleGroup(string role) => $"role-{role.ToUpperInvariant()}";

    private string? GetCurrentUserId()
    {
        return Context.User?.FindFirstValue(ClaimTypes.NameIdentifier)
            ?? Context.User?.FindFirstValue("sub")
            ?? Context.User?.FindFirstValue("userId")
            ?? Context.User?.FindFirstValue("id");
    }

    private List<string> GetCurrentUserRoles()
    {
        var roles = new List<string>();

        roles.AddRange(Context.User?.FindAll(ClaimTypes.Role).Select(c => c.Value) ?? Enumerable.Empty<string>());
        roles.AddRange(Context.User?.FindAll("role").Select(c => c.Value) ?? Enumerable.Empty<string>());
        roles.AddRange(Context.User?.FindAll("roles").Select(c => c.Value) ?? Enumerable.Empty<string>());

        return roles
            .Where(r => !string.IsNullOrWhiteSpace(r))
            .Select(r => r.Replace("ROLE_", "", StringComparison.OrdinalIgnoreCase).Trim().ToUpperInvariant())
            .Distinct(StringComparer.OrdinalIgnoreCase)
            .ToList();
    }
}
