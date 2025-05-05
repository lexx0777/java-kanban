package http;

import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;
import model.*;
import controllers.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final JsonTaskBuilder jsonTaskBuilder = new JsonTaskBuilder();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.taskManager = taskManager;
    }

    public JsonTaskBuilder getJsonTaskBuilder() {
        return jsonTaskBuilder;
    }

    public void start() {
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
        server.start();
    }

    public void stop() {
        server.stop(2);
        System.out.println("Сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        manager.add(new Task(0, "Задача 1", "Купить апельсин", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().minusMinutes(500)));
        manager.add(new Task(0, "Задача 2", "Купить банан", TaskStatus.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2025, 3, 2, 10, 10)));
        Epic epic = new Epic(0, "Эпик 1", "Обед");
        manager.add(epic);
        manager.add(new Subtask(0, "Подзадача 1", "Почистить", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(20), LocalDateTime.now()));
        manager.add(new Subtask(0, "Подзадача 2", "Сварить", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(21)));
        manager.add(new Subtask(0, "Подзадача 3", "Съесть", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(60)));
        manager.add(new Epic(0, "Эпик 1", "Ужин"));
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();

        //GET http://localhost:8080/tasks
        //GET http://localhost:8080/tasks/2
        //GET http://localhost:8080/epics
        //GET http://localhost:8080/subtasks
        //DELETE http://localhost:8080/tasks/1
        //GET http://localhost:8080/prioritized

    }
}