package schoolmanagement.model;

public class DashboardStats {
    public final int users;
    public final int teachers;
    public final int students;
    public final int tasks;
    public final int openTasks;
    public final int completedTasks;
    public final int teacherFeedback;
    public final int studentFeedback;

    public DashboardStats(int users, int teachers, int students, int tasks, int openTasks, int completedTasks, int teacherFeedback, int studentFeedback) {
        this.users = users;
        this.teachers = teachers;
        this.students = students;
        this.tasks = tasks;
        this.openTasks = openTasks;
        this.completedTasks = completedTasks;
        this.teacherFeedback = teacherFeedback;
        this.studentFeedback = studentFeedback;
    }
}
