import java.util.ArrayList;
//import java.util.Objects;

public class Epic extends Task {
    ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    @Override
    public String toString() {
        return "Epic #" + id + "\t[" + status + "]\t" + title + " (" + description + ")" +
                " - " + this.subtasksIds;
    }
}