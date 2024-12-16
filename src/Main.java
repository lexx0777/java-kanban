import model.*;
import controllers.*;

public class Main {
    static Managers managers;
    static TaskManager taskManager;
    static Task task;
    static Subtask subtask;
    static Epic epic;


    public static void main(String[] args) {
        taskManager = Managers.getDefault();

        test1_tasks();
        test2_epics_subtasks();
        test3_upd_del();
        test4_remove();
    }

    static void test1_tasks() {
        taskManager.add(new Task(0, "Задача1", "Описание задачи1", TaskStatus.NEW));
        taskManager.add(new Task(0, "Задача2", "Описание задачи2", TaskStatus.NEW));
        taskManager.add(new Task(0, "Задача3", "Описание задачи3", TaskStatus.NEW));
        System.out.println(taskManager.getTasks());

        task  = taskManager.getTaskById(3);
        System.out.println("task 3 " + task.toString());
    }
    static void test2_epics_subtasks() {
        taskManager.add(new Epic(0, "Epic1", "Описание Epic1"));
        taskManager.add(new Epic(0, "Epic2", "Описание Epic2"));
        taskManager.add(new Epic(0, "Epic3", "Описание Epic3"));
        System.out.println(taskManager.getEpics().toString());

        epic  = taskManager.getEpicById(6);
        System.out.println("getEpicById " + epic.toString());

        subtask = new Subtask(0, "subtask3_1", "Описание subtask3_1", TaskStatus.DONE, epic.getId());
        taskManager.add(subtask);

        taskManager.add(new Subtask(0, "subtask3_2", "Описание subtask3_2", TaskStatus.IN_PROGRESS, epic.getId()));
        System.out.println(taskManager.getSubtasks().toString());

        subtask  = taskManager.getSubtaskById(8);
        System.out.println("subtask " + subtask.toString());

        System.out.println("epics " + taskManager.getEpics().toString());

        taskManager.removeSubtaskById(subtask.getId());
        System.out.println("epics " + taskManager.getEpics().toString());
    }
    static void test3_upd_del() {
        task = taskManager.getTaskById(3);
        //task.setId(2);
        task.setDescription("new descriprion");
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.update(task);

        System.out.println("tasks " + taskManager.getTasks().toString());

        System.out.println("DEL EPIC 6 ");
        epic  = taskManager.getEpicById(6);
        taskManager.removeEpicById(epic.getId());

        System.out.println("epics " + taskManager.getEpics().toString());
        System.out.println(taskManager.getSubtasks().toString());
    }
    static void test4_remove() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        System.out.println(taskManager.getTasks().toString());
        System.out.println(taskManager.getEpics().toString());
    }
}
