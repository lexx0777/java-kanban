package httpTest;

import http.HttpTaskServer;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.TaskStatus;
import model.Epic;
import model.Subtask;
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

public class HttpSubtaskTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    JsonTaskBuilder json = server.getJsonTaskBuilder();
    URI subtaskUrl = URI.create("http://localhost:8080/subtasks");
    LocalDateTime startTime1 = LocalDateTime.of(LocalDate.of(2025, 2, 4),
            LocalTime.of(10, 0));
    LocalDateTime startTime2 = LocalDateTime.of(LocalDate.of(2025, 2, 5),
            LocalTime.of(10, 0));

    public HttpSubtaskTest() throws IOException {
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
    public void getSubtasks() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        manager.add(new Subtask(0,"test_subtask", "test_subtask_descr", TaskStatus.NEW, 1, Duration.ofMinutes(100), startTime1));
        manager.add(new Subtask(0,"test_subtask2", "test_subtask_descr2", TaskStatus.NEW,1, Duration.ofMinutes(100), startTime2));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        String subtasks = json.toJson(manager.getSubtasks());
        Assertions.assertEquals(subtasks,response.body());
    }
/*
    @Test
    public void getSubTask() throws IOException, InterruptedException {
        manager.add(new Epic(1,"test_epic", "descr"));
        manager.add(new Subtask(0,"testSubtask", "testSubtaskDescr",  TaskStatus.NEW,1, Duration.ofMinutes(5), startTime1));

        Subtask subtask = manager.getSubtaskById(2);
        subtaskUrl = URI.create("http://localhost:8080/subtasks/" + subtask.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertEquals(json.toJson(subtask), response.body());


    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять 20 минут",
                        "epicId": "1",
                        "duration": "100",
                        "startTime": "2025-02-21 15:45:01"
                    }""";
        manager.add(new Epic(0,"test_epic", "descr"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        ArrayList<Subtask> subtasks = manager.getSubtasks();

        Assertions.assertNotNull(subtasks);
        Assertions.assertEquals(1, subtasks.size());
        Assertions.assertEquals("Выгулять собаку", subtasks.getFirst().getTitle());
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        manager.add(new Subtask(0,"testSubtask", "testSubtaskDescr",  TaskStatus.NEW,1, Duration.ofMinutes(5), startTime1));
        String taskJson = """
                {"id": 2,
                        "title": "Выгулять собаку",
                        "description": "Погулять с Джеком 20 минут",
                        "status": "DONE",
                        "epicId": "1",
                        "type": "TASK",
                        "duration": "100",
                        "startTime": "2025-02-21 15:45:01"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskUpd = manager.getSubtaskById(2);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Выгулять собаку", subtaskUpd.getTitle());
        Assertions.assertEquals("Погулять с Джеком 20 минут", subtaskUpd.getDescription());
        Assertions.assertEquals(TaskStatus.DONE, subtaskUpd.getStatus());
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        manager.add(new Subtask(0,"testTask", "testTaskDescr",  TaskStatus.NEW,1, Duration.ofMinutes(5), startTime1));
        Subtask subtask = manager.getSubtaskById(2);
        subtaskUrl = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        ArrayList<Subtask> subtasks = manager.getSubtasks();

        Assertions.assertTrue(subtasks.isEmpty());
    }

    @Test
    public void getSubtaskNotFound() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        manager.add(new Subtask(0,"testTask", "testTaskDescr",  TaskStatus.NEW,1, Duration.ofMinutes(5), startTime1));
        subtaskUrl = URI.create("http://localhost:8080/subtasks/3");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Подзадача с указанным id не найдена", response.body());

    }

    @Test
    public void addSubtaskEpicNotFound() throws IOException, InterruptedException {
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять с Джеком 20 минут",
                        "epicId": "1",
                        "duration": "5",
                        "startTime": "2025-02-04 10:00:00"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Эпика не существует. Подзадачу невозможно создать без эпика", response.body());
    }

    @Test
    public void addSubtaskIntersection() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        manager.add(new Subtask(0,"testTask", "testTaskDescr",  TaskStatus.NEW,1, Duration.ofMinutes(5), startTime1));
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять 20 минут",
                        "epicId": "1",
                        "duration": "5",
                        "startTime": "2025-02-04 10:00:00"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals("Подзадача пересекается с другими", response.body());
    }

    @Test
    public void updateNotFound() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        manager.add(new Subtask(0,"testTask", "testTaskDescr",  TaskStatus.NEW,1, Duration.ofMinutes(5), startTime1));
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять с Джеком 20 минут",
                        "status": "DONE",
                        "id": 50,
                        "epicId": "1",
                        "type": "SUBTASK",
                        "duration": "100",
                        "startTime": "2025-02-21 15:45:01",
                        "endTime": "2025-02-21 17:25:01"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Не существует такой подзадачи", response.body());
    }

    @Test
    public void deleteNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        subtaskUrl = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Подзадача не найдена", response.body());
    }

    @Test
    public void EndpointNotFound() throws IOException, InterruptedException {
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять с Джеком 20 минут",
                        "epicId": "1",
                        "duration": "5",
                        "startTime": "2025-02-04 10:00:00"
                    }""";
        HttpClient client = HttpClient.newHttpClient();
        URI subtaskNegativeUrl = URI.create("http://localhost:8080/subtasksdsds");
        URI subtasksNegativeGetUrl = URI.create("http://localhost:8080/subtasks/3rts");
        HttpRequest request = HttpRequest.newBuilder().uri(subtaskNegativeUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request2 = HttpRequest.newBuilder().uri(subtaskNegativeUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response2.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request3 = HttpRequest.newBuilder().uri(subtasksNegativeGetUrl).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response3.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request4 = HttpRequest.newBuilder().uri(subtasksNegativeGetUrl).DELETE().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response4.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());

    }

 */
}