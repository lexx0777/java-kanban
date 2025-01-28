package controllers;

import model.*;
import java.util.ArrayList;

public interface TaskManager {

    void add(Task task);

    void add(Epic epic);

    boolean add(Subtask subtask);

    void updEpicStatus(Epic epic);

    void update(Task task);

    void update(Epic epic);

    void update(Subtask subtask);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void removeTaskById(int id);

    boolean removeEpicById(int id);

    void removeSubtaskById(int id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getSubtasks(int epicId);

    HistoryManager getHistory();
}
