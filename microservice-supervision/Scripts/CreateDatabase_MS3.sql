-- ============================================================
-- SCRIPT SQL SERVER - BASE DE DONNÉES BloodFlow MS3
-- Serveur  : DESKTOP-TON57SE
-- Base     : MS3
-- Auteur   : BloodFlow PFA - Microservice 3
-- ============================================================

-- ─── Création de la base de données ──────────────────────────────────────────
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'MS3')
BEGIN
    CREATE DATABASE [MS3];
    PRINT 'Base de données MS3 créée avec succès.';
END
ELSE
BEGIN
    PRINT 'La base de données MS3 existe déjà.';
END
GO

USE [MS3];
GO

-- ─── Table : Administrateurs ─────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Administrateurs')
BEGIN
    CREATE TABLE [dbo].[Administrateurs] (
        [Id]            INT            NOT NULL IDENTITY(1,1),
        [Nom]           NVARCHAR(100)  NOT NULL,
        [Prenom]        NVARCHAR(100)  NOT NULL,
        [Email]         NVARCHAR(200)  NOT NULL,
        [Actif]         BIT            NOT NULL DEFAULT 1,
        [UserIdMs1]     NVARCHAR(100)  NOT NULL,
        [DateCreation]  DATETIME2      NOT NULL DEFAULT GETUTCDATE(),
        CONSTRAINT [PK_Administrateurs] PRIMARY KEY ([Id]),
        CONSTRAINT [UQ_Administrateurs_Email] UNIQUE ([Email])
    );
    PRINT 'Table Administrateurs créée.';
END
GO

-- ─── Table : AgentsPromoteurs ─────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'AgentsPromoteurs')
BEGIN
    CREATE TABLE [dbo].[AgentsPromoteurs] (
        [Id]            INT            NOT NULL IDENTITY(1,1),
        [Nom]           NVARCHAR(100)  NOT NULL,
        [Prenom]        NVARCHAR(100)  NOT NULL,
        [Email]         NVARCHAR(200)  NOT NULL,
        [Actif]         BIT            NOT NULL DEFAULT 1,
        [UserIdMs1]     NVARCHAR(100)  NOT NULL,
        [DateCreation]  DATETIME2      NOT NULL DEFAULT GETUTCDATE(),
        CONSTRAINT [PK_AgentsPromoteurs] PRIMARY KEY ([Id]),
        CONSTRAINT [UQ_AgentsPromoteurs_Email] UNIQUE ([Email])
    );
    PRINT 'Table AgentsPromoteurs créée.';
END
GO

-- ─── Table : ServicesSurveilles ───────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ServicesSurveilles')
BEGIN
    CREATE TABLE [dbo].[ServicesSurveilles] (
        [Id]                        INT            NOT NULL IDENTITY(1,1),
        [NomService]                NVARCHAR(150)  NOT NULL,
        [UrlHealthCheck]            NVARCHAR(500)  NOT NULL,
        [Etat]                      NVARCHAR(50)   NOT NULL DEFAULT 'Inconnu',
        [DateDerniereVerification]  DATETIME2      NULL,
        [DerniereLatenceMs]         INT            NULL,
        [MessageEtat]               NVARCHAR(500)  NULL,
        CONSTRAINT [PK_ServicesSurveilles] PRIMARY KEY ([Id])
    );
    PRINT 'Table ServicesSurveilles créée.';
END
GO

-- ─── Table : JournalSysteme ───────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'JournalSysteme')
BEGIN
    CREATE TABLE [dbo].[JournalSysteme] (
        [Id]              INT             NOT NULL IDENTITY(1,1),
        [DateEvenement]   DATETIME2       NOT NULL DEFAULT GETUTCDATE(),
        [Niveau]          NVARCHAR(50)    NOT NULL,
        [Source]          NVARCHAR(200)   NOT NULL,
        [Message]         NVARCHAR(MAX)   NOT NULL,
        [Details]         NVARCHAR(MAX)   NULL,
        [CorrelationId]   NVARCHAR(50)    NULL,
        CONSTRAINT [PK_JournalSysteme] PRIMARY KEY ([Id])
    );
    CREATE INDEX [IX_JournalSysteme_Niveau] ON [dbo].[JournalSysteme] ([Niveau]);
    CREATE INDEX [IX_JournalSysteme_DateEvenement] ON [dbo].[JournalSysteme] ([DateEvenement] DESC);
    PRINT 'Table JournalSysteme créée.';
