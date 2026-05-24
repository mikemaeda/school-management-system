package schoolmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import schoolmanagement.db.DBConnector;

public class FeedbackDao {
    public void createTeacherFeedback(String teacherIdNo, int taskId, String coverage, String notes, int preparedness, int delivery, int enjoyment) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO teacher_feedback
                    (teacher_id_no, task_id, coverage, notes, preparedness, delivery, enjoyment)
                 VALUES (?, ?, ?, ?, ?, ?, ?)
             """)) {
            statement.setString(1, teacherIdNo);
            statement.setInt(2, taskId);
            statement.setString(3, coverage);
            statement.setString(4, notes);
            statement.setInt(5, preparedness);
            statement.setInt(6, delivery);
            statement.setInt(7, enjoyment);
            statement.executeUpdate();
        }
    }

    public void createStudentFeedback(String studentIdNo, String teacherIdNo, String subject, int clarity, int engagement, int comfort, int pacing, int rating, String comments) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO student_feedback
                    (student_id_no, teacher_id_no, subject, clarity, engagement, comfort, pacing, rating, comments)
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
             """)) {
            statement.setString(1, studentIdNo);
            statement.setString(2, teacherIdNo);
            statement.setString(3, subject);
            statement.setInt(4, clarity);
            statement.setInt(5, engagement);
            statement.setInt(6, comfort);
            statement.setInt(7, pacing);
            statement.setInt(8, rating);
            statement.setString(9, comments);
            statement.executeUpdate();
        }
    }

    public List<Object[]> findTeacherFeedbackRows() throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT u.first_name || ' ' || u.last_name AS teacher_name,
                        COALESCE(t.subject || ' - ' || t.class_name, 'General') AS task_name,
                        f.coverage, f.preparedness, f.delivery, f.enjoyment, f.notes, f.created_at
                 FROM teacher_feedback f
                 JOIN users u ON f.teacher_id_no = u.id_no
                 LEFT JOIN tasks t ON f.task_id = t.id
                 ORDER BY f.created_at DESC
             """)) {
            ResultSet result = statement.executeQuery();
            List<Object[]> rows = new ArrayList<>();
            while (result.next()) {
                rows.add(new Object[] {
                    result.getString("teacher_name"),
                    result.getString("task_name"),
                    result.getString("coverage"),
                    result.getInt("preparedness"),
                    result.getInt("delivery"),
                    result.getInt("enjoyment"),
                    result.getString("notes"),
                    result.getString("created_at")
                });
            }
            return rows;
        }
    }

    public List<Object[]> findStudentFeedbackRows() throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT s.first_name || ' ' || s.last_name AS student_name,
                        COALESCE(t.first_name || ' ' || t.last_name, 'Unassigned') AS teacher_name,
                        f.subject, f.rating, f.clarity, f.engagement, f.comfort, f.pacing, f.comments, f.created_at
                 FROM student_feedback f
                 JOIN users s ON f.student_id_no = s.id_no
                 LEFT JOIN users t ON f.teacher_id_no = t.id_no
                 ORDER BY f.created_at DESC
             """)) {
            ResultSet result = statement.executeQuery();
            List<Object[]> rows = new ArrayList<>();
            while (result.next()) {
                rows.add(new Object[] {
                    result.getString("student_name"),
                    result.getString("teacher_name"),
                    result.getString("subject"),
                    result.getInt("rating"),
                    result.getInt("clarity"),
                    result.getInt("engagement"),
                    result.getInt("comfort"),
                    result.getInt("pacing"),
                    result.getString("comments"),
                    result.getString("created_at")
                });
            }
            return rows;
        }
    }

    public int countTeacherFeedback() throws SQLException {
        return count("teacher_feedback");
    }

    public int countStudentFeedback() throws SQLException {
        return count("student_feedback");
    }

    private int count(String table) throws SQLException {
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + table)) {
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
        }
    }
}
