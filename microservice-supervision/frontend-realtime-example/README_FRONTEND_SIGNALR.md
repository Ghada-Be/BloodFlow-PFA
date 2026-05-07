# BloodFlow MS3 - React SignalR + Toastify Integration

Install dependencies in your React frontend:

```bash
npm install @microsoft/signalr react-toastify axios
```

Add this environment variable in your frontend `.env`:

```env
VITE_SUPERVISION_API_URL=http://localhost:8083
```

After login with MS1, store:

```js
localStorage.setItem("token", response.data.data.accessToken);
localStorage.setItem("user", JSON.stringify(response.data.data.user));
```

Wrap your connected app/layout:

```jsx
import NotificationRealtimeProvider from "./components/NotificationRealtimeProvider";

export default function App() {
  return (
    <NotificationRealtimeProvider>
      {/* your routes/layout */}
    </NotificationRealtimeProvider>
  );
}
```

Test real-time notifications:

1. Login in MS1 and keep the frontend open.
2. Open MS3 Swagger: `http://localhost:8083/swagger`.
3. Authorize with `Bearer <MS1_ACCESS_TOKEN>`.
4. Call `POST /api/notifications`:

```json
{
  "userId": "1",
  "targetRole": "ADMIN",
  "title": "BloodFlow urgent alert",
  "message": "Stock O- faible, vérification urgente demandée.",
  "type": "Alert",
  "priority": "High"
}
```

The toast should appear instantly in the React app without polling.
