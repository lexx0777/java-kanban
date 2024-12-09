import java.util.Objects;

public class Task {
    public int id;
    public String title;
    public String description;
    public TaskStatus status;

    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public boolean update(Task task) {
        if (task == null) return false;
        //if (task.getClass() != this.getClass()) return false;
        setTitle(task.getTitle());
        setDescription(task.getDescription());
        setStatus(task.getStatus());
        setId(task.getId());
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task #" + id + "\t[" + status + "]\t" + title
                + " (" + description + ")";
    }
}