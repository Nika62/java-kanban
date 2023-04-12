package server;

import com.google.gson.*;
import com.sun.net.httpserver.*;
import manager.*;
import tasks.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import static java.nio.charset.StandardCharsets.*;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private HttpServer httpTaskServer;

    private TaskManager taskManager;

    Gson gson = new Gson();

    public HttpTaskServer() {
        taskManager = Managers.getDefault();
        try {
            httpTaskServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            httpTaskServer.createContext("/tasks/epic/", this::handelTaskManager);
            httpTaskServer.createContext("/tasks/{task}/?id={id}", this::handelTaskManager);
            httpTaskServer.createContext("/tasks/prioritized", this::handelTaskManager);
            httpTaskServer.createContext("/load", this::handelTaskManager);
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера");
        }
    }

    public HttpTaskServer(TaskManager<Task> taskManager) {
        try {
            this.taskManager = taskManager;
            httpTaskServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            httpTaskServer.createContext("/tasks/{task}/", this::handelTaskManager);
            httpTaskServer.createContext(" /tasks/subtasks%20of%20epic/", this::handelTaskManager);
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера");
        }
    }

    private void handelTaskManager(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String rawQuery = exchange.getRequestURI().getRawQuery();

            //"http://localhost:8080/tasks/task/?id=1
            switch (method) {
                case "GET": {
                    if (Objects.isNull(rawQuery) && path.equals("/tasks/epic/")) {
                        String allEpics = gson.toJson(taskManager.getListAllEpic());
                        sendText(exchange, allEpics);
                        return;

                    } else if (Objects.isNull(rawQuery) && path.equals("/tasks/task/")) {
                        String allTasks = gson.toJson(taskManager.getListAllTasks());
                        sendText(exchange, allTasks);
                        return;

                    } else if (Objects.isNull(rawQuery) && path.equals("/tasks/subtask")) {
                        String allSubtasks = gson.toJson(taskManager.getListAllSubtasks());
                        sendText(exchange, allSubtasks);
                        return;

                    } else if (Objects.nonNull(rawQuery) && Pattern.matches("^/tasks/subtasks%20of%20epic/?id=\\d+$", path)) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (taskManager.getListAllEpic().contains(id)) {
                            String subtasksOfEpic = gson.toJson(taskManager.getListSubtasksOfEpic((Epic) taskManager.getEpicById(id)));
                            sendText(exchange, subtasksOfEpic);
                            return;
                        } else {
                            System.out.println("Эпик с id=" + id + " не найден.");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }
                    } else if (Objects.isNull(rawQuery) && path.equals("/tasks/prioritized")) {
                        String prioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(exchange, prioritizedTasks);
                        return;

                    } else if (Objects.nonNull(rawQuery) && path.equals("/tasks/epic/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (taskManager.getListAllEpic().contains(id)) {
                            String epic = gson.toJson(taskManager.getEpicById(id));
                            sendText(exchange, epic);
                            return;
                        } else {
                            System.out.println("Эпик с id=" + id + " не найден.");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }

                    } else if (Objects.nonNull(rawQuery) && path.equals("tasks/subtask/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (taskManager.getListAllSubtasks().contains(id)) {
                            String subtask = gson.toJson(taskManager.getSubtaskById(id));
                            sendText(exchange, subtask);
                            return;
                        } else {
                            System.out.println("Подзадача с id=" + id + " не найден.");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }

                    } else if (Objects.nonNull(rawQuery) && path.equals("tasks/task/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (taskManager.getListAllTasks().contains(id)) {
                            String task = gson.toJson(taskManager.getTaskById(id));
                            sendText(exchange, task);
                            return;
                        } else {
                            System.out.println("Задача с id=" + id + " не найден.");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }

                    } else {
                        System.out.println("Указан неверный путь - " + exchange.getRequestURI().getPath());
                        exchange.sendResponseHeaders(405, 0);
                        return;
                    }
                }
                case "POST": {

                }
                case "DELETE": {

                }
                default: {
                    System.out.println("Сервер ожидал метод GET, POST или DELETE, но получил неизвестный метод - " + method);
                    exchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        this.httpTaskServer.start();
    }

    public void stop() {
        httpTaskServer.stop(0);
        System.out.println("Сервер" + httpTaskServer + " на порту " + PORT + " остановлен.");

    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();
    }
}
