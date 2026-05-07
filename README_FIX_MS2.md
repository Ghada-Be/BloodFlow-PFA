# BloodFlow MS2 - Version corrigée

Cette version corrige les problèmes Maven/test observés dans le microservice 2.

## Corrections appliquées

1. `CommandeSangControllerTest` : ajout du mock `JwtUtil` pour que `JwtAuthenticationFilter` puisse démarrer dans `@WebMvcTest`.
2. `CommandeSangControllerTest` : remplacement des anciens rôles de test par les rôles compatibles avec `SecurityConfig` : `DOCTOR` et `STAFF`.
3. `AnalyseSangControllerTest` : ajout du mock `JwtUtil`.
4. `AnalyseSangControllerTest` : remplacement du rôle `TECHNICIEN` par `LAB_TECHNICIAN`.
5. `AnalyseSangControllerTest` : correction du test sans authentification : Spring Security renvoie `401 Unauthorized`, pas `403 Forbidden`, lorsqu'il n'y a aucun token.
6. `AnalyseSangServiceTest` : suppression du stubbing Mockito inutile qui causait `UnnecessaryStubbingException`.

## Lancer en local

Créer la base PostgreSQL :

```sql
CREATE DATABASE bloodflow_medical;
```

Puis lancer :

```powershell
mvn clean install
mvn spring-boot:run
```

Swagger :

```text
http://localhost:8082/swagger-ui.html
```

Health check :

```text
http://localhost:8082/api/health
```

## Lancer avec Docker

```powershell
docker compose down -v
docker compose up --build
```

Swagger Docker :

```text
http://localhost:8082/swagger-ui.html
```

## Notes importantes

- MS2 utilise PostgreSQL.
- Le port backend est `8082`.
- Docker expose PostgreSQL sur `5434` pour éviter les conflits avec un PostgreSQL local.
- Le JWT secret est aligné avec MS1 : `bloodflow-secret-key-for-demo-change-this-later-123456789123456789`.
