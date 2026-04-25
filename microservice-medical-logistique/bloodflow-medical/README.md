# 🩸 BloodFlow — Medical Service (Port 8083)

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)

Microservice médical du système BloodFlow. Gère les dossiers patients, analyses, prescriptions, commandes de sang et livraisons.

---

## 🚀 Lancement rapide

### Option 1 — IntelliJ + Docker (recommandé)

```bash
# 1. Lancer uniquement PostgreSQL
docker-compose up -d postgres-medical

# 2. Lancer l'app depuis IntelliJ (profil : dev)
# Ouvrir BloodFlowMedicalApplication.java → ▶️
```

### Option 2 — Tout avec Docker

```bash
docker-compose up -d
```

---

## 📋 Endpoints

| Méthode | URL | Description |
|---------|-----|-------------|
| GET/POST | `/api/analyses-sang` | Analyses biologiques |
| GET/POST | `/api/commandes-sang` | Commandes de sang |
| GET/POST | `/api/dossiers-medicaux` | Dossiers patients |
| GET/POST | `/api/livraisons` | Livraisons |
| GET/POST | `/api/poches-sang` | Poches de sang |
| GET/POST | `/api/prescriptions` | Prescriptions médicales |
| GET/POST | `/api/resultats-biologiques` | Résultats d'analyses |
| GET/POST | `/api/stocks` | Stock sanguin |

**Swagger UI :** http://localhost:8083/swagger-ui.html

---

## 🔐 Authentification

MS3 ne gère pas le login. Il reçoit le JWT de MS1 :

```
Authorization: Bearer <token_de_MS1>
```

Le JWT_SECRET doit être **identique** dans MS1 et MS3.

---

## 🗄️ Base de données

- **Nom :** `bloodflow_medical`
- **Port local :** `5434` (pour éviter conflit avec MS1:5432 et MS2:5433)

---

## 🧪 Tests

```bash
mvn test
```

Tests inclus : service (Mockito), controller (@WebMvcTest), repository (@DataJpaTest), intégration (@SpringBootTest).
