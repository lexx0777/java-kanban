import java.util.HashMap;
import java.util.ArrayList;
//import java.util.Objects;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int nextId = 1;

    public void add(Task task) {
        task.id = nextId++;
        tasks.put(task.id, task);
    }

    // Как вставляют эпик
    // Создали epic без подзадач
    // .add(epic) // <- выставился ему id
    // Создали подзадачу с subtask.epicId = epic.id
    // .add(subtask)
    public void add(Epic epic) {
        epic.id = nextId++;
        epics.put(epic.id, epic);
    }

    public boolean add(Subtask subtask) {
        subtask.id = nextId++;
        subtasks.put(subtask.id, subtask);
        Epic epicA = epics.get(subtask.epicId);
        if (epicA == null)
            return  false;
        ArrayList<Integer> subtasksIds = epicA.getSubtasksIds();

        if (!subtasksIds.contains(subtask.id)) {
            subtasksIds.add(subtask.id);
        }
        updEpicStatus(epics.get(subtask.epicId));
        return true;
    }

    public void updEpicStatus (Epic epic) {
        TaskStatus epicStatus = TaskStatus.DONE; //начнем с максимума

        for (Integer subtasksId : epic.subtasksIds) {
            Subtask subtaskA = subtasks.get(subtasksId);
            switch (subtaskA.status) {
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

        epic.status = epicStatus;
        //update(epic);
    }

    public void update(Task task) {
        tasks.put(task.id, task);
    }


    public void update(Epic epic) {
        epics.put(epic.id, epic);
        updEpicStatus(epics.get(epic.id));
    }

    public void update(Subtask subtask) {
        subtasks.put(subtask.id, subtask);
        updEpicStatus(epics.get(subtask.epicId));
    }

    Task findTask(Task task) {
        for (Task taskA : tasks.values()) {
             if (task.equals(taskA)) {
                return taskA;
            }
        }
        return task;
    }

    Epic findEpic(Epic epic) {
        for (Epic epicA : epics.values()) {
            if (epic.equals(epicA)) {
                return epicA;
            }
        }
        return epic;
    }


    Subtask findSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.epicId);
        for (int subtasksId : epic.subtasksIds) {
            Subtask subtaskA = subtasks.get(subtasksId);
            if (subtask.equals(subtaskA)) {
                return subtaskA;
            }
        }
        return subtask;
    }


    public void removeAllTasks() {
        tasks = new HashMap<>();
    }
    public void removeAllEpics() {
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }
    public void removeAllSubtasks() {
        subtasks = new HashMap<>();
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
        for (int subtaskId: epic.subtasksIds) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
        return true;
    }
    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }

    public ArrayList<Task> getTasks() {
        if (tasks.isEmpty()) return null;
        ArrayList<Task> ret = new ArrayList<>();
        for (Task task : tasks.values()) {
            ret.add(task);
        }
        return ret;
    }

    public ArrayList<Epic> getEpics() {
        if (epics.isEmpty()) return null;
        ArrayList<Epic> ret = new ArrayList<>();
        for (Epic epic : epics.values()) {
            ret.add(epic);
        }
        return ret;
    }

    public ArrayList<Subtask> getSubtasks() {
        if (subtasks.isEmpty()) return null;
        ArrayList<Subtask> ret = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            ret.add(subtask);
        }
        return ret;
    }

    public ArrayList<Subtask> getSubtasks(int epicId) {
        if (subtasks.isEmpty()) return null;

        Epic   epic = epics.get(epicId);
        if (epic.subtasksIds.isEmpty()) return null;

        ArrayList<Subtask> ret = new ArrayList<>();

        for (Integer subtasksId : epic.subtasksIds) {
            Subtask subtask = subtasks.get(subtasksId);
            ret.add(subtask);
        }
        return ret;
    }
}
