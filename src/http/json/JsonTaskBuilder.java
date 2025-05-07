package http.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonTaskBuilder {
    public Gson gson;

    public JsonTaskBuilder() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public String toJson(Object obj) {
        return this.gson.toJson(obj);
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        return this.gson.fromJson(json, clazz);
    }

    static class TaskDeserializer implements JsonDeserializer<Task> {
        @Override
        public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String tittle = jsonObject.get("title").getAsString();
            String description = jsonObject.get("description").getAsString();
            Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (jsonObject.has("id") && jsonObject.get("id").getAsInt() != 0) {
                int id = jsonObject.get("id").getAsInt();
                TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
                return new Task(id, tittle, description, status, duration, startTime);
            } else {
                return new Task(0, tittle, description, TaskStatus.NEW, duration, startTime);
            }

        }
    }

    static class SubtaskDeserializer implements JsonDeserializer<Subtask> {
        @Override
        public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String title = jsonObject.get("title").getAsString();
            String description = jsonObject.get("description").getAsString();
            Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            int epicId = jsonObject.get("epicId").getAsInt();

            if (jsonObject.has("id") && jsonObject.get("id").getAsInt() != 0) {
                int id = jsonObject.get("id").getAsInt();
                TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
                return new Subtask(id, title, description, status, epicId, duration, startTime);
            } else {
                return new Subtask(0, title, description, TaskStatus.NEW, epicId, duration, startTime);
            }

        }
    }

    static class EpicDeserializer implements JsonDeserializer<Epic> {
        @Override
        public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String tittle = jsonObject.get("title").getAsString();
            String description = jsonObject.get("description").getAsString();
            return new Epic(0, tittle, description);
        }
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(dateTimeFormatter));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), dateTimeFormatter);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(String.format("%d", duration.toMinutes()));
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(jsonReader.nextInt());
        }
    }
}