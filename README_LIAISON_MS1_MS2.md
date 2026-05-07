# BloodFlow Microservice 2 Backend — Liaison MS1/MS2 corrigée

Ce ZIP contient le Microservice 2 corrigé pour accepter les JWT générés par le Microservice 1 Auth.

## Corrections appliquées

1. MS2 écoute sur le port `8082`.
2. Le secret JWT est exactement aligné avec MS1 :
   `bloodflow-secret-key-for-demo-change-this-later-123456789123456789`
3. `JwtUtil` lit le secret comme texte brut, pas en Base64.
4. Les rôles MS2 sont alignés avec les rôles MS1 :
   `ADMIN`, `DONOR`, `PATIENT`, `DOCTOR`, `STAFF`, `LAB_TECHNICIAN`, `BIOLOGIST`, `DELIVERY_AGENT`, `PROMOTER`.
5. Le filtre JWT ajoute automatiquement le préfixe `ROLE_` pour Spring Security.
6. CORS est configuré pour React/Vite (`5173`, `5174`) et React classique (`3000`).
7. Health check ajouté :
   - `GET http://localhost:8082/api/health`
   - `GET http://localhost:8082/api/medical/health`

## Ordre de test

### 1. Lancer MS1 Auth

MS1 doit tourner sur :

```txt
http://localhost:8081
```

Puis login admin :

```json
{
  "email": "admin@bloodflow.ma",
  "password": "Admin123@"
}
```

Copier `data.accessToken`.

### 2. Lancer PostgreSQL

Base attendue :

```txt
bloodflow_medical
```

Créer la base si nécessaire :

```sql
CREATE DATABASE bloodflow_medical;
```

### 3. Lancer MS2

```bash
mvn clean install
mvn spring-boot:run
```

Swagger :

```txt
http://localhost:8082/swagger-ui.html
```

Health :

```txt
http://localhost:8082/api/health
```

### 4. Tester un endpoint MS2 avec le token MS1

Dans Swagger MS2, cliquer sur **Authorize**, puis mettre :

```txt
Bearer <TOKEN_MS1>
```

Tester ensuite un endpoint `GET /api/...`.

Si le token vient de MS1 et que le secret est le même, MS2 doit accepter la requête.
