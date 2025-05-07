package model;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String title, String description, Duration duration, LocalDateTime startTime) {
        super(id, title, description, TaskStatus.NEW, duration, startTime);
    }

    public Epic(String title, String description, Duration duration, LocalDateTime startTime) {
        super(title, description, TaskStatus.NEW, duration, startTime);
    }

    public Epic(int id, String title, String description) {
        super(id, title, description, TaskStatus.NEW);
    }

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void cleanSubtasksIds() {
        this.subtasksIds.clear();
    }

    public void removeSubtaskId(int id) {
        this.subtasksIds.remove(this.subtasksIds.indexOf(id));
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return id + ","
                + TaskType.EPIC + ","
                + title + ","
                + status + ","
                + description + ","
                + duration.toMinutes() + ","
                + dateTimeOrNull(startTime) + ","
                + dateTimeOrNull(endTime) + ",";
    }

}