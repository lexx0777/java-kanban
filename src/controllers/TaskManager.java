package controllers;

import model.*;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    public void add(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    // Как вставляют эпик
    // Создали epic без подзадач
    // .add(epic) // <- выставился ему id
    // Создали подзадачу с subtask.epicId = epic.id
    // .add(subtask)
    public void add(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    public boolean add(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epicA = epics.get(subtask.getEpicId());
        if (epicA == null)
            return  false;
        ArrayList<Integer> subtasksIds = epicA.getSubtasksIds();

        if (!subtasksIds.contains(subtask.getId())) {
            subtasksIds.add(subtask.getId());
        }
        updEpicStatus(epics.get(subtask.getEpicId()));
        return true;
    }

    public void updEpicStatus (Epic epic) {
        TaskStatus epicStatus = TaskStatus.DONE; //начнем с максимума

        for (Integer subtasksId : epic.getSubtasksIds()) {
            Subtask subtaskA = subtasks.get(subtasksId);
            switch (subtaskA.getStatus()) {
                case NEW:
                    if (epicStatus != TaskStatus.NEW) {
                        epicStatus = TaskStatus.NEW;
                    }
                    break;
                case IN_PROGRESS:
                    if (epicStatus != TaskStatus.NEW
                            && epicStatus != TaskStatus.IN_PROGRESS) {
                        epicStatus = TaskStatus.IN_PROGRESS;
                    }
                    break;
                case DONE:
                    //ничего не делаем
                    break;
            }
        }

        epic.setStatus(epicStatus);
        //update(epic);
    }

    public void update(Task task) {
        tasks.put(task.getId(), task);
    }


    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
        updEpicStatus(epics.get(epic.getId()));
    }

    public void update(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void removeAllTasks() {
        tasks.clear();
    }
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtasksIds();
            updEpicStatus(epic);
        }
        subtasks.clear();
    }


    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public boolean removeEpicById(int id) {
        if (!epics.containsKey(id))
            return false;
        Epic epic = epics.get(id);
        for (int subtaskId: epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
        return true;
    }
    public void removeSubtaskById(int id) {
        Epic epicA  = epics.get(subtasks.get(id).getEpicId());
        epicA.removeSubtaskId(id);
        subtasks.remove(id);
        this.updEpicStatus(epicA);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtasks(int epicId) {
        if (subtasks.isEmpty()) return new ArrayList<>();
        Epic   epic = epics.get(epicId);
        if (epic.getSubtasksIds().isEmpty()) return new ArrayList<>();

        ArrayList<Subtask> ret = new ArrayList<>();

        for (Integer subtasksId : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtasksId);
            ret.add(subtask);
        }
        return ret;
    }
}
