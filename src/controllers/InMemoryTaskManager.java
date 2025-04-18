package controllers;

import exceptions.TaskSaveDateTimeException;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager  implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder()));
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

    @Override
    public void add(Task task) {
        if (isValidateDateTime(task)) {
            //System.out.println("Задача пересекается по времени с другими");
            throw new TaskSaveDateTimeException(task);
        } else {
            task.setId(nextId++);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void add(Epic epic) {
        if (isValidateDateTime(epic)) {
            //System.out.println("Подзадача пересекается по времени с другими");
            throw new TaskSaveDateTimeException(epic);
        } else {
            epic.setId(nextId++);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public boolean add(Subtask subtask) {
        if (isValidateDateTime(subtask)) {
            //System.out.println("Подзадача пересекается по времени с другими");
            throw new TaskSaveDateTimeException(subtask);
        } else {
            subtask.setId(nextId++);

            subtasks.put(subtask.getId(), subtask);
            Epic epicA = epics.get(subtask.getEpicId());
            if (epicA == null)
                return false;
            ArrayList<Integer> subtasksIds = epicA.getSubtasksIds();

            if (!subtasksIds.contains(subtask.getId())) {
                subtasksIds.add(subtask.getId());
            }
            updEpicStatus(epics.get(subtask.getEpicId()));
            prioritizedTasks.add(subtask);
        }
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
        updEpicTime(epic);
    }

    @Override
    public void updEpicTime(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration totalDuration = Duration.ZERO;
        for (Integer subtasksId : epic.getSubtasksIds()) {
            Subtask subtaskA = subtasks.get(subtasksId);
            if (subtaskA.getStartTime() != null) {
                if (startTime == null) {
                    startTime = subtaskA.getStartTime();
                } else if (subtaskA.getStartTime().isBefore(startTime)) {
                    startTime = subtaskA.getStartTime();
                }
                if (endTime == null) {
                    endTime = subtaskA.getEndTime();
                } else if (subtaskA.getEndTime().isAfter(endTime)) {
                    endTime = subtaskA.getEndTime();
                }
                totalDuration = totalDuration.plus(subtaskA.getDuration());
            }
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(totalDuration);
    }

    @Override
    public void update(Task task) {
        prioritizedTasks.remove(task);
        if (isValidateDateTime(task)) {
            //System.out.println("Задача пересекается по времени с другими");
            throw new TaskSaveDateTimeException(task);
        } else {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
        updEpicStatus(epics.get(epic.getId()));
    }

    @Override
    public void update(Subtask subtask) {
        prioritizedTasks.remove(subtask);
        if (isValidateDateTime(subtask)) {
            //System.out.println("Задача пересекается по времени с другими");
            throw new TaskSaveDateTimeException(subtask);
        } else {
            subtasks.put(subtask.getId(), subtask);
            Epic epicA = epics.get(subtask.getEpicId());
            ArrayList<Integer> subtasksIds = epicA.getSubtasksIds();
            if (!subtasksIds.contains(subtask.getId())) {
                subtasksIds.add(subtask.getId());
            }
            updEpicStatus(epicA);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtasksIds();
            updEpicStatus(epic);
        }
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.keySet().forEach(historyManager::remove);
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
        prioritizedTasks.remove(getTaskById(id));
        historyManager.remove(id);
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
        prioritizedTasks.remove(getEpicById(id));
        epics.remove(id);
        return true;
    }

    @Override
    public void removeSubtaskById(int id) {
        Epic epicA  = epics.get(subtasks.get(id).getEpicId());
        epicA.removeSubtaskId(id);
        prioritizedTasks.remove(getSubtaskById(id));
        historyManager.remove(id);
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
        return epic.getSubtasksIds().stream().map(subtasks::get).collect(Collectors.toCollection(ArrayList::new));
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().filter(task -> Objects.nonNull(task.getStartTime()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
    }

    private boolean isValidateDateTime(Task task) {
        return prioritizedTasks.stream()
                .anyMatch(t -> t.getStartTime().isBefore(task.getEndTime())
                        && task.getStartTime().isBefore(t.getEndTime()));
    }

}
