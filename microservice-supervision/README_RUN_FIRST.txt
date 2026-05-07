BloodFlow MS3 - version MySQL corrigée

1) Ouvre MySQL Workbench ou phpMyAdmin et exécute :

CREATE DATABASE IF NOT EXISTS bloodflow_ms3
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

2) appsettings.json est déjà configuré pour MySQL local sans mot de passe :
Server=localhost;Port=3306;Database=bloodflow_ms3;User=root;Password=;

Si ton MySQL a un mot de passe, remplace Password=; par Password=TON_MOT_DE_PASSE;

3) Dans le dossier du projet :

dotnet restore
dotnet clean
dotnet build
dotnet run

4) Ouvre Swagger avec l URL affichée par le terminal, par exemple :
http://localhost:8083/swagger

Correction importante effectuée :
- Microsoft.EntityFrameworkCore passé de 8.0.11 à 8.0.13 pour être compatible avec Pomelo.EntityFrameworkCore.MySql 8.0.3.
- SQL Server supprimé.
- UseSqlServer remplacé par UseMySql.
