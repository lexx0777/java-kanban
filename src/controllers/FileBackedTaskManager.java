package controllers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(String path) {
        this.file = new File(path);
    }

    public FileBackedTaskManager(File manager) {
        this.file = manager;
    }

    public FileBackedTaskManager() {
        this.file = new File("resources/manager.csv");
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

    private void fromString(String line) {
        String[] splitLine = line.split(","); //file format: id,type,name,status,description,epic
        int id = Integer.parseInt(splitLine[0]);
        TaskType type = TaskType.valueOf(splitLine[1]);
        String title = splitLine[2];
        TaskStatus status = TaskStatus.valueOf(splitLine[3]);
        String description = splitLine[4];
        switch (type) {
            case TaskType.TASK: {
                update(new Task(id, title, description, status));
                break;
            }
            case TaskType.EPIC: {
                update(new Epic(id, title, description));
                break;
            }
            case TaskType.SUBTASK: {
                int epicId = Integer.parseInt(splitLine[5]);
                update(new Subtask(id, title, description, status, epicId));
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
                taskManager.fromString(line);
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка загрузки из файла", file);
        }
        return taskManager;
    }

    private void save() {
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
            throw new ManagerSaveException("Ошибка сохранения в файл", file);
        }
    }

    public static void main(String[] args) {
        System.out.println("Считываем данные:");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("resources/manager.csv"));
        System.out.println("Список задач:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("Список подзадач:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }
}
