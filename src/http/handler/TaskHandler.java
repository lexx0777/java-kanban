package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;
import model.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final JsonTaskBuilder jsonTaskBuilder;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.jsonTaskBuilder = new JsonTaskBuilder();
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length == 3)
            handleGetTask(exchange, path);
        else
            handleGetTasks(exchange);
    }

    @Override
    protected void processPost(HttpExchange exchange, String path, boolean hasId) throws IOException {
        if (hasId)
            handleUpdateTask(exchange, getRequestBody(exchange));
        else
            handleAddTask(exchange, getRequestBody(exchange));
    }

    @Override
    protected void processDelete(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length == 3)
            handleDeleteTask(exchange, path);
        else
            sendEndpointNotFound(exchange);
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, jsonTaskBuilder.toJson(taskManager.getTasks()));
    }

    private void handleGetTask(HttpExchange httpExchange, String path) {
        getTaskId(path)
                .map(taskId -> tryGetTask(httpExchange, () -> taskManager.getTaskById(taskId)))
                .map(jsonTaskBuilder::toJson)
                .ifPresentOrElse(json -> catchIOException(() -> sendText(httpExchange, json)),
                        () -> catchIOException(() -> sendEndpointNotFound(httpExchange))
                );
    }

    private void handleAddTask(HttpExchange httpExchange, String requestBody) throws IOException {
        try {
            Task task = (jsonTaskBuilder.fromJson(requestBody, Task.class));
            taskManager.add(task);
            sendTextUpdate(httpExchange, String.format("Задача добавлена: id %d", task.getId()));
        } catch (IntersectionException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        } catch (Exception e) { // JsonParseException или другая ошибка десериализации
            sendBadRequest(httpExchange, "Неверный формат задачи: " + e.getMessage());
        }
    }

    private void handleUpdateTask(HttpExchange httpExchange, String requestBody) throws IOException {
        try {
            Task task = (jsonTaskBuilder.fromJson(requestBody, Task.class));
            taskManager.update(task);
            sendTextUpdate(httpExchange, String.format("Задача %d обновлена", task.getId()));
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (Exception e) { // JsonParseException или другая ошибка десериализации
            sendBadRequest(httpExchange, "Неверный формат задачи: " + e.getMessage());
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange, String path) {
        getTaskId(path)
                .ifPresentOrElse(
                        taskId -> {
                            try {
                                taskManager.removeTaskById(taskId);
                                catchIOException(() -> sendText(httpExchange, "Задача удалена"));
                            } catch (NotFoundException e) {
                                catchIOException(() -> sendNotFound(httpExchange, e.getMessage()));
                            }

                        }, () -> catchIOException(() -> sendEndpointNotFound(httpExchange)));
    }
}