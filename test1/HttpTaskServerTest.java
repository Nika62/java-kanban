import com.google.gson.*;
import manager.*;
import org.junit.jupiter.api.*;
import server.*;
import tasks.*;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.time.*;

import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static HttpTaskServer httpTaskServer;
    HttpClient client = HttpClient.newHttpClient();
    private static KVServer kvServer;
    private Task taskForTestServer;
    private Epic epicForTestServer;
    private Subtask subtaskForTestServer;
    private Subtask subtaskForTestServer2;
    private Epic epicForTestServerWithoutSub;
    private Gson gson = new Gson();
    TaskManager manager;

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();


    }

    @BeforeEach
    public void before() throws IOException, InterruptedException {
        manager = Managers.getDefault();
        taskForTestServer = new Task("taskForTestServer", "TASK test http server", 50, LocalDateTime.parse("1999-12-11T17:17"));
        epicForTestServer = new Epic("epicForTestServer", "EPIC");
        subtaskForTestServer = new Subtask(2, "subtaskForTestServer 1", "SUBTASK-1", 10, LocalDateTime.parse("2000-10-10T00:00:02"));
        subtaskForTestServer2 = new Subtask(2, "subtask 2", "SUBTASK-2", 5, LocalDateTime.parse("2000-11-11T11:10"));
        epicForTestServerWithoutSub = new Epic("epicF", "EPIC WITHOUT SBU");
        manager.saveTaskAndEpic(taskForTestServer);
        manager.saveTaskAndEpic(epicForTestServer);
        manager.saveSubtask(subtaskForTestServer, epicForTestServer);
        manager.saveSubtask(subtaskForTestServer2, epicForTestServer);
        manager.saveTaskAndEpic(epicForTestServerWithoutSub);
    }

    @AfterEach
    public void after() {
        manager.deleteAllSubtasks();
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager = null;
    }

    @AfterAll
    public static void afterAll() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(manager.getListAllTasks()));
    }

    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(manager.getListAllSubtasks()));
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(manager.getListAllEpic()));
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(epicForTestServer));
    }

    @Test
    void shouldGetEpicByWrongId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_NOT_FOUND);
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(subtaskForTestServer));
    }

    @Test
    void shouldGetSubtasksByWrongId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=8");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_NOT_FOUND);
    }

    @Test
    void shouldGetSTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(taskForTestServer));
    }

    @Test
    void shouldGetTaskByWrongId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=11");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_NOT_FOUND);
    }

    @Test
    void shouldGetSubtasksEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtasksepic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(epicForTestServer.getSubtasks()));
    }

    @Test
    void shouldGetSubtasksEpicWithoutSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtasksepic/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_NOT_FOUND);
    }

    @Test
    void shouldGetPrioritized() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), gson.toJson(manager.getPrioritizedTasks()));
    }

    @Test
    void shouldSaveTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task taskForTestSave = new Task("TaskSave", "task");
        String json = gson.toJson(taskForTestSave);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), "Задача успешно сохранена");
        assertEquals(manager.getListAllTasks().size(), 2);

    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        taskForTestServer.setDescription("UPDATE");
        String json = gson.toJson(taskForTestServer);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), "Задача успешно обновлена");
        assertEquals(((Task) manager.getListAllTasks().get(0)).getDescription(), "UPDATE");
    }

    @Test
    void shouldSaveEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epicForTestSave = new Epic("EPICSave", "epic");
        String json = gson.toJson(epicForTestSave);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), "Эпик успешно сохранен");
        assertEquals(manager.getListAllEpic().size(), 3);
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        epicForTestServer.setDescription("UPDATE");
        String json = gson.toJson(epicForTestServer);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), "Эпик успешно обновлен");
        assertEquals(((Epic) manager.getListAllEpic().get(0)).getDescription(), "UPDATE");
    }

    @Test
    void shouldSaveSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Subtask subtaskForTestSave = new Subtask(2, "SubtaskSave", "subtask");
        String json = gson.toJson(subtaskForTestSave);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), "Подзадача успешно сохранена");
        assertEquals(manager.getListAllSubtasks().size(), 3);
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        subtaskForTestServer.setDescription("UPDATE");
        String json = gson.toJson(subtaskForTestServer);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertEquals(response.body(), "Подзадача успешно обновлена");
        assertEquals(((Subtask) manager.getListAllSubtasks().get(0)).getDescription(), "UPDATE");
    }

    @Test
    void shouldDeleteAllTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertTrue(manager.deleteAllTasks().isEmpty());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertFalse(manager.getListAllTasks().contains(taskForTestServer));
    }

    @Test
    void shouldDeleteTaskByWrongId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=12");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_NOT_FOUND);
    }

    @Test
    void shouldDeleteAllSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertTrue(manager.deleteAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertFalse(manager.getListAllSubtasks().contains(subtaskForTestServer));
    }

    @Test
    void shouldDeleteSubtaskByWrongId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=12");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_NOT_FOUND);
    }

    @Test
    void shouldDeleteAllEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertTrue(manager.deleteAllEpics().isEmpty());
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_OK);
        assertFalse(manager.getListAllEpic().contains(epicForTestServer));
    }

    @Test
    void shouldDeleteEpicByWrongId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=12");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), HTTP_NOT_FOUND);
    }
}