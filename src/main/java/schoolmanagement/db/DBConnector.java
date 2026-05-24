package schoolmanagement.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import schoolmanagement.security.PasswordUtils;

public class DBConnector {
    public static final String DEFAULT_ADMIN_EMAIL = "admin@school.local";
    public static final String DEFAULT_ADMIN_PASSWORD = "Admin123!";

    private static final String DB_DRIVER = getConfig("DB_DRIVER", "org.sqlite.JDBC");
    private static final String DB_URL = getConfig("DB_URL", "jdbc:sqlite:school_management.db");
    private static boolean initialized = false;

    private static String getConfig(String name, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            value = System.getenv(name);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + DB_DRIVER, e);
        }

        Connection connection = DriverManager.getConnection(DB_URL);
        configureConnection(connection);
        initializeDatabase(connection);
        return connection;
    }

    private static void configureConnection(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("PRAGMA foreign_keys = ON");
            statement.executeUpdate("PRAGMA busy_timeout = 5000");
        }
    }

    private static synchronized void initializeDatabase(Connection connection) throws SQLException {
        if (initialized) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    id_no TEXT NOT NULL UNIQUE,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    password_changed INTEGER NOT NULL DEFAULT 1,
                    role TEXT NOT NULL CHECK (role IN ('Head of School', 'Teacher', 'Student')),
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject TEXT NOT NULL,
                    class_name TEXT NOT NULL,
                    day TEXT NOT NULL,
                    teacher_id_no TEXT NOT NULL,
                    details TEXT NOT NULL,
                    deadline TEXT,
                    status TEXT NOT NULL DEFAULT 'Assigned',
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (teacher_id_no) REFERENCES users(id_no)
                )
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS teacher_feedback (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    teacher_id_no TEXT NOT NULL,
                    task_id INTEGER,
                    coverage TEXT NOT NULL,
                    notes TEXT,
                    preparedness INTEGER NOT NULL,
                    delivery INTEGER NOT NULL,
                    enjoyment INTEGER NOT NULL,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (teacher_id_no) REFERENCES users(id_no),
                    FOREIGN KEY (task_id) REFERENCES tasks(id)
                )
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS student_feedback (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id_no TEXT NOT NULL,
                    teacher_id_no TEXT,
                    subject TEXT NOT NULL,
                    clarity INTEGER NOT NULL,
                    engagement INTEGER NOT NULL,
                    comfort INTEGER NOT NULL,
                    pacing INTEGER NOT NULL,
                    rating INTEGER NOT NULL,
                    comments TEXT,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (student_id_no) REFERENCES users(id_no)
                )
            """);
        }

        ensureColumn(connection, "users", "password_changed", "INTEGER NOT NULL DEFAULT 1");
        seedDefaultHeadOfSchool(connection);
        markDefaultAdminIfStillUsingDefaultPassword(connection);
        initialized = true;
    }

    private static void ensureColumn(Connection connection, String tableName, String columnName, String definition) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (result.next()) {
                if (columnName.equalsIgnoreCase(result.getString("name"))) {
                    return;
                }
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
        }
    }

    private static void seedDefaultHeadOfSchool(Connection connection) throws SQLException {
        try (var check = connection.prepareStatement("""
            SELECT COUNT(*)
            FROM users
            WHERE role = 'Head of School' OR email = ?
        """)) {
            check.setString(1, DEFAULT_ADMIN_EMAIL);
            var result = check.executeQuery();
            if (result.next() && result.getInt(1) > 0) {
                return;
            }
        }

        try (var insert = connection.prepareStatement("""
            INSERT INTO users (first_name, last_name, id_no, email, password_hash, password_changed, role)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """)) {
            insert.setString(1, "Default");
            insert.setString(2, "Admin");
            insert.setString(3, "HOS-001");
            insert.setString(4, DEFAULT_ADMIN_EMAIL);
            insert.setString(5, PasswordUtils.hashPassword(DEFAULT_ADMIN_PASSWORD));
            insert.setInt(6, 0);
            insert.setString(7, "Head of School");
            insert.executeUpdate();
        }
    }

    private static void markDefaultAdminIfStillUsingDefaultPassword(Connection connection) throws SQLException {
        try (var check = connection.prepareStatement("""
            SELECT id, password_hash, password_changed
            FROM users
            WHERE email = ?
        """)) {
            check.setString(1, DEFAULT_ADMIN_EMAIL);
            ResultSet result = check.executeQuery();
            if (!result.next()) {
                return;
            }

            boolean usingDefaultPassword = PasswordUtils.verifyPassword(DEFAULT_ADMIN_PASSWORD, result.getString("password_hash"));
            boolean alreadyChanged = result.getInt("password_changed") == 1;
            if (!usingDefaultPassword || !alreadyChanged) {
                return;
            }

            try (var update = connection.prepareStatement("UPDATE users SET password_changed = 0 WHERE id = ?")) {
                update.setInt(1, result.getInt("id"));
                update.executeUpdate();
            }
        }
    }
}
