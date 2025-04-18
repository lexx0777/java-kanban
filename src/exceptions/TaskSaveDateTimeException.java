package exceptions;

import model.Task;

public class TaskSaveDateTimeException extends RuntimeException {
    Task    task;

    public TaskSaveDateTimeException(Task task) {
        this.task = task;
    }

    public String getDetailMessage() {
        return String.format("%s %d %s %s пересекается по времени с другими", task.getType(), task.getId(), task.getTitle(), task.getDescription());
    }
}
