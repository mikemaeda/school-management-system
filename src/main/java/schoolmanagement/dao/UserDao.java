package schoolmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import schoolmanagement.db.DBConnector;
import schoolmanagement.model.User;

public class UserDao {
    public User findByEmail(String email) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            return result.next() ? readUser(result) : null;
        }
    }

    public User findById(int id) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            return result.next() ? readUser(result) : null;
        }
    }

    public void createUser(String firstName, String lastName, String idNo, String email, String passwordHash, String role) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO users (first_name, last_name, id_no, email, password_hash, password_changed, role)
                 VALUES (?, ?, ?, ?, ?, ?, ?)
             """)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, idNo);
            statement.setString(4, email);
            statement.setString(5, passwordHash);
            statement.setInt(6, 1);
            statement.setString(7, role);
            statement.executeUpdate();
        }
    }

    public List<User> findAll() throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users ORDER BY role, last_name, first_name")) {
            ResultSet result = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (result.next()) {
                users.add(readUser(result));
            }
            return users;
        }
    }

    public List<User> findByRole(String role) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE role = ? ORDER BY last_name, first_name")) {
            statement.setString(1, role);
            ResultSet result = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (result.next()) {
                users.add(readUser(result));
            }
            return users;
        }
    }

    public void updatePasswordHash(int userId, String passwordHash) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET password_hash = ?, password_changed = 1 WHERE id = ?")) {
            statement.setString(1, passwordHash);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    public String findPasswordHashByEmail(String email) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT password_hash FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getString("password_hash") : "";
        }
    }

    public int countAll() throws SQLException {
        return count("1 = 1");
    }

    public int countByRole(String role) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE role = ?")) {
            statement.setString(1, role);
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
        }
    }

    private int count(String whereClause) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE " + whereClause)) {
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
        }
    }

    private User readUser(ResultSet result) throws SQLException {
        return new User(
            result.getInt("id"),
            result.getString("first_name"),
            result.getString("last_name"),
            result.getString("id_no"),
            result.getString("email"),
            result.getString("role"),
            result.getInt("password_changed") == 1
        );
    }
}
