# KnowGrow Backend (Spring Boot 3 + MySQL 8)

## Requirements
Java 21, Maven (IntelliJ includes it), MySQL 8

## One-time database setup
In a terminal:  mysql -u root -p     (enter your MySQL root password), then run:
    CREATE DATABASE knowgrow_app CHARACTER SET utf8mb4;
    CREATE USER 'knowgrow'@'localhost' IDENTIFIED BY 'knowgrow_dev';
    GRANT ALL PRIVILEGES ON knowgrow_app.* TO 'knowgrow'@'localhost';
    EXIT;

## Run
Run KnowGrowApplication in IntelliJ (Flyway creates the tables; 5 demo users are seeded).
Check: http://localhost:8080/api/v1/ping  ->  {"status":"ok"}

## Connect the frontend
knowgrow-frontend/.env:  VITE_API_BASE_URL=http://localhost:8080/api/v1
Use "Switch to Live Backend Login" and sign in with hr@knowgrow.demo / demo12345.

## Endpoints
POST /auth/login · POST /auth/register · GET /auth/session · POST /auth/logout
GET|PUT /skills/me · GET|PUT /learning/me · GET /notifications · PUT /notifications/read-state

## Production checklist
Set JWT_SECRET (32+ random chars), COOKIE_SECURE=true, SEED_DEMO_USERS=false, CORS_ORIGINS=<your site>, use HTTPS.
