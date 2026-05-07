# BloodFlow MS3 — Supervision, Alertes, Notifications et Rapports

Microservice simple et fonctionnel en **ASP.NET Core 8 + SQL Server + Memory Cache**.

## Architecture simple

Le MS3 contient :

- `SystemLogs` : journal système avec l’architecture demandée : `Id`, `ServiceName`, `Level`, `Message`, `CreatedAt`
- `Alerts` : alertes internes
- `Notifications` : notifications applicatives
- `Reports` : rapports de supervision
- `SupervisedServices` : services surveillés avec health check

## Cache

Le cache est fait avec `IMemoryCache`.

Exemples de données cachées :

- liste des logs
- liste des alertes
- liste des notifications
- rapport résumé
- état des microservices

Le cache est automatiquement vidé après création ou modification d’une donnée.

## Configuration SQL Server

Dans `appsettings.json` :

```json
"DefaultConnection": "Server=DESKTOP-TON57SE;Database=MS3;Trusted_Connection=True;TrustServerCertificate=True;"
```

Si tu utilises SQL Express :

```json
"DefaultConnection": "Server=DESKTOP-TON57SE\\SQLEXPRESS;Database=MS3;Trusted_Connection=True;TrustServerCertificate=True;"
```

## Lancer le projet

```bash
dotnet restore
dotnet build
dotnet run
```

La base est créée automatiquement avec `EnsureCreated()`.

Swagger :

```text
https://localhost:xxxx/swagger
http://localhost:xxxx/swagger
```

## Endpoints importants

### Health

```http
GET /api/health
```

### Logs système

```http
GET /api/system-logs
POST /api/system-logs
```

Body :

```json
{
  "serviceName": "MS1 Auth",
  "level": "Information",
  "message": "Connexion réussie"
}
```

### Alertes

```http
GET /api/alerts
POST /api/alerts
PATCH /api/alerts/{id}/read
DELETE /api/alerts/{id}
```

### Notifications

```http
GET /api/notifications
GET /api/notifications/user/{userId}
POST /api/notifications
PATCH /api/notifications/{id}/read
```

### Supervision

```http
GET /api/supervision/services-status
GET /api/supervision/services-status?forceRefresh=true
POST /api/supervision/services
```

### Rapports

```http
GET /api/reports/summary
POST /api/reports/generate
```
