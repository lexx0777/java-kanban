//import java.util.Objects;

public class Subtask extends Task {
    int epicId;
    /*
    public Subtask(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        epicId = 0;
    }
    */
    public Subtask(int id, String title, String description, TaskStatus status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask #" + id + "\t[" + status + "]\t" + title + " (" + description + ") "
                + epicId;
    }
}
