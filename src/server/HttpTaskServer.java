package server;

import com.google.gson.*;
import com.sun.net.httpserver.*;
import manager.*;
import tasks.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.net.HttpURLConnection.*;
import static java.nio.charset.StandardCharsets.*;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private HttpServer httpTaskServer;

    private TaskManager taskManager;
    Gson gson = new Gson();

    private final static String TASKS_TASK = "/tasks/task/";
    private final static String TASKS_SUBTASK = "/tasks/subtask/";
    private final static String TASKS_EPIC = "/tasks/epic/";
    private final static String TASK_SUBTASKS_EPIC = "/tasks/subtasksepic/";
    private final static String TASK_PRIORITIZED = "/tasks/prioritized/";

    public HttpTaskServer() throws IOException, InterruptedException {
        taskManager = Managers.getDefault();
        try {
            httpTaskServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            httpTaskServer.createContext("/tasks/epic/", this::handelTaskManager);
            httpTaskServer.createContext("/tasks/subtask/", this::handelTaskManager);
            httpTaskServer.createContext("/tasks/task/", this::handelTaskManager);
            httpTaskServer.createContext("/tasks/prioritized/", this::handelTaskManager);
            httpTaskServer.createContext("/tasks/subtasksepic/", this::handelTaskManager);
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера");

        }
    }

    private void handelTaskManager(HttpExchange exchange) throws HttpTaskServerException {
        try {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String rawQuery = exchange.getRequestURI().getRawQuery();
                boolean isRawQuery = Objects.nonNull(rawQuery);

                switch (method) {
                    case "GET": {
                        actionGet(exchange, path, rawQuery, isRawQuery);
                        return;
                    }
                    case "POST": {
                        actionPost(exchange, path);
                        return;
                    }
                    case "DELETE": {
                        actionDelete(exchange, path, rawQuery, isRawQuery);
                        return;

                    }
                    default: {
                        System.out.println("Сервер ожидал метод GET, POST или DELETE, но получил неизвестный метод - " + method);
                        exchange.sendResponseHeaders(HTTP_BAD_METHOD, 0);
                    }
                }
            } catch (IOException e) {
                throw new HttpTaskServerException("Ошибка при обработке запроса: " + exchange.getRequestMethod() + exchange.getRequestURI().getPath() + exchange.getRequestURI().getRawQuery());
            }
        } catch (HttpTaskServerException e) {
            System.out.println(e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void actionDelete(HttpExchange exchange, String path, String rawQuery, boolean isRawQuery) throws IOException {
        if (!isRawQuery && path.equals(TASKS_TASK)) {
            taskManager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены");
            return;
        } else if (!isRawQuery && path.equals(TASKS_SUBTASK)) {
            taskManager.deleteAllSubtasks();
            sendText(exchange, "Все подзадачи удалены");
            return;
        } else if (!isRawQuery && path.equals(TASKS_EPIC)) {
            taskManager.deleteAllEpics();
            sendText(exchange, "Все эпики удалены");
            return;
        } else if (isRawQuery && path.equals(TASKS_TASK)) {
            deleteTaskById(exchange, rawQuery);

        } else if (isRawQuery && path.equals(TASKS_SUBTASK)) {
            deleteSubtaskById(exchange, rawQuery);

        } else if (isRawQuery && path.equals(TASKS_EPIC)) {
            deleteEpicById(exchange, rawQuery);
        }
    }

    private void deleteTaskById(HttpExchange exchange, String rawQuery) throws IOException {
        int id = Integer.parseInt(rawQuery.split("=")[1]);

        if (Objects.nonNull(taskManager.getTaskById(id))) {
            taskManager.deleteTaskById(id);
            sendText(exchange, "Задача удалена");
        } else {
            System.out.println("Задача с id=" + id + " не найдена");
            exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        }
    }

    private void deleteSubtaskById(HttpExchange exchange, String rawQuery) throws IOException {
        int id = Integer.parseInt(rawQuery.split("=")[1]);

        if (Objects.nonNull(taskManager.getSubtaskById(id))) {
            taskManager.deleteSubtaskById(id);
            sendText(exchange, "Подзадача удалена");
        } else {
            System.out.println("Подзадача с id=" + id + " не найдена");
            exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        }
    }

    private void deleteEpicById(HttpExchange exchange, String rawQuery) throws IOException {
        int id = Integer.parseInt(rawQuery.split("=")[1]);

        if (Objects.nonNull(taskManager.getEpicById(id))) {
            taskManager.deleteEpicById(id);
            sendText(exchange, "Эпик удален");
        } else {
            System.out.println("Эпик с id=" + id + " не найден");
            exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        }
    }

    private void actionPost(HttpExchange exchange, String path) throws IOException {
        if (path.equals(TASKS_EPIC)) {
            crateOrUpdateEpic(exchange);
        } else if (path.equals(TASKS_TASK)) {
            createOrUpdateTask(exchange);
        } else if (path.equals(TASKS_SUBTASK)) {
            createOrUpdateSubtask(exchange);

        }
    }

    private void createOrUpdateSubtask(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = gson.fromJson(readText(exchange), Subtask.class);
            Epic epic = (Epic) taskManager.getEpicById(subtask.getParentId());
            if (Objects.nonNull(subtask.getId()) && subtask.getId() != 0) {
                if (taskManager.updateSubtask(subtask)) {
                    sendText(exchange, "Подзадача успешно обновлена");
                    return;
                } else {
                    System.out.println("Подзадача не обновлена");
                    exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    return;
                }
            }
            if (taskManager.saveSubtask(subtask, epic)) {
                sendText(exchange, "Подзадача успешно сохранена");
            } else {
                System.out.println("Подзадача не сохранена");
                exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обработке запроса");
            exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
        }
    }

    private void createOrUpdateTask(HttpExchange exchange) throws IOException {
        try {
            Task task = gson.fromJson(readText(exchange), Task.class);

            if (Objects.nonNull(task.getId()) && task.getId() != 0) {
                if (taskManager.updateTask(task)) {
                    sendText(exchange, "Задача успешно обновлена");
                    return;
                } else {
                    System.out.println("Задача не обновлена");
                    exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                    return;
                }
            }
            if (taskManager.saveTaskAndEpic(task)) {
                sendText(exchange, "Задача успешно сохранена");
            } else {
                System.out.println("Задача не сохранена");
                exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обработке запроса");
            exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
        }
    }

    private void crateOrUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic epic = gson.fromJson(readText(exchange), Epic.class);
            if (Objects.nonNull(epic.getId()) && epic.getId() != 0) {
                if (taskManager.updateEpic(epic)) {
                    sendText(exchange, "Эпик успешно обновлен");
                    return;
                }
                System.out.println("Эпик не обновлен");
                exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
                return;
            }
            if (taskManager.saveTaskAndEpic(epic)) {
                sendText(exchange, "Эпик успешно сохранен");
                return;
            }
            System.out.println("Эпик не сохранен");
            exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
        } catch (Exception e) {
            System.out.println("Ошибка при обработке запроса");
            exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
        }
    }

    private void actionGet(HttpExchange exchange, String path, String rawQuery, boolean isRawQuery) throws IOException {

        if (!isRawQuery && path.equals(TASKS_EPIC)) {
            String allEpics = gson.toJson(taskManager.getListAllEpic());
            sendText(exchange, allEpics);
            return;

        } else if (!isRawQuery && path.equals(TASKS_TASK)) {
            String allTasks = gson.toJson(taskManager.getListAllTasks());
            sendText(exchange, allTasks);
            return;

        } else if (!isRawQuery && path.equals(TASKS_SUBTASK)) {
            String allSubtasks = gson.toJson(taskManager.getListAllSubtasks());
            sendText(exchange, allSubtasks);
            return;

        } else if (path.equals(TASK_SUBTASKS_EPIC)) {
            getListSubtasksInEpic(exchange, rawQuery);

        } else if (!isRawQuery && path.equals(TASK_PRIORITIZED)) {
            String prioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(exchange, prioritizedTasks);

        } else if (isRawQuery && path.equals(TASKS_EPIC)) {
            getEpicById(exchange, rawQuery);

        } else if (isRawQuery && path.equals(TASKS_SUBTASK)) {
            getSubtasksById(exchange, rawQuery);

        } else if (isRawQuery && path.equals(TASKS_TASK)) {
            getTasksById(exchange, rawQuery);

        } else {
            System.out.println("Указан неверный путь - " + exchange.getRequestURI().getPath());
            exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        }
    }

    private void getListSubtasksInEpic(HttpExchange exchange, String rawQuery) throws IOException {
        if (Objects.nonNull(rawQuery)) {
            int id = Integer.parseInt(rawQuery.split("=")[1]);
            if (Objects.nonNull(taskManager.getEpicById(id))) {
                Epic epic = (Epic) taskManager.getEpicById(id);
                if (Objects.nonNull(taskManager.getListSubtasksOfEpic(epic)) && !taskManager.getListSubtasksOfEpic(epic).isEmpty()) {
                    String subtasksOfEpic = gson.toJson(taskManager.getListSubtasksOfEpic((Epic) taskManager.getEpicById(id)));
                    sendText(exchange, subtasksOfEpic);
                } else {
                    System.out.println("Эпик с id=" + id + " не имеет подзадач");
                    exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                }
            } else {
                System.out.println("Эпик с id=" + id + " не найден.");
                exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
            }
        } else {
            System.out.println("В запросе отсутствует id эпика");
            exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
        }
    }

    private void getSubtasksById(HttpExchange exchange, String rawQuery) throws IOException {
        int id = Integer.parseInt(rawQuery.split("=")[1]);
        if (Objects.nonNull(taskManager.getSubtaskById(id))) {
            String subtask = gson.toJson(taskManager.getSubtaskById(id));
            sendText(exchange, subtask);
        } else {
            System.out.println("Подзадача с id=" + id + " не найден.");
            exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        }
    }

    private void getTasksById(HttpExchange exchange, String rawQuery) throws IOException {
        int id = Integer.parseInt(rawQuery.split("=")[1]);
        if (Objects.nonNull(taskManager.getTaskById(id))) {
            String task = gson.toJson(taskManager.getTaskById(id));
            sendText(exchange, task);
        } else {
            System.out.println("Задача с id=" + id + " не найден.");
            exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        }
    }

    private void getEpicById(HttpExchange exchange, String rawQuery) throws IOException {
        int id = Integer.parseInt(rawQuery.split("=")[1]);

        if (Objects.nonNull(taskManager.getEpicById(id))) {
            String epic = gson.toJson(taskManager.getEpicById(id));
            sendText(exchange, epic);
        } else {
            System.out.println("Эпик с id=" + id + " не найден.");
            exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        this.httpTaskServer.start();
    }

    public void stop() {
        this.httpTaskServer.stop(0);
        System.out.println("Сервер на порту " + PORT + " остановлен.");

    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(HTTP_OK, resp.length);
        h.getResponseBody().write(resp);
    }
}
