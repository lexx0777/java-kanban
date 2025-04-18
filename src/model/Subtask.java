package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, TaskStatus status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return id + ","
                + TaskType.SUBTASK + ","
                + title + ","
                + status + ","
                + description + ","
                + duration.toMinutes() + ","
                + dateTimeOrNull(startTime) + ","
                + dateTimeOrNull(endTime) + ","
                + epicId + ","
                ;
    }
}
