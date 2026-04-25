# BloodFlow - Microservice 3 : Supervision & Coordination

## Rôle du Microservice 3
MS3 est le cerveau de coordination de BloodFlow. Il :
- **Surveille** l'état des autres microservices (MS1, MS2) via leurs endpoints /health
- **Gère les alertes** critiques (stock de sang bas, service indisponible...)
- **Envoie des notifications** aux utilisateurs et donneurs
- **Lance des appels urgents** aux donneurs ciblés par groupe sanguin
- **Organise les campagnes** de sensibilisation au don de sang
- **Coordonne les collectes** de sang sur le terrain
- **Recrute des bénévoles** pour les collectes
- **Génère des rapports** (Global, Campagnes, Alertes, Système...)
- **Tient un journal système** de tous les événements importants

Il NE gère PAS l'authentification — celle-ci est dans MS1.
Il VALIDE les tokens JWT émis par MS1.

---

## Architecture du projet

```
BloodFlow.MS3/
 ├── Controllers/          → Points d'entrée REST de l'API
 │     ├── AlertesCampagnesControllers.cs
 │     ├── CollectesRapportsControllers.cs
 │     └── SupervisionControllers.cs
 ├── DTOs/                 → Objets de transfert (ce que l'API reçoit et renvoie)
 │     ├── AlerteDtos.cs
 │     ├── NotificationDtos.cs
 │     ├── CampagneDtos.cs
 │     └── OtherDtos.cs
 ├── Models/               → Entités de la base de données
 │     ├── Administrateur.cs
 │     ├── AgentPromoteur.cs
 │     ├── ServiceSurveille.cs
 │     ├── Rapport.cs
 │     ├── JournalSysteme.cs
 │     ├── Alerte.cs
 │     ├── Notification.cs
 │     ├── Campagne.cs
 │     ├── CollecteSang.cs
 │     ├── Benevole.cs
 │     └── AppelUrgent.cs
 ├── Data/                 → Contexte EF Core (connexion à SQL Server)
 │     └── AppDbContext.cs
 ├── Services/             → Logique métier
 │     ├── AlerteService.cs
 │     ├── JournalSystemeService.cs
 │     ├── NotificationService.cs
 │     ├── CampagneService.cs
 │     ├── CollecteSangService.cs
 │     ├── OtherServices.cs      (Benevole, Rapport, AppelUrgent)
 │     ├── ServiceSurveilleService.cs
 │     └── HealthCheckBackgroundService.cs
 ├── Interfaces/           → Contrats des services (bonne pratique)
 │     └── IServices.cs
 ├── Middleware/           → Gestion globale des erreurs
 │     └── ErrorHandlingMiddleware.cs
 ├── Scripts/              → Script SQL de création de la base
 │     └── CreateDatabase_MS3.sql
 ├── Program.cs            → Configuration et démarrage de l'API
 ├── appsettings.json      → Configuration (connexion BDD, JWT, URLs)
 └── BloodFlow.MS3.csproj  → Fichier de projet .NET
```

---

## Prérequis

- Visual Studio 2022 (ou VS Code)
- .NET 8 SDK
- SQL Server (instance locale : DESKTOP-TON57SE)
- SQL Server Management Studio (SSMS) recommandé

---

## Étapes d'installation et d'exécution

### Étape 1 — Vérifier la connexion SQL Server

Dans `appsettings.json`, vérifier la chaîne de connexion :
```json
"Server=DESKTOP-TON57SE;Database=MS3;Trusted_Connection=True;TrustServerCertificate=True;"
```
Remplacer `DESKTOP-TON57SE` par le nom de votre serveur SQL Server si différent.

### Étape 2 — Restaurer les packages NuGet

Dans Visual Studio : clic droit sur le projet → "Restore NuGet Packages"

Ou en ligne de commande dans le dossier du projet :
```bash
dotnet restore
```

### Étape 3 — Créer la migration initiale

```bash
dotnet ef migrations add InitialCreate
```

### Étape 4 — Appliquer la migration (crée la base MS3)

```bash
dotnet ef database update
```

Note : La base MS3 est aussi créée automatiquement au démarrage de l'API
grâce à `db.Database.Migrate()` dans Program.cs.

### Étape 5 — Lancer l'API

```bash
dotnet run
```

Ou dans Visual Studio : touche F5

### Étape 6 — Ouvrir Swagger

L'API démarre sur `http://localhost:5003` (ou le port affiché dans le terminal).
Swagger est accessible directement à la racine : **http://localhost:5003/**

---

## Packages NuGet utilisés

| Package | Version | Rôle |
|---------|---------|------|
| Microsoft.EntityFrameworkCore.SqlServer | 8.0.0 | ORM pour SQL Server |
| Microsoft.EntityFrameworkCore.Tools | 8.0.0 | Commandes migrations |
| Microsoft.EntityFrameworkCore.Design | 8.0.0 | Support design-time |
| Microsoft.AspNetCore.Authentication.JwtBearer | 8.0.0 | Validation JWT |
| Swashbuckle.AspNetCore | 6.5.0 | Documentation Swagger |

