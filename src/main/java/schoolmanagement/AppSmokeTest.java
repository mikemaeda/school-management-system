package schoolmanagement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import schoolmanagement.db.DBConnector;
import schoolmanagement.model.DashboardStats;
import schoolmanagement.model.Task;
import schoolmanagement.model.User;
import schoolmanagement.service.SchoolService;

public class AppSmokeTest {
    public static void main(String[] args) throws Exception {
        Path database = Files.createTempFile("school-management-smoke", ".db");
        System.setProperty("DB_URL", "jdbc:sqlite:" + database.toAbsolutePath());

        try {
            SchoolService service = new SchoolService();

            User admin = service.authenticate(DBConnector.DEFAULT_ADMIN_EMAIL, DBConnector.DEFAULT_ADMIN_PASSWORD);
            if (admin == null) {
                throw new IllegalStateException("Default admin account was not created.");
            }
            if (!service.needsFirstRunPasswordChange(admin)) {
                throw new IllegalStateException("Default admin should be required to change the first-run password.");
            }

            admin = service.changePassword(admin, "Admin2026!");
            if (service.needsFirstRunPasswordChange(admin)) {
                throw new IllegalStateException("Admin password-change flag was not updated.");
            }

            service.createUser("Grace", "Teacher", "T-001", "teacher@example.edu", "Teacher2026!", SchoolService.ROLE_TEACHER);
            service.createUser("Sam", "Student", "S-001", "student@example.edu", "Student2026!", SchoolService.ROLE_STUDENT);

            User teacher = service.authenticate("teacher@example.edu", "Teacher2026!");
            User student = service.authenticate("student@example.edu", "Student2026!");
            service.assignTask("Computer Science", "Form 4", "Monday", teacher.idNo, "Finish arrays lesson and notes.", "2026-06-01");

            List<Task> tasks = service.getTasks(teacher.idNo);
            if (tasks.isEmpty()) {
                throw new IllegalStateException("Task assignment did not save.");
            }

            service.submitTeacherProgress(teacher, tasks.get(0).id, "Started", "Students understood the first examples.", 4, 4, 5);
            service.submitStudentReflection(student, teacher.idNo, "Computer Science", 5, 4, 5, 4, 5, "Helpful lesson.");

            DashboardStats stats = service.getDashboardStats();
            if (stats.users != 3 || stats.tasks != 1 || stats.teacherFeedback != 1 || stats.studentFeedback != 1) {
                throw new IllegalStateException("Smoke test stats did not match expected values.");
            }

            System.out.println("Default account: " + DBConnector.DEFAULT_ADMIN_EMAIL + " / Head of School");
            System.out.println("Created teacher, student, task, progress report, and student reflection.");
            System.out.println("Smoke test passed.");
        } finally {
            Files.deleteIfExists(database);
        }
    }
}
