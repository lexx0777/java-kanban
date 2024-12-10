package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description, TaskStatus.NEW);
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
    public String toString() {
        return "Epic #" + id + "\t[" + status + "]\t" + title + " (" + description + ")" +
                " - " + this.subtasksIds;
    }
}