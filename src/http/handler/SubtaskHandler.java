package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;
import model.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final JsonTaskBuilder jsonTaskBuilder;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.jsonTaskBuilder = new JsonTaskBuilder();
    }

    @Override
    protected void processGet(HttpExchange exchange, String path, boolean hasId) throws IOException {
        if (hasId)
            handleGetSubtask(exchange, path);
        else
            handleGetSubtasks(exchange);
    }

    @Override
    protected void processPost(HttpExchange exchange, String path, boolean hasId) throws IOException {
        if (hasId)
            handleUpdateSubtask(exchange, getRequestBody(exchange));
        else
            handleAddSubtask(exchange, getRequestBody(exchange));
    }

    @Override
    protected void processDelete(HttpExchange exchange, String path, boolean hasId) throws IOException {
        if (hasId)
            handleDeleteSubtask(exchange, path);
        else
            sendEndpointNotFound(exchange);
    }

    private void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, jsonTaskBuilder.toJson(taskManager.getSubtasks()));
    }

    private void handleGetSubtask(HttpExchange httpExchange, String path) {
        getTaskId(path)
                .map(taskId -> tryGetTask(httpExchange, () -> taskManager.getSubtaskById(taskId)))
                .map(jsonTaskBuilder::toJson)
                .ifPresentOrElse(json -> catchIOException(() -> sendText(httpExchange, json)),
                        () -> catchIOException(() -> sendEndpointNotFound(httpExchange))
                );
    }

    private void handleAddSubtask(HttpExchange httpExchange, String requestBody) throws IOException {
        try {
            Subtask subtask = (jsonTaskBuilder.fromJson(requestBody, Subtask.class));
            taskManager.add(subtask);
            sendTextUpdate(httpExchange, String.format("Подзадача добавлена: id %d", subtask.getId()));
        } catch (IntersectionException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleUpdateSubtask(HttpExchange httpExchange, String requestBody) throws IOException {
        try {
            Subtask subtask = (jsonTaskBuilder.fromJson(requestBody, Subtask.class));
            taskManager.update(subtask);
            sendTextUpdate(httpExchange, String.format("Подзадача %d обновлена", subtask.getId()));
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleDeleteSubtask(HttpExchange httpExchange, String path) {
        getTaskId(path)
                .ifPresentOrElse(
                        taskId -> {
                            try {
                                taskManager.removeSubtaskById(taskId);
                                catchIOException(() -> sendText(httpExchange, "Подзадача удалена"));
                            } catch (NotFoundException e) {
                                catchIOException(() -> sendNotFound(httpExchange, e.getMessage()));
                            }

                        }, () -> catchIOException(() -> sendEndpointNotFound(httpExchange)));
    }

}