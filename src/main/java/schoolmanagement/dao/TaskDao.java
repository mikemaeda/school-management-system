package schoolmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import schoolmanagement.db.DBConnector;
import schoolmanagement.model.Task;

public class TaskDao {
    public void createTask(String subject, String className, String day, String teacherIdNo, String details, String deadline) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO tasks (subject, class_name, day, teacher_id_no, details, deadline)
                 VALUES (?, ?, ?, ?, ?, ?)
             """)) {
            statement.setString(1, subject);
            statement.setString(2, className);
            statement.setString(3, day);
            statement.setString(4, teacherIdNo);
            statement.setString(5, details);
            statement.setString(6, deadline);
            statement.executeUpdate();
        }
    }

    public List<Task> findAll() throws SQLException {
        return findByTeacherIdNo(null);
    }

    public List<Task> findByTeacherIdNo(String teacherIdNo) throws SQLException {
        String sql = """
            SELECT t.*, u.first_name || ' ' || u.last_name AS teacher_name
            FROM tasks t
            LEFT JOIN users u ON t.teacher_id_no = u.id_no
        """;
        if (teacherIdNo != null) {
            sql += " WHERE t.teacher_id_no = ?";
        }
        sql += " ORDER BY t.created_at DESC";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (teacherIdNo != null) {
                statement.setString(1, teacherIdNo);
            }

            ResultSet result = statement.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (result.next()) {
                tasks.add(readTask(result));
            }
            return tasks;
        }
    }

    public void updateStatus(int taskId, String status) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE tasks SET status = ? WHERE id = ?")) {
            statement.setString(1, status);
            statement.setInt(2, taskId);
            statement.executeUpdate();
        }
    }

    public int countAll() throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tasks")) {
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
        }
    }

    public int countByStatus(String status) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tasks WHERE status = ?")) {
            statement.setString(1, status);
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
        }
    }

    public int countOpen() throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tasks WHERE status <> 'Completed'")) {
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
        }
    }

    private Task readTask(ResultSet result) throws SQLException {
        return new Task(
            result.getInt("id"),
            result.getString("subject"),
            result.getString("class_name"),
            result.getString("day"),
            result.getString("teacher_id_no"),
            result.getString("teacher_name"),
            result.getString("details"),
            result.getString("deadline"),
            result.getString("status")
        );
    }
}
