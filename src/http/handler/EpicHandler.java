package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final JsonTaskBuilder jsonTaskBuilder;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.jsonTaskBuilder = new JsonTaskBuilder();
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        switch (pathParts.length) {
            case 4:
                handleGetEpicSubtasks(exchange, path);
                break;
            case 3:
                handleGetEpic(exchange, path);
                break;
            case 2:
                handleGetEpics(exchange);
                break;
            default:
                sendEndpointNotFound(exchange);
                break;
        }
    }

    @Override
    protected void processPost(HttpExchange exchange, String path, boolean hasId) throws IOException {
        if (hasId)
            handleAddEpic(exchange, getRequestBody(exchange));
        else
            sendEndpointNotFound(exchange);
    }

    @Override
    protected void processDelete(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length == 3)
            handleDeleteEpic(exchange, path);
        else
            sendEndpointNotFound(exchange);
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, jsonTaskBuilder.toJson(taskManager.getEpics()));
    }

    private void handleGetEpic(HttpExchange httpExchange, String path) {
        getTaskId(path)
                .map(taskId -> tryGetTask(httpExchange, () -> taskManager.getEpicById(taskId)))
                .map(jsonTaskBuilder::toJson)
                .ifPresentOrElse(json -> catchIOException(() -> sendText(httpExchange, json)),
                        () -> catchIOException(() -> sendEndpointNotFound(httpExchange))
                );
    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange, String path) {
        getTaskId(path)
                .map(taskId -> tryGetTask(httpExchange, () -> taskManager.getSubtasks(taskId)))
                .map(jsonTaskBuilder::toJson)
                .ifPresentOrElse(json -> catchIOException(() -> sendText(httpExchange, json)),
                        () -> catchIOException(() -> sendEndpointNotFound(httpExchange))
                );
    }

    private void handleAddEpic(HttpExchange httpExchange, String requestBody) throws IOException {
        try {
            Epic epic = (jsonTaskBuilder.fromJson(requestBody, Epic.class));
            taskManager.add(epic);
            sendTextUpdate(httpExchange, String.format("Эпик добавлен: id %d", epic.getId()));
        } catch (IntersectionException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange, String path) {
        getTaskId(path)
                .ifPresentOrElse(
                        taskId -> {
                            try {
                                taskManager.removeEpicById(taskId);
                                catchIOException(() -> sendText(httpExchange, "Эпик удален"));
                            } catch (NotFoundException e) {
                                catchIOException(() -> sendNotFound(httpExchange, e.getMessage()));
                            }

                        }, () -> catchIOException(() -> sendEndpointNotFound(httpExchange)));
    }

}