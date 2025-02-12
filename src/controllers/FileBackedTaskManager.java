package controllers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(String path) {
        this.file = new File(path);
    }

    public FileBackedTaskManager(File manager) {
        this.file = manager;
    }

    public FileBackedTaskManager() {
        this.file = new File("manager.csv");
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    @Override
    public boolean add(Subtask subtask) {
        boolean ret  = super.add(subtask);
        save();
        return ret;
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public boolean removeEpicById(int id) {
        boolean ret  = super.removeEpicById(id);
        save();
        return ret;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(Subtask subtask) {
        super.update(subtask);
        save();
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    public File getFile() {
        return this.file;
    }

    private static void fromString(String line, FileBackedTaskManager taskManager) {
        String[] splitLine = line.split(","); //file format: id,type,name,status,description,epic
        int id = Integer.parseInt(splitLine[0]);
        TaskType type = TaskType.valueOf(splitLine[1]);
        String title = splitLine[2];
        TaskStatus status = TaskStatus.valueOf(splitLine[3]);
        String description = splitLine[4];
        switch (type) {
            case TaskType.TASK: {
                taskManager.update(new Task(id, title, description, status));
                break;
            }
            case TaskType.EPIC: {
                taskManager.update(new Epic(id, title, description));
                break;
            }
            case TaskType.SUBTASK: {
                int epicId = Integer.parseInt(splitLine[5]);
                taskManager.update(new Subtask(id, title, description, status, epicId));
                break;
            }
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();//head
            while (reader.ready()) {
                line = reader.readLine();
                fromString(line, taskManager);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка во время загрузки из файла");
        }
        return taskManager;
    }

    private void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(file)) {
            String head = "id,type,name,status,description,epic\n";
            writer.write(head);
            for (Task task : getTasks()) {
                writer.write(String.format("%s\n", task.toString()));
            }
            for (Epic epic : getEpics()) {
                writer.write(String.format("%s\n", epic.toString()));
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(String.format("%s\n", subtask.toString()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла - " + file);
        }
    }

    public static void main(String[] args) {
        /*
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        Task task1 = new Task(1, "убраться в комнате", "пыль и пр " , TaskStatus.NEW);
        taskManager.update(task1);
        Task task2 = new Task(2, "Сходить в кино", "купить билет и семки", TaskStatus.NEW);
        taskManager.update(task2);
        Epic epic1 = new Epic(3, "Дело", "дело дело дело");
        taskManager.update(epic1);
        Epic epic2 = new Epic(4, "Сделать тз", "успеть");
        taskManager.update(epic2);
        Epic epic3 = new Epic(5, "Epic3", "Описание Epic3");
        taskManager.update(epic3);
        Subtask subtask1 = new Subtask(7, "subtask3_1", "Описание subtask3_1", TaskStatus.IN_PROGRESS, epic3.getId());
        taskManager.update(subtask1);
        Subtask subtask2 = new Subtask(8, "subtask3_1", "Описание subtask3_1", TaskStatus.DONE, epic3.getId());
        taskManager.update(subtask2);
        Subtask subtask3 = new Subtask(9, "subtask3_1", "Описание subtask3_1", TaskStatus.IN_PROGRESS, epic3.getId());
        taskManager.update(subtask3);

        System.out.println("Список задач исходный:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков исходный:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("Список подзадач исходный:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }
        taskManager.save();
        */

        System.out.println("Считываем данные:");
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(new File("manager.csv"));
        System.out.println("Список задач:");
        for (Task task : taskManager2.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков:");
        for (Epic epic : taskManager2.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("Список подзадач:");
        for (Subtask subtask : taskManager2.getSubtasks()) {
            System.out.println(subtask);
        }
    }
}