END
GO

-- ─── Table : Alertes ──────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Alertes')
BEGIN
    CREATE TABLE [dbo].[Alertes] (
        [Id]                  INT            NOT NULL IDENTITY(1,1),
        [DateAlerte]          DATETIME2      NOT NULL DEFAULT GETUTCDATE(),
        [NiveauUrgence]       NVARCHAR(50)   NOT NULL,
        [Titre]               NVARCHAR(200)  NOT NULL,
        [Description]         NVARCHAR(MAX)  NOT NULL,
        [Etat]                NVARCHAR(50)   NOT NULL DEFAULT 'Ouverte',
        [ServiceSurveilleId]  INT            NULL,
        [CreeeParSysteme]     BIT            NOT NULL DEFAULT 0,
        CONSTRAINT [PK_Alertes] PRIMARY KEY ([Id]),
        CONSTRAINT [FK_Alertes_ServicesSurveilles]
            FOREIGN KEY ([ServiceSurveilleId])
            REFERENCES [dbo].[ServicesSurveilles]([Id])
            ON DELETE SET NULL
    );
    CREATE INDEX [IX_Alertes_Etat] ON [dbo].[Alertes] ([Etat]);
    CREATE INDEX [IX_Alertes_NiveauUrgence] ON [dbo].[Alertes] ([NiveauUrgence]);
    PRINT 'Table Alertes créée.';
END
GO

-- ─── Table : Campagnes ────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Campagnes')
BEGIN
    CREATE TABLE [dbo].[Campagnes] (
        [Id]                INT            NOT NULL IDENTITY(1,1),
        [Titre]             NVARCHAR(200)  NOT NULL,
        [Objectif]          NVARCHAR(500)  NOT NULL,
        [Description]       NVARCHAR(MAX)  NOT NULL,
        [DateDebut]         DATETIME2      NOT NULL,
        [DateFin]           DATETIME2      NOT NULL,
        [Statut]            NVARCHAR(50)   NOT NULL DEFAULT 'Brouillon',
        [AgentPromoteurId]  INT            NOT NULL,
        CONSTRAINT [PK_Campagnes] PRIMARY KEY ([Id]),
        CONSTRAINT [FK_Campagnes_AgentsPromoteurs]
            FOREIGN KEY ([AgentPromoteurId])
            REFERENCES [dbo].[AgentsPromoteurs]([Id])
            ON DELETE RESTRICT
    );
    PRINT 'Table Campagnes créée.';
END
GO

-- ─── Table : CollectesSang ────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CollectesSang')
BEGIN
    CREATE TABLE [dbo].[CollectesSang] (
        [Id]                INT            NOT NULL IDENTITY(1,1),
        [Lieu]              NVARCHAR(300)  NOT NULL,
        [Ville]             NVARCHAR(100)  NOT NULL,
        [DateCollecte]      DATETIME2      NOT NULL,
        [HeureDebut]        TIME           NOT NULL,
        [HeureFin]          TIME           NOT NULL,
        [ObjectifPoches]    INT            NOT NULL,
        [Statut]            NVARCHAR(50)   NOT NULL DEFAULT 'Planifiee',
        [CampagneId]        INT            NULL,
        [AgentPromoteurId]  INT            NOT NULL,
        CONSTRAINT [PK_CollectesSang] PRIMARY KEY ([Id]),
        CONSTRAINT [FK_CollectesSang_Campagnes]
            FOREIGN KEY ([CampagneId])
            REFERENCES [dbo].[Campagnes]([Id])
            ON DELETE SET NULL,
        CONSTRAINT [FK_CollectesSang_AgentsPromoteurs]
            FOREIGN KEY ([AgentPromoteurId])
            REFERENCES [dbo].[AgentsPromoteurs]([Id])
            ON DELETE NO ACTION
    );
    CREATE INDEX [IX_CollectesSang_Ville] ON [dbo].[CollectesSang] ([Ville]);
    CREATE INDEX [IX_CollectesSang_DateCollecte] ON [dbo].[CollectesSang] ([DateCollecte]);
    PRINT 'Table CollectesSang créée.';
