package http;

import com.google.gson.JsonParser;

public enum Endpoint {
    GET_TASKS,
    GET_TASK,
    CREATE_TASK,
    UPDATE_TASK,
    DELETE_TASK,
    GET_SUBTASKS,
    GET_SUBTASK,
    CREATE_SUBTASK,
    UPDATE_SUBTASK,
    DELETE_SUBTASK,
    GET_EPICS,
    GET_EPIC,
    GET_EPIC_SUBTASKS,
    CREATE_EPIC,
    DELETE_EPIC,
    GET_HISTORY,
    GET_PRIORITIZED,
    UNKNOWN_ENDPOINT;

    public static Endpoint getEndpoint(String requestPath, String requestMethod, String requestBody) {
        String[] pathParts = requestPath.split("/");
        boolean hasId;
        if (!requestBody.isEmpty()) {
            hasId = JsonParser.parseString(requestBody).getAsJsonObject().has("id");
        } else {
            hasId = false;
        }

        switch (pathParts[1]) {
            case "tasks" -> {
                return switch (pathParts.length) {
                    case 2 -> switch (requestMethod) {
                        case "GET" -> GET_TASKS;
                        case "POST" -> {
                            if (hasId) {
                                yield UPDATE_TASK;
                            } else {
                                yield CREATE_TASK;
                            }
                        }
                        default -> UNKNOWN_ENDPOINT;
                    };
                    case 3 -> switch (requestMethod) {
                        case "GET" -> GET_TASK;
                        case "DELETE" -> DELETE_TASK;
                        default -> UNKNOWN_ENDPOINT;
                    };
                    default -> UNKNOWN_ENDPOINT;
                };
            }
            case "subtasks" -> {
                return switch (pathParts.length) {
                    case 2 -> switch (requestMethod) {
                        case "GET" -> GET_SUBTASKS;
                        case "POST" -> {
                            if (hasId) {
                                yield UPDATE_SUBTASK;
                            } else {
                                yield CREATE_SUBTASK;
                            }
                        }
                        default -> UNKNOWN_ENDPOINT;
                    };
                    case 3 -> switch (requestMethod) {
                        case "GET" -> GET_SUBTASK;
                        case "DELETE" -> DELETE_SUBTASK;
                        default -> UNKNOWN_ENDPOINT;
                    };
                    default -> UNKNOWN_ENDPOINT;
                };
            }
            case "epics" -> {
                return switch (pathParts.length) {
                    case 2 -> switch (requestMethod) {
                        case "GET" -> GET_EPICS;
                        case "POST" -> CREATE_EPIC;
                        default -> UNKNOWN_ENDPOINT;
                    };
                    case 3 -> switch (requestMethod) {
                        case "GET" -> GET_EPIC;
                        case "DELETE" -> DELETE_EPIC;
                        default -> UNKNOWN_ENDPOINT;
                    };
                    case 4 -> {
                        if (pathParts[3].equals("subtasks") && requestMethod.equals("GET")) {
                            yield GET_EPIC_SUBTASKS;
                        } else {
                            yield UNKNOWN_ENDPOINT;
                        }
                    }
                    default -> UNKNOWN_ENDPOINT;
                };
            }
            case "history" -> {
                if (pathParts.length == 2 && requestMethod.equals("GET")) {
                    return GET_HISTORY;
                } else {
                    return UNKNOWN_ENDPOINT;
                }
            }
            case "prioritized" -> {
                if (pathParts.length == 2 && requestMethod.equals("GET")) {
                    return GET_PRIORITIZED;
                } else {
                    return UNKNOWN_ENDPOINT;
                }
            }
            default -> {
                return UNKNOWN_ENDPOINT;
            }
        }
    }
}