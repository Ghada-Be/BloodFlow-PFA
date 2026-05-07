import * as signalR from "@microsoft/signalr";

const MS3_URL = import.meta.env.VITE_SUPERVISION_API_URL || "http://localhost:8083";

export function createNotificationConnection(token) {
  return new signalR.HubConnectionBuilder()
    .withUrl(`${MS3_URL}/hubs/notifications`, {
      accessTokenFactory: () => token,
    })
    .withAutomaticReconnect([0, 2000, 5000, 10000, 30000])
    .configureLogging(signalR.LogLevel.Information)
    .build();
}
