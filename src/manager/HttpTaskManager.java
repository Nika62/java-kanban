package manager;

import com.google.gson.*;
import server.*;
import tasks.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class HttpTaskManager extends FileBackedTasksManager {

    static Gson gson = new Gson();

    private static KVTaskClient client;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(url);
        this.client = new KVTaskClient(url);
    }

    @Override

    protected void save() {
        try {
            if (!listTasks.isEmpty()) {
                client.put("tasks", gson.toJson(getListAllTasks()));
            }
            if (!listSubtasks.isEmpty()) {
                client.put("subtasks", gson.toJson(getListAllSubtasks()));
            }
            if (!listEpics.isEmpty()) {
                client.put("epics", gson.toJson(getListAllEpic()));
            }
            if (!historyManager.getHistory().isEmpty()) {
                ArrayList<Integer> list = (ArrayList<Integer>) historyManager.getHistory()
                        .stream()
                        .map(task -> task.getId())
                        .collect(Collectors.toList());
                client.put("history", gson.toJson(list));
            }

            if (!sortedList.isEmpty()) {
                ArrayList<Integer> list = (ArrayList<Integer>) sortedList.stream()
                        .map(task -> task.getId())
                        .collect(Collectors.toList());
                client.put("sorted", gson.toJson(list));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HttpTaskManager load(String url) throws IOException, InterruptedException {
        HttpTaskManager newHttpTaskManager = new HttpTaskManager(url);
        JsonArray jsonArrayTasks = parseResponse(client.load("tasks"));
        if (Objects.nonNull(jsonArrayTasks)) {
            for (int i = 0; i < jsonArrayTasks.size(); i++) {
                JsonObject object = jsonArrayTasks.get(i).getAsJsonObject();
                Task task = gson.fromJson(object, Task.class);
                listTasks.put(task.getId(), task);
            }
        }

        JsonArray jsonArraySubtasks = parseResponse(client.load("subtasks"));
        if (Objects.nonNull(jsonArraySubtasks)) {
            for (int i = 0; i < jsonArraySubtasks.size(); i++) {
                JsonObject object = jsonArraySubtasks.get(i).getAsJsonObject();
                Subtask subtask = gson.fromJson(object, Subtask.class);
                listSubtasks.put(subtask.getId(), subtask);
            }
        }

        JsonArray jsonArrayEpics = parseResponse(client.load("epics"));
        if (Objects.nonNull(jsonArrayEpics)) {
            for (int i = 0; i < jsonArrayEpics.size(); i++) {
                JsonObject object = jsonArrayEpics.get(i).getAsJsonObject();
                Epic epic = gson.fromJson(object, Epic.class);
                listTasks.put(epic.getId(), epic);
            }
        }
        JsonArray jsonArrayHistory = parseResponse(client.load("history"));
        if (Objects.nonNull(jsonArrayHistory)) {
            for (int i = 0; i < jsonArrayHistory.size(); i++) {
                JsonPrimitive primitive = jsonArrayHistory.get(i).getAsJsonPrimitive();
                int id = gson.fromJson(primitive, Integer.class);
                if (listTasks.containsKey(id)) {
                    historyManager.add(listTasks.get(id));
                } else if (listSubtasks.containsKey(id)) {
                    historyManager.add(listSubtasks.get(id));
                } else if (listEpics.containsKey(id)) {
                    historyManager.add(listEpics.get(id));
                } else {
                    System.out.println("В списке задач, подзадач и эпиков нет задачи с id=" + id);
                }
                System.out.println(historyManager.getHistory());
            }
        }
        JsonArray jsonArraySorted = parseResponse(client.load("sorted"));
        if (Objects.nonNull(jsonArraySorted)) {
            for (int i = 0; i < jsonArraySorted.size(); i++) {
                JsonPrimitive primitive = jsonArraySorted.get(i).getAsJsonPrimitive();
                int id = gson.fromJson(primitive, Integer.class);
                if (listTasks.containsKey(id)) {
                    sortedList.add(listTasks.get(id));
                } else if (listSubtasks.containsKey(id)) {
                    sortedList.add(listSubtasks.get(id));
                } else {
                    System.out.println("В списке задач, подзадач и эпиков нет задачи с id=" + id);
                }
                System.out.println(historyManager.getHistory());
            }
        }
        return newHttpTaskManager;
    }


    private static JsonArray parseResponse(String response) {
        try {
            if (!response.isBlank()) {
                JsonElement jsonElement = JsonParser.parseString(response);
                if (jsonElement.isJsonArray()) {
                    return jsonElement.getAsJsonArray();
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обработке ответа от сервера");
        }
        return null;
    }
}
