# BloodFlow Auth Service — Microservice 1

**BloodFlow** est une plateforme de don de sang basée sur une architecture microservices.

Ce dépôt contient le **Microservice 1** : authentification, gestion des utilisateurs et des rôles.

---

## Table des matières

1. [Ce que fait ce microservice](#1-ce-que-fait-ce-microservice)
2. [Technologies utilisées](#2-technologies-utilisées)
3. [Créer la base de données MySQL](#3-créer-la-base-de-données-mysql)
4. [Configurer application.yml](#4-configurer-applicationyml)
5. [Lancer avec Maven](#5-lancer-avec-maven)
6. [Compte admin par défaut](#6-compte-admin-par-défaut)
7. [Liste des endpoints](#7-liste-des-endpoints)
8. [Comment fonctionne le login](#8-comment-fonctionne-le-login)
9. [Comment fonctionnent les rôles](#9-comment-fonctionnent-les-rôles)
10. [Redirection frontend par rôle](#10-redirection-frontend-par-rôle)
11. [Cache](#11-cache)
12. [Explication de la sécurité (pour les étudiants)](#12-explication-de-la-sécurité-pour-les-étudiants)
13. [API REST pour MS2 et MS3](#13-api-rest-pour-ms2-et-ms3)
14. [Exemples curl](#14-exemples-curl)

---

## 1. Ce que fait ce microservice

Ce microservice gère tout ce qui concerne l'identité et l'accès :

- **Inscription** : les utilisateurs publics s'inscrivent en tant que DONOR ou PATIENT
- **Login** : vérifie le mot de passe et retourne un JWT + refresh token
- **Logout** : révoque le refresh token
- **JWT** : chaque requête protégée nécessite un token JWT dans le header
- **Rôles** : ADMIN, DONOR, PATIENT, DOCTOR, STAFF, LAB_TECHNICIAN, BIOLOGIST, DELIVERY_AGENT, PROMOTER
- **Admin** : crée des comptes staff, active/désactive des comptes, gère les rôles
- **Profil** : l'utilisateur peut voir et modifier son profil
- **Réinitialisation de mot de passe** : par token (lien affiché dans la console en mode démo)
- **Audit** : enregistre les actions importantes (login, logout, désactivation…)
- **Cache** : stocke temporairement les rôles et profils pour accélérer les réponses
- **API REST** : expose des endpoints pour que MS2 et MS3 puissent vérifier l'identité des utilisateurs

---

## 2. Technologies utilisées

| Technologie | Version | Rôle |
|---|---|---|
| Java | 17 | Langage de programmation |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Security | 6 | Sécurité, JWT filter, RBAC |
| Spring Data JPA | — | Accès base de données |
| MySQL | 8+ | Base de données |
| jjwt | 0.11.5 | Génération et validation JWT |
| Lombok | — | Réduit le code répétitif |
| Caffeine Cache | 3.1.8 | Cache en mémoire |
| Springdoc OpenAPI | 2.5.0 | Documentation Swagger |
| BCrypt | — | Hash des mots de passe |
| Maven | 3.x | Build et dépendances |

---

## 3. Créer la base de données MySQL

Ouvre un terminal MySQL et exécute :

```sql
CREATE DATABASE IF NOT EXISTS bloodflow_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> **Note** : L'URL dans application.yml contient `createDatabaseIfNotExist=true`, donc la base est créée automatiquement si elle n'existe pas.

---

## 4. Configurer application.yml

Le fichier `src/main/resources/application.yml` contient toute la configuration.

Points importants à modifier selon ton environnement :

```yaml
spring:
  datasource:
    username: root      # ton utilisateur MySQL
    password: root      # ton mot de passe MySQL

bloodflow:
  admin:
    email: admin@bloodflow.ma
    password: Admin123@   # mot de passe admin par défaut
```

> **En production** : change le `jwt.secret` par une vraie clé secrète longue et aléatoire.

---

## 5. Lancer avec Maven

### Prérequis
- Java 17 installé (`java -version`)
- Maven installé (`mvn -version`)
- MySQL lancé et accessible

### Commandes

```bash
# Compilation complète
mvn clean install

# Lancer le serveur
mvn spring-boot:run
```

Le serveur démarre sur : **http://localhost:8081**

Swagger UI accessible sur : **http://localhost:8081/swagger-ui.html**

---

## 6. Compte admin par défaut

Au démarrage, Spring Boot crée automatiquement :

| Champ | Valeur |
|---|---|
| Email | admin@bloodflow.ma |
| Mot de passe | Admin123@ |
| Rôle | ADMIN |
| Status | ACTIVE |

Tu verras ces logs dans la console :

```
[Seeder] Default admin created:
[Seeder]   Email:    admin@bloodflow.ma
[Seeder]   Password: Admin123@
[Seeder]   Role:     ADMIN
```

---

## 7. Liste des endpoints

### Authentification (publics)

| Méthode | URL | Description |
|---|---|---|
| POST | /api/auth/register | Inscription (DONOR ou PATIENT seulement) |
| POST | /api/auth/login | Connexion, retourne JWT |
| POST | /api/auth/refresh | Renouveler le token JWT |
| POST | /api/auth/forgot-password | Demander un lien de reset |
| POST | /api/auth/reset-password | Réinitialiser le mot de passe |
| POST | /api/auth/verify-email | Vérifier l'email |
| POST | /api/auth/resend-verification-email | Renvoyer l'email de vérification |

### Authentification (nécessitent un JWT)

| Méthode | URL | Description |
|---|---|---|
| GET | /api/auth/me | Profil de l'utilisateur connecté |
| POST | /api/auth/logout | Déconnexion |
| POST | /api/auth/logout-all | Déconnexion de tous les appareils |
| POST | /api/auth/change-password | Changer son mot de passe |

### Profil utilisateur

| Méthode | URL | Description |
|---|---|---|
| GET | /api/users/profile | Voir son profil |
| PUT | /api/users/profile | Modifier son profil |

### Admin (nécessitent le rôle ADMIN)

| Méthode | URL | Description |
|---|---|---|
| GET | /api/admin/users | Liste tous les utilisateurs |
| GET | /api/admin/users/{id} | Voir un utilisateur |
| PUT | /api/admin/users/{id} | Modifier un utilisateur |
| POST | /api/admin/users/staff | Créer un compte staff |
| POST | /api/admin/users/{id}/roles | Assigner un rôle |
| DELETE | /api/admin/users/{id}/roles/{roleName} | Retirer un rôle |
| PATCH | /api/admin/users/{id}/enable | Activer un compte |
| PATCH | /api/admin/users/{id}/disable | Désactiver un compte |
| POST | /api/admin/users/{id}/reset-temporary-password | Reset mot de passe temporaire |

### Rôles

| Méthode | URL | Description |
|---|---|---|
| GET | /api/roles | Liste tous les rôles |
| GET | /api/roles/{id} | Voir un rôle |

### Intégration MS2 / MS3

| Méthode | URL | Description |
|---|---|---|
| GET | /api/integration/ping | Vérifier si MS1 est disponible |
| POST | /api/integration/validate-token | Valider un JWT |
| GET | /api/integration/users/{id}/exists | Vérifier si un user existe |
| GET | /api/integration/users/{id} | Obtenir les infos d'un user |
| GET | /api/integration/current-user | Utilisateur connecté (JWT requis) |

### Santé

| Méthode | URL | Description |
|---|---|---|
| GET | /api/health | Status du service |

---

## 8. Comment fonctionne le login

**Étape par étape :**

1. Le frontend envoie `POST /api/auth/login` avec `{ email, password }`.
2. Le backend vérifie que l'email existe dans MySQL.
3. Le backend vérifie le mot de passe avec BCrypt.
4. Si tout est OK, le backend génère :
   - Un **JWT access token** (valable 60 minutes)
   - Un **refresh token** (valable 7 jours, stocké en base)
5. Le backend retourne les deux tokens + les infos de l'utilisateur.
6. Le frontend stocke le JWT et l'envoie dans chaque requête suivante :
   ```
   Authorization: Bearer eyJhbGci...
   ```
7. Notre filtre `JwtAuthenticationFilter` lit ce header, valide le token et identifie l'utilisateur.

**Réponse du login :**

```json
{
  "success": true,
  "message": "Connexion réussie",
  "data": {
    "accessToken": "eyJhbGci...",
    "token": "eyJhbGci...",
    "refreshToken": "uuid-refresh-token",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "firstName": "Super",
      "lastName": "Admin",
      "fullName": "Super Admin",
      "email": "admin@bloodflow.ma",
      "emailVerified": true,
      "mustChangePassword": false,
      "status": "ACTIVE",
      "roles": ["ADMIN"]
    }
  }
}
```

> `accessToken` et `token` contiennent la même valeur JWT. Les deux champs existent pour compatibilité avec le frontend React.

---

## 9. Comment fonctionnent les rôles

**Inscription publique :** uniquement DONOR et PATIENT.

**Création par admin :** DOCTOR, STAFF, LAB_TECHNICIAN, BIOLOGIST, DELIVERY_AGENT, PROMOTER.

**Création automatique au démarrage :** ADMIN (via AdminSeeder).

Le JWT contient les rôles dans un champ `roles` :
```json
{
  "sub": "1",
  "email": "admin@bloodflow.ma",
  "roles": ["ADMIN"],
  "exp": 1234567890
}
```

Spring Security lit ce JWT à chaque requête et applique les restrictions :
- `/api/admin/**` → seulement ADMIN
- `/api/users/**` → n'importe quel utilisateur connecté
- `/api/auth/login` → public

---

## 10. Redirection frontend par rôle

Après le login, le frontend lit `data.user.roles[0]` et redirige :

| Rôle | Dashboard |
|---|---|
| ADMIN | /dashboard/admin |
| DONOR | /dashboard/donor |
| PATIENT | /dashboard/patient |
| DOCTOR | /dashboard/doctor |
| STAFF | /dashboard/staff |
| LAB_TECHNICIAN | /dashboard/lab-technician |
| BIOLOGIST | /dashboard/biologist |
| DELIVERY_AGENT | /dashboard/delivery |
| PROMOTER | /dashboard/promoter |

Exemple React :
```javascript
const role = response.data.user.roles[0];
navigate(`/dashboard/${role.toLowerCase().replace('_', '-')}`);
```

---

## 11. Cache

**Qu'est-ce que le cache ?**

Au lieu de demander à MySQL les mêmes données encore et encore, on les stocke temporairement en mémoire (RAM). La prochaine fois que quelqu'un demande la même chose, on répond directement depuis la mémoire — beaucoup plus rapide.

**Ce qu'on met en cache :**

| Cache | Clé | Durée | Raison |
|---|---|---|---|
| `roles` | — | 5 min | Les rôles changent rarement |
| `userProfiles` | userId | 5 min | Le profil est lu souvent |
| `tokenValidation` | token | 5 min | MS2/MS3 valident le même token plusieurs fois |

**Comment ça fonctionne dans le code :**

```java
@Cacheable("roles")
public List<RoleResponse> getAllRoles() { ... }

@Cacheable(value = "userProfiles", key = "#principal.id")
public UserResponse getProfile(UserPrincipal principal) { ... }

@CacheEvict(value = "userProfiles", key = "#principal.id")
public UserResponse updateProfile(...) { ... }
```

Quand le profil est mis à jour, `@CacheEvict` vide le cache pour que les données soient rafraîchies.

---

## 12. Explication de la sécurité (pour les étudiants)

### 1. BCrypt — Hashage des mots de passe

**Ce que c'est :** les mots de passe ne sont jamais stockés en clair dans MySQL. On les transforme en une chaîne incompréhensible (hash) avec BCrypt.

Exemple : `Admin123@` → `$2a$10$xyz...abc`

**Protection contre :** le vol de la base de données. Même si un attaquant obtient la base, il ne peut pas lire les mots de passe.

---

### 2. JWT — Authentification par token

**Ce que c'est :** après le login, le serveur génère un token signé que le frontend garde. Le frontend envoie ce token dans chaque requête. Le serveur vérifie la signature pour confirmer que le token est authentique.

**Protection contre :** accéder aux pages privées sans être connecté. Sans token valide, le serveur refuse la requête.

---

### 3. Contrôle d'accès basé sur les rôles (RBAC)

**Ce que c'est :** chaque utilisateur a un rôle (ADMIN, DOCTOR, PATIENT…). Les endpoints admin ne sont accessibles qu'aux ADMIN.

**Protection contre :** un PATIENT qui essaie d'accéder aux endpoints admin. Spring Security vérifie le rôle automatiquement.

---

### 4. CORS — Cross-Origin Resource Sharing

**Ce que c'est :** on configure le backend pour accepter uniquement les appels venant de `http://localhost:5173` (le frontend React).

**Protection contre :** un site malveillant qui essaierait d'appeler notre API depuis le navigateur d'un utilisateur.

---

### 5. Validation des entrées

**Ce que c'est :** on vérifie que les données reçues sont valides (email correct, mot de passe assez long, champs obligatoires présents) grâce aux annotations `@Valid`, `@Email`, `@NotBlank`, `@Size`.

**Protection contre :** les données incorrectes, certaines tentatives d'injection.

---

### 6. Désactivation de compte

**Ce que c'est :** l'admin peut désactiver (`DISABLED`) un compte. L'utilisateur ne peut plus se connecter même avec le bon mot de passe.

**Protection contre :** les comptes suspects ou non autorisés.

---

### 7. Audit logs

**Ce que c'est :** on enregistre dans la base toutes les actions importantes : login réussi/échoué, déconnexion, désactivation, assignation de rôle…

**Protection contre :** ne pas savoir ce qui s'est passé après un incident de sécurité. L'audit permet de retrouver l'historique.

---

### 8. Révocation des refresh tokens

**Ce que c'est :** quand l'utilisateur se déconnecte, son refresh token est marqué comme révoqué dans MySQL. Il ne peut plus l'utiliser pour obtenir un nouveau JWT.

**Protection contre :** des sessions qui restent actives indéfiniment après déconnexion.

---

### 9. Réponse neutre pour "mot de passe oublié"

**Ce que c'est :** l'endpoint `/forgot-password` retourne toujours le même message, que l'email existe ou non dans la base.

**Protection contre :** un attaquant qui essaierait de deviner quels emails sont enregistrés sur la plateforme.

---

## 13. API REST pour MS2 et MS3

MS2 (médical) et MS3 (notifications) peuvent appeler ces endpoints sans JWT (sauf `/current-user`) :

**Vérifier que MS1 est en ligne :**
```
GET /api/integration/ping
```

**Valider un token JWT :**
```
POST /api/integration/validate-token
{ "token": "eyJhbGci..." }
```

**Vérifier si un utilisateur existe :**
```
GET /api/integration/users/{id}/exists
```

**Obtenir les infos d'un utilisateur :**
```
GET /api/integration/users/{id}
```

Ces endpoints permettent à MS2 de vérifier l'identité d'un utilisateur avant de créer un dossier médical, ou à MS3 d'obtenir l'email d'un utilisateur pour lui envoyer une notification.

---

## 14. Exemples curl

### Santé du service
```bash
curl http://localhost:8081/api/health
```

### Login admin
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@bloodflow.ma\",\"password\":\"Admin123@\"}"
```

### Inscription d'un donneur
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"firstName\":\"Ghada\",\"lastName\":\"Benbarech\",\"email\":\"ghada@gmail.com\",\"phoneNumber\":\"0600000000\",\"password\":\"Ghada123@\",\"confirmPassword\":\"Ghada123@\",\"role\":\"DONOR\",\"city\":\"Oujda\"}"
```

### Accéder à un endpoint protégé (remplace JWT_HERE)
```bash
curl http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer JWT_HERE"
```

### Lister les utilisateurs (admin)
```bash
curl http://localhost:8081/api/admin/users \
  -H "Authorization: Bearer JWT_HERE"
```

### Créer un médecin (admin)
```bash
curl -X POST http://localhost:8081/api/admin/users/staff \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer JWT_HERE" \
  -d "{\"firstName\":\"Sara\",\"lastName\":\"Amrani\",\"email\":\"sara@bloodflow.ma\",\"phoneNumber\":\"0600000001\",\"role\":\"DOCTOR\",\"city\":\"Casablanca\",\"temporaryPassword\":\"Temp123@\"}"
```

### Valider un token (pour MS2 et MS3)
```bash
curl -X POST http://localhost:8081/api/integration/validate-token \
  -H "Content-Type: application/json" \
  -d "{\"token\":\"JWT_HERE\"}"
```

### Mot de passe oublié
```bash
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"ghada@gmail.com\"}"
```

> En mode démo, le lien de réinitialisation s'affiche dans la console du serveur.

---

## Structure du projet

```
bloodflow-auth-service/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/bloodflow/auth/
    │   ├── BloodFlowAuthApplication.java   ← Point d'entrée
    │   ├── config/                         ← SecurityConfig, CorsConfig, CacheConfig...
    │   ├── controller/                     ← AuthController, AdminUserController...
    │   ├── dto/request/                    ← LoginRequest, RegisterRequest...
    │   ├── dto/response/                   ← ApiResponse, AuthResponse, UserResponse...
    │   ├── entity/                         ← User, Role, RefreshToken...
    │   ├── enums/                          ← RoleName, UserStatus
    │   ├── exception/                      ← GlobalExceptionHandler, exceptions...
    │   ├── mapper/                         ← UserMapper, RoleMapper
    │   ├── repository/                     ← UserRepository, RoleRepository...
    │   ├── security/                       ← JwtAuthenticationFilter, UserPrincipal...
    │   ├── seed/                           ← RoleSeeder, AdminSeeder
    │   └── service/                        ← AuthService, UserService, JwtService...
    └── resources/
        └── application.yml
```

---

*BloodFlow Auth Service — Microservice 1 | Java 17 + Spring Boot 3 + MySQL*
