package schoolmanagement.service;

import java.sql.SQLException;
import java.util.List;
import schoolmanagement.dao.FeedbackDao;
import schoolmanagement.dao.TaskDao;
import schoolmanagement.dao.UserDao;
import schoolmanagement.db.DBConnector;
import schoolmanagement.model.DashboardStats;
import schoolmanagement.model.Task;
import schoolmanagement.model.User;
import schoolmanagement.security.PasswordUtils;

public class SchoolService {
    public static final String ROLE_HEAD = "Head of School";
    public static final String ROLE_TEACHER = "Teacher";
    public static final String ROLE_STUDENT = "Student";

    private final UserDao userDao = new UserDao();
    private final TaskDao taskDao = new TaskDao();
    private final FeedbackDao feedbackDao = new FeedbackDao();

    public User authenticate(String email, String password) throws SQLException {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return null;
        }

        String passwordHash = userDao.findPasswordHashByEmail(email);
        return PasswordUtils.verifyPassword(password, passwordHash) ? user : null;
    }

    public void createUser(String firstName, String lastName, String idNo, String email, String password, String role) throws SQLException {
        validateRequired("First name", firstName);
        validateRequired("Last name", lastName);
        validateRequired("School ID", idNo);
        validateEmail(email);
        validatePassword(password);
        validateRole(role);
        userDao.createUser(firstName.trim(), lastName.trim(), idNo.trim(), email.trim(), PasswordUtils.hashPassword(password), role);
    }

    public User changePassword(User user, String newPassword) throws SQLException {
        validatePassword(newPassword);
        if (DBConnector.DEFAULT_ADMIN_EMAIL.equalsIgnoreCase(user.email) && DBConnector.DEFAULT_ADMIN_PASSWORD.equals(newPassword)) {
            throw new IllegalArgumentException("Choose a password that is different from the first-run admin password.");
        }

        userDao.updatePasswordHash(user.id, PasswordUtils.hashPassword(newPassword));
        User updated = userDao.findById(user.id);
        if (updated == null) {
            throw new SQLException("Could not reload updated user.");
        }
        return updated;
    }

    public boolean needsFirstRunPasswordChange(User user) {
        return user != null && DBConnector.DEFAULT_ADMIN_EMAIL.equalsIgnoreCase(user.email) && !user.passwordChanged;
    }

    public void assignTask(String subject, String className, String day, String teacherIdNo, String details, String deadline) throws SQLException {
        validateRequired("Subject", subject);
        validateRequired("Class", className);
        validateRequired("Task details", details);
        validateRequired("Teacher", teacherIdNo);
        taskDao.createTask(subject.trim(), className.trim(), day, teacherIdNo, details.trim(), deadline == null ? "" : deadline.trim());
    }

    public void submitTeacherProgress(User teacher, int taskId, String coverage, String notes, int preparedness, int delivery, int enjoyment) throws SQLException {
        requireRole(teacher, ROLE_TEACHER);
        feedbackDao.createTeacherFeedback(teacher.idNo, taskId, coverage, notes == null ? "" : notes.trim(), preparedness, delivery, enjoyment);
        taskDao.updateStatus(taskId, "In Progress");
    }

    public void submitStudentReflection(User student, String teacherIdNo, String subject, int clarity, int engagement, int comfort, int pacing, int rating, String comments) throws SQLException {
        requireRole(student, ROLE_STUDENT);
        validateRequired("Subject", subject);
        feedbackDao.createStudentFeedback(student.idNo, teacherIdNo, subject.trim(), clarity, engagement, comfort, pacing, rating, comments == null ? "" : comments.trim());
    }

    public void updateTaskStatus(int taskId, String status) throws SQLException {
        taskDao.updateStatus(taskId, status);
    }

    public List<User> getUsers() throws SQLException {
        return userDao.findAll();
    }

    public List<User> getUsersByRole(String role) throws SQLException {
        validateRole(role);
        return userDao.findByRole(role);
    }

    public List<Task> getTasks(String teacherIdNo) throws SQLException {
        return teacherIdNo == null ? taskDao.findAll() : taskDao.findByTeacherIdNo(teacherIdNo);
    }

    public List<Object[]> getTeacherFeedbackRows() throws SQLException {
        return feedbackDao.findTeacherFeedbackRows();
    }

    public List<Object[]> getStudentFeedbackRows() throws SQLException {
        return feedbackDao.findStudentFeedbackRows();
    }

    public DashboardStats getDashboardStats() throws SQLException {
        return new DashboardStats(
            userDao.countAll(),
            userDao.countByRole(ROLE_TEACHER),
            userDao.countByRole(ROLE_STUDENT),
            taskDao.countAll(),
            taskDao.countOpen(),
            taskDao.countByStatus("Completed"),
            feedbackDao.countTeacherFeedback(),
            feedbackDao.countStudentFeedback()
        );
    }

    private void validateRequired(String label, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private void validateEmail(String email) {
        validateRequired("Email", email);
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
    }

    private void validatePassword(String password) {
        validateRequired("Password", password);
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new IllegalArgumentException("Password must include at least one letter and one number.");
        }
    }

    private void validateRole(String role) {
        if (!List.of(ROLE_HEAD, ROLE_TEACHER, ROLE_STUDENT).contains(role)) {
            throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }

    private void requireRole(User user, String expectedRole) {
        if (user == null || !expectedRole.equals(user.role)) {
            throw new IllegalArgumentException("This action requires a " + expectedRole + " account.");
        }
    }
}
