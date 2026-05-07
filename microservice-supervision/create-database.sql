CREATE DATABASE IF NOT EXISTS bloodflow_ms3
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bloodflow_ms3;

CREATE TABLE IF NOT EXISTS SystemLogs (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ServiceName VARCHAR(120) NOT NULL,
    Level VARCHAR(50) NOT NULL,
    Message VARCHAR(1000) NOT NULL,
    CreatedAt DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX IX_SystemLogs_ServiceName (ServiceName),
    INDEX IX_SystemLogs_CreatedAt (CreatedAt)
);

CREATE TABLE IF NOT EXISTS Alerts (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(200) NOT NULL,
    Description VARCHAR(1000) NOT NULL,
    Severity VARCHAR(30) NOT NULL,
    SourceService VARCHAR(120) NOT NULL,
    IsRead BOOLEAN NOT NULL DEFAULT FALSE,
    CreatedAt DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX IX_Alerts_CreatedAt (CreatedAt)
);

CREATE TABLE IF NOT EXISTS Notifications (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId VARCHAR(100) NOT NULL,
    TargetRole VARCHAR(80) NULL,
    Title VARCHAR(200) NOT NULL,
    Message VARCHAR(1000) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    Priority VARCHAR(30) NOT NULL,
    IsRead BOOLEAN NOT NULL DEFAULT FALSE,
    CreatedAt DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX IX_Notifications_UserId (UserId),
    INDEX IX_Notifications_CreatedAt (CreatedAt)
);

CREATE TABLE IF NOT EXISTS Reports (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ReportType VARCHAR(100) NOT NULL,
    Content LONGTEXT NOT NULL,
    Format VARCHAR(20) NOT NULL,
    GeneratedByUserId VARCHAR(100) NULL,
    GeneratedAt DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS SupervisedServices (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(120) NOT NULL,
    BaseUrl VARCHAR(300) NOT NULL,
    HealthEndpoint VARCHAR(200) NOT NULL,
    Status VARCHAR(30) NOT NULL,
    LastCheckAt DATETIME(6) NULL,
    ResponseTimeMs INT NULL
);

INSERT INTO SupervisedServices (Id, Name, BaseUrl, HealthEndpoint, Status)
VALUES
    (1, 'MS1 Auth', 'http://localhost:8081', '/api/health', 'Unknown'),
    (2, 'MS2 Medical', 'http://localhost:8082', '/api/health', 'Unknown')
ON DUPLICATE KEY UPDATE
    Name = VALUES(Name),
    BaseUrl = VALUES(BaseUrl),
    HealthEndpoint = VALUES(HealthEndpoint),
    Status = VALUES(Status);
