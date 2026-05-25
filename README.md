# School Teaching & Syllabus Tracking System

A Java Swing desktop app for organizing teaching tasks, tracking syllabus progress, and collecting feedback from teachers and students.

This project is intentionally a desktop application. It preserves the original sign-in, registration, and role-based dashboard flow instead of replacing it with a generic web dashboard.

## What The App Does

- Provides role-based login for:
  - Head of School
  - Teacher
  - Student
- Supports account registration with password hashing and stronger password validation
- Creates a default Head of School account on first run
- Requires the default admin password to be changed after first login
- Lets the Head of School:
  - view dashboard metrics
  - assign teacher tasks
  - track all tasks
  - view users
  - review teacher feedback
  - review student feedback
  - optionally broadcast email to teachers
  - export users, tasks, and feedback to CSV
- Lets teachers:
  - view assigned tasks
  - update task status
  - submit lesson progress/reflections
- Lets students:
  - submit lesson reflections
  - rate teacher/subject feedback
- Uses SQLite with automatic table creation and light migration support

## Default Login

When the app starts for the first time, it creates a default Head of School account:

```text
Email: admin@school.local
Password: Admin123!
```

After logging in, the app prompts you to replace the default password.

## Quick Start

### Fastest Option: Build A Runnable App File

Requirements:

- Java JDK 17 or newer
- Maven

From this folder:

```powershell
mvn clean package
java -jar target\school-management-system.jar
```

The packaged `.jar` includes the app dependencies and opens the original desktop interface.

On Windows, you can also run:

```powershell
.\run-packaged-app.ps1
```

### Option 1: Run With Maven

Requirements:

- Java JDK 17 or newer
- Maven

From this folder:

```powershell
mvn clean compile
mvn exec:java
```

### Option 2: Run With The Included Helper Script

If you are using the portable Java/Maven tools folder included beside this project, run:

```powershell
.\run-school-app.ps1
```

## Shareable Download Instructions

This project is best shared as a downloadable desktop app/project. Users can click the green **Code** button on GitHub, download the ZIP, unzip it, and run:

1. Download or clone this repository.
2. Open the folder in VS Code, IntelliJ, NetBeans, or a terminal.
3. Make sure Java 17+ and Maven are installed, or use the helper script if the portable tools folder is available.
4. Build and open the app:

```powershell
mvn clean package
java -jar target\school-management-system.jar
```

The app opens as the original desktop window with the same sign-in, registration, and role-based dashboards.

## Smoke Test

Run a quick database and workflow smoke test:

```powershell
mvn "-Dapp.mainClass=schoolmanagement.AppSmokeTest" exec:java
```

Expected result:

```text
Smoke test passed.
```

## Database

The app creates and uses this local SQLite database file:

```text
school_management.db
```

This file is ignored by Git because it is local runtime data.

## Project Files

- `src/main/java/schoolmanagement/SchoolManagementSystem.java` - main Swing user interface
- `src/main/java/schoolmanagement/service/` - application rules, email delivery, and CSV export helpers
- `src/main/java/schoolmanagement/dao/` - database access objects for users, tasks, and feedback
- `src/main/java/schoolmanagement/db/DBConnector.java` - SQLite database connection and automatic setup
- `src/main/java/schoolmanagement/security/PasswordUtils.java` - PBKDF2 password hashing and verification
- `src/main/java/schoolmanagement/model/` - app models used by the UI
- `docs/ARCHITECTURE.md` - explanation of the code organization
- `database_schema.sql` - database schema reference
- `pom.xml` - Maven build file and dependencies
- `run-packaged-app.ps1` - builds and launches the packaged desktop app
- `.env.example` - optional local configuration
- `Design..pdf`, `Development..pdf`, `Evaluation..pdf` - original project documentation

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

## Security Notes

- No real passwords are stored in source code.
- New user passwords are stored as PBKDF2 hashes.
- The first-run admin password must be changed before using the app.
- `.env`, `.db`, and build output files are ignored by Git.

## Future Improvements

- Package the app as a Windows installer
- Add teacher task comments and due-date reminders
- Add chart summaries for syllabus coverage
- Split the Swing UI into smaller view classes if the interface keeps growing
