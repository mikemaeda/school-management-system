# School Teaching & Syllabus Tracking System

A Java Swing desktop app for organizing teaching tasks, tracking syllabus progress, and collecting feedback from teachers and students.

## Current Version

This version is a cleaned-up rebuild of the original NetBeans project. The app now uses a clearer Java structure with UI, service, DAO, model, database, and security layers.

## Core Features

- Role-based login for:
  - Head of School
  - Teacher
  - Student
- Account registration with password hashing and stronger password validation.
- Default Head of School account for first run, with a required password change after the first login.
- Head of School dashboard with:
  - Overview metrics
  - Teacher task assignment
  - All task tracking
  - User list
  - Teacher feedback review
  - Student feedback review
  - Optional email broadcast to teachers
  - CSV exports for users, tasks, teacher feedback, and student feedback
- Teacher dashboard with:
  - Assigned task list
  - Task status updates
  - Lesson progress/reflection submission
- Student dashboard with:
  - Lesson reflection form
  - Teacher/subject feedback ratings
- SQLite database with automatic table creation, light migration support, foreign keys, and busy-timeout protection.

## Default Login

When the app starts for the first time, it creates a default Head of School account:

```text
Email: admin@school.local
Password: Admin123!
```

Use this account to sign in. The app will immediately ask you to replace the default password. After that, create teacher and student accounts from the registration page.

## Project Files

- `src/main/java/schoolmanagement/SchoolManagementSystem.java` - main Swing user interface.
- `src/main/java/schoolmanagement/service/` - application rules, email delivery, and CSV export helpers.
- `src/main/java/schoolmanagement/dao/` - database access objects for users, tasks, and feedback.
- `src/main/java/schoolmanagement/db/DBConnector.java` - SQLite database connection and automatic setup.
- `src/main/java/schoolmanagement/security/PasswordUtils.java` - PBKDF2 password hashing and verification.
- `src/main/java/schoolmanagement/model/` - simple app models used by the UI.
- `docs/ARCHITECTURE.md` - short explanation of the code organization.
- `database_schema.sql` - database schema reference.
- `pom.xml` - Maven build file and dependencies.
- `.env.example` - optional local configuration.
- `Design..pdf`, `Development..pdf`, `Evaluation..pdf` - original documentation.

## Requirements

- Java JDK 17 or newer
- Maven

## Run The App

From this folder:

```powershell
mvn clean compile
mvn exec:java
```

If Java is not installed system-wide, use the included helper script after the portable tools have been downloaded:

```powershell
.\run-school-app.ps1
```

You can also run a quick database smoke test:

```powershell
mvn "-Dapp.mainClass=schoolmanagement.AppSmokeTest" exec:java
```

The app creates this database file automatically:

```text
school_management.db
```

## Optional Environment Variables

The default SQLite settings work without extra configuration.

```powershell
$env:DB_DRIVER = "org.sqlite.JDBC"
$env:DB_URL = "jdbc:sqlite:school_management.db"
```

Email broadcast requires SMTP settings:

```powershell
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"
$env:SMTP_FROM = "your_email@gmail.com"
$env:SMTP_PASSWORD = "your_gmail_app_password"
```

You can also pass configuration through Java system properties, which is useful for tests:

```powershell
mvn "-DDB_URL=jdbc:sqlite:test-school.db" exec:java
```

## Security Notes

- No real passwords are stored in source code.
- New user passwords are stored as PBKDF2 hashes.
- The first-run admin password must be changed before using the app.
- `.env`, `.db`, and build output files are ignored by Git.

## Next Improvements

Good future upgrades:

- Add teacher task comments and due-date reminders.
- Add chart summaries for syllabus coverage.
- Split the Swing UI into smaller view classes if the interface keeps growing.
- Add a packaged installer once the app is ready to share outside VS Code.
