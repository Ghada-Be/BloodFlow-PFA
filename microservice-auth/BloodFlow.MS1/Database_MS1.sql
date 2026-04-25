-- =====================================================================
-- BloodFlow MS1 - Script de création de la base de données SQL Server
-- Base : MS1
-- =====================================================================

-- 1. Créer la base de données MS1
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'MS1')
BEGIN
    CREATE DATABASE MS1;
    PRINT 'Base de données MS1 créée.';
END
GO

USE MS1;
GO

-- =====================================================================
-- 2. Table Roles
-- =====================================================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Roles' AND xtype='U')
BEGIN
    CREATE TABLE Roles (
        Id          INT IDENTITY(1,1) PRIMARY KEY,
        Name        NVARCHAR(50)  NOT NULL,
        Description NVARCHAR(200) NULL,
        CONSTRAINT UQ_Roles_Name UNIQUE (Name)
    );
    PRINT 'Table Roles créée.';
END
GO

-- =====================================================================
-- 3. Table Users
-- =====================================================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Users' AND xtype='U')
BEGIN
    CREATE TABLE Users (
        Id                  INT IDENTITY(1,1) PRIMARY KEY,
        FirstName           NVARCHAR(100) NOT NULL,
        LastName            NVARCHAR(100) NOT NULL,
        Email               NVARCHAR(150) NOT NULL,
        PasswordHash        NVARCHAR(MAX) NOT NULL,
        PhoneNumber         NVARCHAR(20)  NULL,
        IsActive            BIT           NOT NULL DEFAULT 1,
        IsEmailVerified     BIT           NOT NULL DEFAULT 0,
        FailedLoginAttempts INT           NOT NULL DEFAULT 0,
        LockoutEnd          DATETIME2     NULL,
        CreatedAt           DATETIME2     NOT NULL DEFAULT GETUTCDATE(),
        UpdatedAt           DATETIME2     NULL,
        CONSTRAINT UQ_Users_Email UNIQUE (Email)
    );
    PRINT 'Table Users créée.';
END
GO

-- =====================================================================
-- 4. Table UserRoles (Many-to-Many)
-- =====================================================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='UserRoles' AND xtype='U')
BEGIN
    CREATE TABLE UserRoles (
        UserId     INT       NOT NULL,
        RoleId     INT       NOT NULL,
        AssignedAt DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
        CONSTRAINT PK_UserRoles PRIMARY KEY (UserId, RoleId),
        CONSTRAINT FK_UserRoles_Users FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE,
        CONSTRAINT FK_UserRoles_Roles FOREIGN KEY (RoleId) REFERENCES Roles(Id) ON DELETE CASCADE
    );
    PRINT 'Table UserRoles créée.';
END
GO

-- =====================================================================
-- 5. Table RefreshTokens
-- =====================================================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='RefreshTokens' AND xtype='U')
BEGIN
    CREATE TABLE RefreshTokens (
        Id          INT IDENTITY(1,1) PRIMARY KEY,
        Token       NVARCHAR(512) NOT NULL,
        CreatedAt   DATETIME2     NOT NULL DEFAULT GETUTCDATE(),
        ExpiresAt   DATETIME2     NOT NULL,
        IsRevoked   BIT           NOT NULL DEFAULT 0,
        RevokedAt   DATETIME2     NULL,
        CreatedByIp NVARCHAR(45)  NULL,
        RevokedByIp NVARCHAR(45)  NULL,
        UserId      INT           NOT NULL,
        CONSTRAINT FK_RefreshTokens_Users FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE
    );
    PRINT 'Table RefreshTokens créée.';
END
GO

-- =====================================================================
-- 6. Table LoginLogs
-- =====================================================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='LoginLogs' AND xtype='U')
BEGIN
    CREATE TABLE LoginLogs (
        Id            INT IDENTITY(1,1) PRIMARY KEY,
        UserId        INT           NULL,
        Email         NVARCHAR(150) NOT NULL,
        IsSuccess     BIT           NOT NULL,
        FailureReason NVARCHAR(200) NULL,
        IpAddress     NVARCHAR(45)  NULL,
        AttemptedAt   DATETIME2     NOT NULL DEFAULT GETUTCDATE(),
        CONSTRAINT FK_LoginLogs_Users FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE SET NULL
    );
    PRINT 'Table LoginLogs créée.';
END
GO

-- =====================================================================
-- 7. Table PasswordResetTokens
-- =====================================================================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='PasswordResetTokens' AND xtype='U')
BEGIN
    CREATE TABLE PasswordResetTokens (
        Id        INT IDENTITY(1,1) PRIMARY KEY,
        Token     NVARCHAR(512) NOT NULL,
        CreatedAt DATETIME2     NOT NULL DEFAULT GETUTCDATE(),
        ExpiresAt DATETIME2     NOT NULL,
        IsUsed    BIT           NOT NULL DEFAULT 0,
        UserId    INT           NOT NULL,
        CONSTRAINT FK_PasswordResetTokens_Users FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE
    );
    PRINT 'Table PasswordResetTokens créée.';
END
GO

-- =====================================================================
-- 8. Seed des 9 rôles BloodFlow
-- =====================================================================
IF NOT EXISTS (SELECT * FROM Roles)
BEGIN
    INSERT INTO Roles (Name, Description) VALUES
        ('Admin',         'Administrateur du système'),
        ('Donor',         'Donneur de sang'),
        ('Patient',       'Patient receveur'),
        ('Doctor',        'Médecin'),
        ('Staff',         'Personnel hospitalier'),
        ('LabTechnician', 'Technicien de laboratoire'),
        ('Biologist',     'Biologiste'),
        ('DeliveryAgent', 'Agent de livraison'),
        ('Promoter',      'Promoteur de campagnes de don');
    PRINT 'Rôles BloodFlow insérés.';
END
GO

-- =====================================================================
-- 9. Vérification
-- =====================================================================
SELECT 'Tables créées :' AS Info;
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';

SELECT 'Rôles disponibles :' AS Info;
SELECT * FROM Roles;
GO