END
GO

-- ─── Table : Benevoles ────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Benevoles')
BEGIN
    CREATE TABLE [dbo].[Benevoles] (
        [Id]              INT            NOT NULL IDENTITY(1,1),
        [Nom]             NVARCHAR(100)  NOT NULL,
        [Prenom]          NVARCHAR(100)  NOT NULL,
        [Contact]         NVARCHAR(200)  NOT NULL,
        [Email]           NVARCHAR(200)  NULL,
        [Disponibilite]   NVARCHAR(100)  NOT NULL,
        [CollecteSangId]  INT            NULL,
        CONSTRAINT [PK_Benevoles] PRIMARY KEY ([Id]),
        CONSTRAINT [FK_Benevoles_CollectesSang]
            FOREIGN KEY ([CollecteSangId])
            REFERENCES [dbo].[CollectesSang]([Id])
            ON DELETE SET NULL
    );
    PRINT 'Table Benevoles créée.';
END
GO

-- ─── Table : Notifications ────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Notifications')
BEGIN
    CREATE TABLE [dbo].[Notifications] (
        [Id]            INT             NOT NULL IDENTITY(1,1),
        [DateEnvoi]     DATETIME2       NOT NULL DEFAULT GETUTCDATE(),
        [Message]       NVARCHAR(MAX)   NOT NULL,
        [Type]          NVARCHAR(50)    NOT NULL DEFAULT 'Information',
        [Canal]         NVARCHAR(50)    NOT NULL DEFAULT 'Email',
        [StatutEnvoi]   NVARCHAR(50)    NOT NULL DEFAULT 'EnAttente',
        [Destinataire]  NVARCHAR(300)   NOT NULL,
        [AlerteId]      INT             NULL,
        [CampagneId]    INT             NULL,
        CONSTRAINT [PK_Notifications] PRIMARY KEY ([Id]),
        CONSTRAINT [FK_Notifications_Alertes]
            FOREIGN KEY ([AlerteId])
            REFERENCES [dbo].[Alertes]([Id])
            ON DELETE SET NULL,
        CONSTRAINT [FK_Notifications_Campagnes]
            FOREIGN KEY ([CampagneId])
            REFERENCES [dbo].[Campagnes]([Id])
            ON DELETE SET NULL
    );
    PRINT 'Table Notifications créée.';
END
GO

-- ─── Table : AppelsUrgents ────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'AppelsUrgents')
BEGIN
    CREATE TABLE [dbo].[AppelsUrgents] (
        [Id]                           INT            NOT NULL IDENTITY(1,1),
        [DateAppel]                    DATETIME2      NOT NULL DEFAULT GETUTCDATE(),
        [GroupeSanguin]                NVARCHAR(10)   NOT NULL,
        [Ville]                        NVARCHAR(100)  NOT NULL,
        [Message]                      NVARCHAR(MAX)  NOT NULL,
        [Priorite]                     NVARCHAR(50)   NOT NULL DEFAULT 'Haute',
        [EstActif]                     BIT            NOT NULL DEFAULT 1,
        [NombreNotificationsEnvoyees]  INT            NOT NULL DEFAULT 0,
        [CreeParAdminId]               INT            NOT NULL,
        CONSTRAINT [PK_AppelsUrgents] PRIMARY KEY ([Id])
    );
    PRINT 'Table AppelsUrgents créée.';
END
GO

-- ─── Table : Rapports ─────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Rapports')
BEGIN
    CREATE TABLE [dbo].[Rapports] (
        [Id]                INT            NOT NULL IDENTITY(1,1),
        [DateGeneration]    DATETIME2      NOT NULL DEFAULT GETUTCDATE(),
        [Type]              NVARCHAR(100)  NOT NULL,
        [Contenu]           NVARCHAR(MAX)  NOT NULL,
        [Format]            NVARCHAR(20)   NOT NULL DEFAULT 'JSON',
        [CreeParAdminId]    INT            NOT NULL,
        CONSTRAINT [PK_Rapports] PRIMARY KEY ([Id]),
        CONSTRAINT [FK_Rapports_Administrateurs]
            FOREIGN KEY ([CreeParAdminId])
            REFERENCES [dbo].[Administrateurs]([Id])
            ON DELETE RESTRICT
    );
    PRINT 'Table Rapports créée.';
