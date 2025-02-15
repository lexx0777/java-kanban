package controllers;

import model.*;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager  implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void add(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void add(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
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

    @Override
    public void updEpicStatus(Epic epic) {
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
    }

    @Override
    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
        updEpicStatus(epics.get(epic.getId()));
    }

    @Override
    public void update(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epicA = epics.get(subtask.getEpicId());
        ArrayList<Integer> subtasksIds = epicA.getSubtasksIds();
        if (!subtasksIds.contains(subtask.getId())) {
            subtasksIds.add(subtask.getId());
        }
        updEpicStatus(epicA);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtasksIds();
            updEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void removeSubtaskById(int id) {
        Epic epicA  = epics.get(subtasks.get(id).getEpicId());
        epicA.removeSubtaskId(id);
        subtasks.remove(id);
        this.updEpicStatus(epicA);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
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

    @Override
    public HistoryManager getHistory() {
        return historyManager;
    }

    @Override
    public int getNextId() {
        return nextId;
    }

    @Override
    public void setNextId(int newNextId) {
        nextId = newNextId;
    }
}
