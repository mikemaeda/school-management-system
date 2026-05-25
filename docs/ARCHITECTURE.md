# Architecture

The app is organized as a Java Swing desktop application. It keeps the original sign-in, registration, and role-based dashboard design instead of converting the UI into a separate web app.

## Packages

- `schoolmanagement` - application entry points and Swing UI.
- `schoolmanagement.dao` - database access classes. SQL lives here instead of inside the Swing UI.
- `schoolmanagement.db` - SQLite connection and database setup.
- `schoolmanagement.model` - simple data objects used by the UI.
- `schoolmanagement.security` - password hashing and verification.
- `schoolmanagement.service` - application rules, email delivery, report export, and workflow methods.

## Runtime Flow

1. `SchoolManagementSystem` starts the Swing window.
2. `DBConnector` opens `school_management.db` and creates tables if needed.
3. `DBConnector` enables SQLite foreign keys and a busy timeout to reduce locking issues.
4. A default Head of School account is created on the first run.
5. The first-run Head of School must replace the default password.
6. Users sign in and see a dashboard based on their role.
7. `SchoolService` coordinates users, tasks, feedback, email, and CSV export.
8. DAO classes store and read data from SQLite.

## Why Desktop

This project is designed as a shareable desktop school-management system. The Java Swing UI is part of the original project identity and includes the login, registration, and role-specific workflows.

For public sharing, the recommended path is to keep the project downloadable and provide clear GitHub instructions for running it locally.

## Why SQLite

SQLite keeps this project beginner-friendly and portable. There is no database server to install, and the app can run from the project folder with a single local `.db` file.

The connector also performs small migrations, such as adding the `password_changed` column to older local databases.

## Default Account

```text
Email: admin@school.local
Password: Admin123!
```

The app prompts for a new admin password after the first login.

## Current Boundaries

- UI code stays in `SchoolManagementSystem`.
- SQL is kept in DAO classes.
- Business rules stay in `SchoolService`.
- Password hashing stays in `PasswordUtils`.
- Email sending stays in `EmailService`.
- CSV writing stays in `CsvExporter`.

## If This Becomes A Web App Later

A true web version should preserve the original product flow:

- real login and registration
- role-based dashboards
- Head of School task assignment
- Teacher task updates and progress reports
- Student reflections and feedback

It should not be a generic demo dashboard. A production web version would need authentication, a hosted database, and a UI designed from the original desktop workflows.

## Good Next Refactors

- Package the desktop app as a Windows installer.
- Split the Swing UI into smaller view classes once the screens grow again.
- Add chart summaries for syllabus coverage and task completion.
- Add JUnit tests around `PasswordUtils`, `SchoolService`, and DAO classes.
