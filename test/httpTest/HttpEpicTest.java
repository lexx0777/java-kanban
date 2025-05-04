package httpTest;

import controllers.InMemoryTaskManager;
import http.HttpTaskServer;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import controllers.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class HttpEpicTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    JsonTaskBuilder json = server.getJsonTaskBuilder();
    URI epicUrl = URI.create("http://localhost:8080/epics");

    public HttpEpicTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }
/*
    @Test
    public void getEpics() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        manager.add(new Epic(0,"test_epic2", "descr2"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(epicUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        String epics = json.toJson(manager.getEpics());
        Assertions.assertEquals(epics, response.body());
    }
*/

/*
    @Test
    public void getEpic() throws IOException, InterruptedException {
        //manager = Managers.getDefault();
        Epic epic = new Epic(0,"test_epic", "descr");
        manager.add(epic);
        //epic = manager.getEpicById(1);
        epicUrl = URI.create("http://localhost:8080/epics/"+epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(epicUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(json.toJson(epic), response.body());
    }
*/
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять 30 минут"
                    }""";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(epicUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        ArrayList<Epic> epics = manager.getEpics();

        Assertions.assertNotNull(epics);
        Assertions.assertEquals(1, epics.size());
        Assertions.assertEquals("Выгулять собаку", epics.getFirst().getTitle());
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        Epic epic = manager.getEpicById(1);
        epicUrl = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(epicUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        ArrayList<Epic> epics = manager.getEpics();

        Assertions.assertTrue(epics.isEmpty());
    }

    @Test
    public void getEpicNotFound() throws IOException, InterruptedException {
        manager.add(new Epic(0,"test_epic", "descr"));
        epicUrl = URI.create("http://localhost:8080/epics/3");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(epicUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Эпик с указанным id не найден", response.body());

    }

    @Test
    public void deleteNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        epicUrl = URI.create("http://localhost:8080/epics/3");
        HttpRequest request = HttpRequest.newBuilder().uri(epicUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Эпик не найден", response.body());
    }

    @Test
    public void EndpointNotFound() throws IOException, InterruptedException {
        String taskJson = """
                {
                        "title": "Выгулять собаку",
                        "description": "Погулять 20 минут"
                    }""";
        HttpClient client = HttpClient.newHttpClient();
        URI epicsNegativeUrl = URI.create("http://localhost:8080/epicsdsds");
        URI epicsNegativeGetUrl = URI.create("http://localhost:8080/epics/3rts");
        HttpRequest request = HttpRequest.newBuilder().uri(epicsNegativeUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request2 = HttpRequest.newBuilder().uri(epicsNegativeUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response2.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request3 = HttpRequest.newBuilder().uri(epicsNegativeGetUrl).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response3.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());
        HttpRequest request4 = HttpRequest.newBuilder().uri(epicsNegativeGetUrl).DELETE().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response4.statusCode());
        Assertions.assertEquals("Такого эндпоинта не существует", response.body());
    }
}