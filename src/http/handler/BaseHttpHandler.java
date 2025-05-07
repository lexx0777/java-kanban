package http.handler;

import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {


    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            String requestBody = getRequestBody(exchange);
            boolean hasId;
            if (!requestBody.isEmpty()) {
                hasId = JsonParser.parseString(requestBody).getAsJsonObject().has("id");
            } else {
                hasId = false;
            }
            int partsCount = path.split("/").length;

            switch (method) {
                case "GET":
                    processGet(exchange, path);
                    break;
                case "POST":
                    processPost(exchange, path, hasId);
                    break;
                case "DELETE":
                    processDelete(exchange, path);
                    break;
                default:
                    sendNotFound(exchange, "Данный метод не предусмотрен");
            }
        } catch (NotFoundException e) {
            sendEndpointNotFound(exchange);
        }
    }

    protected void processGet(HttpExchange exchange, String path) throws IOException {}

    protected void processPost(HttpExchange exchange, String path, boolean hasId) throws IOException {}

    protected void processDelete(HttpExchange exchange, String path) throws IOException {}

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendTextUpdate(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(201, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(404, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(406, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendEndpointNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(405, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write("Такого эндпоинта не существует".getBytes(StandardCharsets.UTF_8));
        }
    }

    protected String getRequestBody(HttpExchange httpExchange) throws IOException {
        try (InputStream is = httpExchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected Optional<Integer> getTaskId(String path) {
        String[] parts = path.split("/");
        try {
            return Optional.of(Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected void catchIOException(FailableCallable f) {
        try {
            f.call();
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw new RuntimeException(e);
            } else {
                throw new RuntimeException("RuntimeException error");
            }
        }
    }

    protected Object tryGetTask(HttpExchange httpExchange, TaskCallable f) {
        try {
            return f.call();
        } catch (NotFoundException e) {
            catchIOException(() -> sendNotFound(httpExchange, e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    protected interface FailableCallable {
        void call() throws Exception;
    }

    protected interface TaskCallable {
        Object call() throws Exception;
    }
}