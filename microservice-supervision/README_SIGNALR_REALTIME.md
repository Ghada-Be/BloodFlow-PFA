# BloodFlow MS3 - Supervision + Notifications Temps Réel

Cette version du Microservice 3 contient :

- API REST supervision / alertes / notifications / rapports / logs
- Authentification JWT alignée avec MS1
- SQL Server via Docker Compose
- Cache MemoryCache
- Swagger avec Bearer JWT
- SignalR Hub pour notifications temps réel
- Exemple React + Toastify dans `frontend-realtime-example/`

## Ports

- MS1 Auth : `http://localhost:8081`
- MS2 Medical : `http://localhost:8082`
- MS3 Supervision : `http://localhost:8083`
- SQL Server MS3 sur le PC : `localhost,1434`

## Lancer MS3 avec Docker

Dans le dossier qui contient `BloodFlow.MS3.csproj`, `Dockerfile` et `docker-compose.yml` :

```powershell
docker compose up --build
```

Tester :

```txt
http://localhost:8083/api/health
http://localhost:8083/swagger
```

## SignalR endpoint

```txt
http://localhost:8083/hubs/notifications
```

Le hub est protégé par JWT MS1. Le frontend doit envoyer le token MS1 via SignalR `accessTokenFactory`.

## Tester rapidement le temps réel

1. Lance MS1 sur `8081`.
2. Login admin dans MS1 avec Swagger.
3. Copie `data.accessToken`.
4. Lance MS3 sur `8083`.
5. Lance le frontend React avec le composant `NotificationRealtimeProvider`.
6. Dans Swagger MS3, clique Authorize et mets :

```txt
Bearer TON_TOKEN_MS1
```

7. Fais :

```txt
POST /api/notifications
```

Exemple :

```json
{
  "userId": "1",
  "targetRole": "ADMIN",
  "title": "Alerte BloodFlow",
  "message": "Nouvelle notification temps réel envoyée depuis MS3.",
  "type": "Alert",
  "priority": "High"
}
```

Résultat attendu : la notification apparaît immédiatement dans React via Toastify, sans polling.

## Important

Le polling peut rester comme fallback, mais cette version ajoute le vrai temps réel avec SignalR.
