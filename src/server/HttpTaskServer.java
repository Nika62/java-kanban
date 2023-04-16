package server;

import com.google.gson.*;
import com.sun.net.httpserver.*;
import manager.*;
import tasks.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.*;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private HttpServer httpTaskServer;

    private TaskManager taskManager;
    Gson gson = new Gson();

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

    private void handelTaskManager(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String rawQuery = exchange.getRequestURI().getRawQuery();

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

                    } else if (Objects.isNull(rawQuery) && path.equals("/tasks/subtask/")) {
                        String allSubtasks = gson.toJson(taskManager.getListAllSubtasks());
                        sendText(exchange, allSubtasks);
                        return;

                    } else if (path.equals("/tasks/subtasksepic/")) {
                        if (Objects.nonNull(rawQuery)) {
                            int id = Integer.parseInt(rawQuery.split("=")[1]);
                            if (Objects.nonNull(taskManager.getEpicById(id))) {
                                Epic epic = (Epic) taskManager.getEpicById(id);
                                if (Objects.nonNull(taskManager.getListSubtasksOfEpic(epic)) && !taskManager.getListSubtasksOfEpic(epic).isEmpty()) {
                                    String subtasksOfEpic = gson.toJson(taskManager.getListSubtasksOfEpic((Epic) taskManager.getEpicById(id)));
                                    sendText(exchange, subtasksOfEpic);
                                    return;
                                } else {
                                    System.out.println("Эпик с id=" + id + " не имеет подзадач");
                                    exchange.sendResponseHeaders(404, 0);
                                    return;
                                }
                            } else {
                                System.out.println("Эпик с id=" + id + " не найден.");
                                exchange.sendResponseHeaders(404, 0);
                                return;
                            }
                        } else {
                            System.out.println("В запросе отсутствует id эпика");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                    } else if (Objects.isNull(rawQuery) && path.equals("/tasks/prioritized/")) {
                        String prioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(exchange, prioritizedTasks);
                        return;

                    } else if (Objects.nonNull(rawQuery) && path.equals("/tasks/epic/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (Objects.nonNull(taskManager.getEpicById(id))) {
                            String epic = gson.toJson(taskManager.getEpicById(id));
                            sendText(exchange, epic);
                            return;
                        } else {
                            System.out.println("Эпик с id=" + id + " не найден.");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }

                    } else if (Objects.nonNull(rawQuery) && path.equals("/tasks/subtask/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (Objects.nonNull(taskManager.getSubtaskById(id))) {
                            String subtask = gson.toJson(taskManager.getSubtaskById(id));
                            sendText(exchange, subtask);
                            return;
                        } else {
                            System.out.println("Подзадача с id=" + id + " не найден.");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }

                    } else if (Objects.nonNull(rawQuery) && path.equals("/tasks/task/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (Objects.nonNull(taskManager.getTaskById(id))) {
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
                        exchange.sendResponseHeaders(404, 0);
                        return;
                    }
                }
                case "POST": {
                    if (path.equals("/tasks/epic/")) {
                        try {
                            Epic epic = gson.fromJson(readText(exchange), Epic.class);
                            if (Objects.nonNull(epic.getId()) && epic.getId() != 0) {
                                if (taskManager.updateEpic(epic)) {
                                    sendText(exchange, "Эпик успешно обновлен");
                                    return;
                                }
                                System.out.println("Эпик не обновлен");
                                exchange.sendResponseHeaders(400, 0);
                                return;
                            }
                            if (taskManager.saveTaskAndEpic(epic)) {
                                sendText(exchange, "Эпик успешно сохранен");
                                return;
                            }
                            System.out.println("Эпик не сохранен");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        } catch (Exception e) {
                            System.out.println("Ошибка при обработке запроса");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                    } else if (path.equals("/tasks/task/")) {
                        try {
                            Task task = gson.fromJson(readText(exchange), Task.class);
                            if (Objects.nonNull(task.getId()) && task.getId() != 0) {
                                if (taskManager.updateTask(task)) {
                                    sendText(exchange, "Задача успешно обновлена");
                                    return;
                                } else {
                                    System.out.println("Задача не обновлена");
                                    exchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                            }
                            if (taskManager.saveTaskAndEpic(task)) {
                                sendText(exchange, "Задача успешно сохранена");
                                return;
                            } else {
                                System.out.println("Задача не сохранена");
                                exchange.sendResponseHeaders(400, 0);
                                return;
                            }
                        } catch (Exception e) {
                            System.out.println("Ошибка при обработке запроса");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                    } else if (path.equals("/tasks/subtask/")) {
                        try {
                            Subtask subtask = gson.fromJson(readText(exchange), Subtask.class);
                            Epic epic = (Epic) taskManager.getEpicById(subtask.getParentId());
                            if (Objects.nonNull(subtask.getId()) && subtask.getId() != 0) {
                                if (taskManager.updateSubtask(subtask)) {
                                    sendText(exchange, "Подзадача успешно обновлена");
                                    return;
                                } else {
                                    System.out.println("Подзадача не обновлена");
                                    exchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                            }
                            if (taskManager.saveSubtask(subtask, epic)) {
                                sendText(exchange, "Подзадача успешно сохранена");
                                return;
                            } else {
                                System.out.println("Подзадача не сохранена");
                                exchange.sendResponseHeaders(400, 0);
                                return;
                            }
                        } catch (Exception e) {
                            System.out.println("Ошибка при обработке запроса");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }

                    }
                }
                case "DELETE": {
                    if (Objects.isNull(rawQuery) && path.equals("/tasks/task/")) {
                        taskManager.deleteAllTasks();
                        sendText(exchange, "Все задачи удалены");
                        return;
                    } else if (Objects.isNull(rawQuery) && path.equals("/tasks/subtask/")) {
                        taskManager.deleteAllSubtasks();
                        sendText(exchange, "Все подзадачи удалены");
                        return;
                    } else if (Objects.isNull(rawQuery) && path.equals("/tasks/epic/")) {
                        taskManager.deleteAllEpics();
                        sendText(exchange, "Все эпики удалены");
                        return;
                    } else if (Objects.nonNull(rawQuery) && path.equals("/tasks/task/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (Objects.nonNull(taskManager.getTaskById(id))) {
                            taskManager.deleteTaskById(id);
                            sendText(exchange, "Задача удалена");
                            return;
                        } else {
                            System.out.println("Задача с id=" + id + " не найдена");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }
                    } else if (Objects.nonNull(rawQuery) && path.equals("/tasks/subtask/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (Objects.nonNull(taskManager.getSubtaskById(id))) {
                            taskManager.deleteSubtaskById(id);
                            sendText(exchange, "Подзадача удалена");
                            return;
                        } else {
                            System.out.println("Подзадача с id=" + id + " не найдена");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }
                    } else if (Objects.nonNull(rawQuery) && path.equals("/tasks/epic/")) {
                        int id = Integer.parseInt(rawQuery.split("=")[1]);
                        if (Objects.nonNull(taskManager.getEpicById(id))) {
                            taskManager.deleteEpicById(id);
                            sendText(exchange, "Эпик удален");
                            return;
                        } else {
                            System.out.println("Эпик с id=" + id + " не найден");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }
                    }

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
        this.httpTaskServer.stop(0);
        System.out.println("Сервер на порту " + PORT + " остановлен.");

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
}