Installation manuelle si nécessaire :
```bash
dotnet add package Microsoft.EntityFrameworkCore.SqlServer --version 8.0.0
dotnet add package Microsoft.EntityFrameworkCore.Tools --version 8.0.0
dotnet add package Microsoft.EntityFrameworkCore.Design --version 8.0.0
dotnet add package Microsoft.AspNetCore.Authentication.JwtBearer --version 8.0.0
dotnet add package Swashbuckle.AspNetCore --version 6.5.0
```

---

## Endpoints REST

### Alertes
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/alertes | Lister toutes les alertes (filtrables) |
| GET | /api/alertes/{id} | Détail d'une alerte |
| POST | /api/alertes | Créer une alerte |
| PATCH | /api/alertes/{id}/etat | Changer l'état d'une alerte |
| DELETE | /api/alertes/{id} | Supprimer une alerte |

### Notifications
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/notifications | Lister les notifications |
| POST | /api/notifications | Envoyer une notification |
| PATCH | /api/notifications/{id}/marquer-envoyee | Marquer comme envoyée |

### Campagnes
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/campagnes | Lister les campagnes |
| GET | /api/campagnes/{id} | Détail campagne |
| POST | /api/campagnes | Créer une campagne |
| PUT | /api/campagnes/{id} | Modifier une campagne |
| PATCH | /api/campagnes/{id}/statut | Changer le statut |
| DELETE | /api/campagnes/{id} | Supprimer |

### Collectes de Sang
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/collectes | Lister les collectes |
| GET | /api/collectes/{id} | Détail collecte |
| POST | /api/collectes | Créer une collecte |
| PUT | /api/collectes/{id} | Modifier |
| DELETE | /api/collectes/{id} | Supprimer |

### Bénévoles
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/benevoles | Lister les bénévoles |
| GET | /api/benevoles/{id} | Détail bénévole |
| POST | /api/benevoles | Inscrire un bénévole |
| PATCH | /api/benevoles/{id}/affecter/{collecteId} | Affecter à une collecte |
| DELETE | /api/benevoles/{id} | Supprimer |

### Rapports
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/rapports | Lister les rapports |
| GET | /api/rapports/{id} | Télécharger un rapport |
| POST | /api/rapports/generer | Générer un rapport |

### Supervision
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/services | État de tous les services |
| POST | /api/services/verifier-tous | Vérification manuelle globale |
| POST | /api/services/{id}/verifier | Vérification d'un service |

### Journal Système
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/journal | Consulter le journal (filtrable) |

### Appels Urgents
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /api/appels-urgents | Lister les appels |
| POST | /api/appels-urgents/lancer | Lancer un appel urgent |
| PATCH | /api/appels-urgents/{id}/desactiver | Désactiver |

### Administrateurs & Agents
| Méthode | Route | Rôle |
|---------|-------|------|
| GET/POST/DELETE | /api/administrateurs | Gestion des admins |
| GET/POST/DELETE | /api/agents-promoteurs | Gestion des agents |

### Health Check MS3
| Méthode | Route | Rôle |
|---------|-------|------|
| GET | /health | État de MS3 lui-même |

---

## Tester avec Swagger (sans authentification)

Pour tester rapidement sans token JWT :
1. Dans `Program.cs`, commenter temporairement `app.UseAuthentication()` et `app.UseAuthorization()`
2. Retirer `[Authorize]` sur un controller
3. Tester via Swagger
4. Remettre l'authentification ensuite

Pour tester avec JWT :
1. Obtenir un token depuis MS1 (ou générer un token de test)
2. Dans Swagger, cliquer sur "Authorize" (cadenas)
3. Entrer : `Bearer eyJhbGciOi...votre_token`
4. Cliquer "Authorize"

---

## Ce qui est simulé (Mock) vs Réel

| Fonctionnalité | Réel ou Mock |
|----------------|-------------|
| Alertes en BDD | ✅ Réel |
| Notifications en BDD | ✅ Réel |
| Campagnes / Collectes | ✅ Réel |
| Bénévoles | ✅ Réel |
| Rapports JSON | ✅ Réel |
| Health check HTTP | ✅ Réel (requête HTTP) |
| Liste des donneurs | ⚠️ Mock (3 emails simulés) |
| Envoi Email/SMS réel | ⚠️ Simulé (statut = Envoyée) |
| Communication avec MS1/MS2 | ⚠️ Partiel (health check seulement) |

---

## Comment présenter ce projet au jury

1. **Rôle** : MS3 est le superviseur et coordinateur — il surveille, alerte, notifie et organise.
2. **Architecture** : couches séparées (Models → Services → Controllers), DTOs pour isoler l'API des entités.
3. **Tables principales** : Alertes, Notifications, Campagnes, CollectesSang, Benevoles, JournalSysteme.
4. **Sécurité** : JWT validé (émis par MS1), rôles Admin et AgentPromoteur, middleware d'erreurs.
5. **Automatisation** : BackgroundService surveille les services toutes les 60 secondes.
6. **Cohérence microservices** : MS3 respecte son périmètre, ne gère pas l'auth, communique via HTTP.
