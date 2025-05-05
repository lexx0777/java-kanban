package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.Endpoint;
import http.json.JsonTaskBuilder;
import controllers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final JsonTaskBuilder jsonTaskBuilder;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.jsonTaskBuilder = new JsonTaskBuilder();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String requestBody = getRequestBody(httpExchange);
        Endpoint endpoint = Endpoint.getEndpoint(path, httpExchange.getRequestMethod(), requestBody);
        if (endpoint.equals(Endpoint.GET_PRIORITIZED)) {
            sendText(httpExchange, jsonTaskBuilder.toJson(taskManager.getPrioritizedTasks()));
        } else {
            sendEndpointNotFound(httpExchange);
        }
    }
}