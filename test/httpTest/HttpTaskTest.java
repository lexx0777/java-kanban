package httpTest;

import http.HttpTaskServer;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.TaskStatus;
import model.Task;
import controllers.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    JsonTaskBuilder json = server.getJsonTaskBuilder();
    URI taskUrl = URI.create("http://localhost:8080/tasks");


    LocalDateTime startTime1 = LocalDateTime.of(LocalDate.of(2025, 2, 4),
            LocalTime.of(10, 0));
    LocalDateTime startTime2 = LocalDateTime.of(LocalDate.of(2025, 2, 5),
            LocalTime.of(10, 0));
    public HttpTaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        Task task = new Task(0,"testTask", "testTaskDescr", TaskStatus.NEW, Duration.ofMinutes(5), startTime1);
        Task task2 = new Task(0,"testTask2", "testTaskDescr", TaskStatus.NEW, Duration.ofMinutes(5), startTime2);

        manager.add(task);
        manager.add(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        String tasks = json.toJson(manager.getTasks());
        assertEquals(tasks,response.body());
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
        manager.add(new Task(0,"testTask", "testTaskDescr", TaskStatus.NEW, Duration.ofMinutes(5), startTime1));

        Task task = manager.getTaskById(1);
        taskUrl = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(json.toJson(task), response.body());


    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = """
                {
                        "title": "Выгулять хомяка",
                        "description": "Выгулять хомяка",
                        "duration": "30",
                        "startTime": "2025-03-21 15:45:01"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        ArrayList<Task> tasks = manager.getTasks();

        Assertions.assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Выгулять хомяка", tasks.getFirst().getTitle());
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        manager.add(new Task(0,"testTask", "testTaskDescr", TaskStatus.IN_PROGRESS, Duration.ofMinutes(5), startTime1));
        String taskJson = """
                {"id": 1,
                        "title": "Выгулять жену",
                        "description": "Показать шубу",
                        "status": "DONE",
                        "duration": "100",
                        "startTime": "2025-02-21 15:45:01"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskUpd = manager.getTaskById(1);
        assertEquals(201, response.statusCode());
        //Assertions.assertEquals("Выгулять Жену", taskUpd.getTitle());
        assertEquals("Показать шубу", taskUpd.getDescription());
        assertEquals(TaskStatus.DONE, taskUpd.getStatus());
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task(0,"testTask", "testTaskDescr", TaskStatus.NEW, Duration.ofMinutes(5), startTime1);
        manager.add(task);
        taskUrl = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        ArrayList<Task> tasks = manager.getTasks();

        Assertions.assertTrue(tasks.isEmpty());
    }

    @Test
    public void getTaskNotFound() throws IOException, InterruptedException {
        manager.add(new Task(0, "testTask", "testTaskDescr", TaskStatus.IN_PROGRESS, Duration.ofMinutes(5), startTime1));
        taskUrl = URI.create("http://localhost:8080/tasks/3");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с указанным id не найдена", response.body());

    }

    @Test
    public void addTaskIntersection() throws IOException, InterruptedException {
        manager.add(new Task(0, "testTask", "testTaskDescr", TaskStatus.NEW, Duration.ofMinutes(5), startTime1));
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять 20 минут",
                        "duration": "5",
                        "startTime": "2025-02-04 10:00:00"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode());
        assertEquals("Задача пересекается с другими", response2.body());
    }


    @Test
    public void updateNotFound() throws IOException, InterruptedException {
        manager.add(new Task(0,"testTask", "testTaskDescr", TaskStatus.NEW, Duration.ofMinutes(5), startTime1));
        String taskJson = """
                {"id": "500",
                        "title": "Выгулять собаку",
                        "description": "Погулять с Джеком 20 минут",
                        "status": "DONE",
                        "duration": "100",
                        "startTime": "2025-02-21 15:45:01"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());;
        assertEquals("Не существует такой задачи", response.body());
    }

    @Test
    public void deleteNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        taskUrl = URI.create("http://localhost:8080/tasks/33");
        HttpRequest request = HttpRequest.newBuilder().uri(taskUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена", response.body());
    }

    @Test
    public void EndpointNotFound() throws IOException, InterruptedException {
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять 20 минут",
                        "duration": "5",
                        "startTime": "2025-02-04 10:00:00"
                    }""";
        HttpClient client = HttpClient.newHttpClient();
        URI tasksNegativeUrl = URI.create("http://localhost:8080/tasksksks");
        URI tasksNegativeGetUrl = URI.create("http://localhost:8080/tasksks/3rts");
        HttpRequest request = HttpRequest.newBuilder().uri(tasksNegativeUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request2 = HttpRequest.newBuilder().uri(tasksNegativeUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response2.statusCode());
        assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request3 = HttpRequest.newBuilder().uri(tasksNegativeGetUrl).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());
        assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request4 = HttpRequest.newBuilder().uri(tasksNegativeGetUrl).DELETE().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response4.statusCode());
        assertEquals("Такого эндпоинта не существует", response.body());
    }
}