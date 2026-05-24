package schoolmanagement.model;

public class TaskItem {
    public final Task task;

    public TaskItem(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return task.subject + " - " + task.className + " [" + task.status + "]";
    }
}
