# BloodFlow MS3 avec MySQL

Cette version utilise **ASP.NET Core 8 + EF Core + MySQL + SignalR + Memory Cache**.

## 1. Créer la base MySQL

Dans MySQL Workbench ou terminal MySQL :

```sql
CREATE DATABASE IF NOT EXISTS bloodflow_ms3 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Ou exécute directement `create-database.sql`.

## 2. Modifier le mot de passe

Dans `appsettings.json` et `appsettings.Development.json`, remplace :

```txt
TON_MOT_DE_PASSE_MYSQL
```

par ton mot de passe MySQL. Si ton utilisateur root n'a pas de mot de passe, mets :

```txt
Password=;
```

## 3. Lancer le backend

```bash
dotnet restore
dotnet clean
dotnet build
dotnet run
```

Le projet crée les tables automatiquement grâce à `db.Database.EnsureCreated()` dans `Program.cs`.

## 4. Tester

Ouvre :

```txt
http://localhost:8083/swagger
```

ou selon le port affiché dans ton terminal.