END
GO

-- ─── DONNÉES DE TEST (SEED DATA) ─────────────────────────────────────────────

-- Admin
IF NOT EXISTS (SELECT 1 FROM [dbo].[Administrateurs] WHERE [Email] = 'admin@bloodflow.ma')
BEGIN
    INSERT INTO [dbo].[Administrateurs] ([Nom],[Prenom],[Email],[Actif],[UserIdMs1],[DateCreation])
    VALUES ('Benali','Fatima','admin@bloodflow.ma',1,'user-ms1-001','2024-01-01');
    PRINT 'Admin seed inséré.';
END
GO

-- Agent Promoteur
IF NOT EXISTS (SELECT 1 FROM [dbo].[AgentsPromoteurs] WHERE [Email] = 'agent@bloodflow.ma')
BEGIN
    INSERT INTO [dbo].[AgentsPromoteurs] ([Nom],[Prenom],[Email],[Actif],[UserIdMs1],[DateCreation])
    VALUES ('Idrissi','Youssef','agent@bloodflow.ma',1,'user-ms1-002','2024-01-01');
    PRINT 'Agent promoteur seed inséré.';
END
GO

-- Services surveillés
IF NOT EXISTS (SELECT 1 FROM [dbo].[ServicesSurveilles] WHERE [NomService] = 'Microservice 1 - Utilisateurs')
BEGIN
    INSERT INTO [dbo].[ServicesSurveilles]
        ([NomService],[UrlHealthCheck],[Etat],[DateDerniereVerification],[DerniereLatenceMs],[MessageEtat])
    VALUES
        ('Microservice 1 - Utilisateurs','http://localhost:5001/health','Disponible','2024-01-01',45,'Service opérationnel'),
        ('Microservice 2 - Médical','http://localhost:5002/health','Disponible','2024-01-01',60,'Service opérationnel');
    PRINT 'Services surveillés seed insérés.';
END
GO

-- Alerte
IF NOT EXISTS (SELECT 1 FROM [dbo].[Alertes] WHERE [Titre] = 'Stock de sang O- critique')
BEGIN
    INSERT INTO [dbo].[Alertes]
        ([DateAlerte],[NiveauUrgence],[Titre],[Description],[Etat],[ServiceSurveilleId],[CreeeParSysteme])
    VALUES
        ('2024-01-15 10:00:00','Élevé','Stock de sang O- critique',
         'Le stock de sang de groupe O- est descendu sous le seuil critique.',
         'Ouverte',2,1);
    PRINT 'Alerte seed insérée.';
END
GO

-- Campagne
IF NOT EXISTS (SELECT 1 FROM [dbo].[Campagnes] WHERE [Titre] = 'Campagne Ramadan 2024')
BEGIN
    INSERT INTO [dbo].[Campagnes]
        ([Titre],[Objectif],[Description],[DateDebut],[DateFin],[Statut],[AgentPromoteurId])
    VALUES
        ('Campagne Ramadan 2024',
         'Collecter 200 poches de sang pendant Ramadan',
         'Campagne nationale de sensibilisation au don de sang pendant le mois sacré.',
         '2024-03-11','2024-04-09','Terminee',1);
    PRINT 'Campagne seed insérée.';
END
GO

-- Collecte
IF NOT EXISTS (SELECT 1 FROM [dbo].[CollectesSang] WHERE [Lieu] = 'Faculté des Sciences - Amphithéâtre A')
BEGIN
    INSERT INTO [dbo].[CollectesSang]
        ([Lieu],[Ville],[DateCollecte],[HeureDebut],[HeureFin],[ObjectifPoches],[Statut],[CampagneId],[AgentPromoteurId])
    VALUES
        ('Faculté des Sciences - Amphithéâtre A','Oujda','2024-03-20',
         '09:00:00','17:00:00',50,'Terminee',1,1);
    PRINT 'Collecte seed insérée.';
END
GO

PRINT '=== Script SQL MS3 terminé avec succès ===';
GO
