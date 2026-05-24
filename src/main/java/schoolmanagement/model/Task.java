package schoolmanagement.model;

public class Task {
    public final int id;
    public final String subject;
    public final String className;
    public final String day;
    public final String teacherIdNo;
    public final String teacherName;
    public final String details;
    public final String deadline;
    public final String status;

    public Task(int id, String subject, String className, String day, String teacherIdNo, String teacherName, String details, String deadline, String status) {
        this.id = id;
        this.subject = subject;
        this.className = className;
        this.day = day;
        this.teacherIdNo = teacherIdNo;
        this.teacherName = teacherName;
        this.details = details;
        this.deadline = deadline;
        this.status = status;
    }
}
