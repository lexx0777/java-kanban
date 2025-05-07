package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final JsonTaskBuilder jsonTaskBuilder;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.jsonTaskBuilder = new JsonTaskBuilder();
    }

    @Override
    protected void processGet(HttpExchange exchange, String path, boolean hasId) throws IOException {
        sendText(exchange, jsonTaskBuilder.toJson(taskManager.getHistory()));
    }
}