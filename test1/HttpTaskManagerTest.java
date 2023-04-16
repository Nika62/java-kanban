import manager.*;
import org.junit.jupiter.api.*;
import server.*;
import tasks.*;

import java.io.*;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    private HttpTaskManager manager;
    private Task taskForTestHttpM;
    private Epic epicForTestHttpM;
    private Subtask subtaskForTestHttpM;
    private Subtask subtaskForTestHttpM2;
    private Epic epicForTestHttpMWithoutSub;
    private static KVServer server;

    @BeforeAll
    public static void beforeAll() throws IOException {
        server = new KVServer();
        server.start();
    }

    @BeforeEach
    public void before() throws IOException, InterruptedException {
        manager = new HttpTaskManager("http://localhost:8078");
        taskForTestHttpM = new Task("taskForTest HttpTaskManager", "TASK test  HttpTaskManager", 50, LocalDateTime.parse("1999-12-11T17:17"));
        epicForTestHttpM = new Epic("epicForTest HttpTaskManager", "EPIC");
        subtaskForTestHttpM = new Subtask(2, "subtaskForTestHttpM 1", "SUBTASK-1", 10, LocalDateTime.parse("2000-10-10T00:00:02"));
        subtaskForTestHttpM2 = new Subtask(2, "subtask 2", "SUBTASK-2 Http task Manager", 5, LocalDateTime.parse("2000-11-11T11:10"));
        epicForTestHttpMWithoutSub = new Epic("epic2", "EPIC WITHOUT SBU Http task Manager");
        manager.saveTaskAndEpic(taskForTestHttpM);
        manager.saveTaskAndEpic(epicForTestHttpM);
        manager.saveSubtask(subtaskForTestHttpM, epicForTestHttpM);
        manager.saveSubtask(subtaskForTestHttpM2, epicForTestHttpM);
        manager.saveTaskAndEpic(epicForTestHttpMWithoutSub);
        manager.getTaskById(1);
        manager.getEpicById(2);
        manager.getSubtaskById(3);
        manager.getSubtaskById(4);
        manager.getEpicById(5);
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
        server.stop();
    }

    @Test
    void shouldLoadTasks() throws IOException, InterruptedException {
        HttpTaskManager managerForTestLoad = new HttpTaskManager("http://localhost:8078");
        assertEquals(managerForTestLoad.getListTasks().size(), 1);
        assertTrue(managerForTestLoad.getListAllTasks().contains(taskForTestHttpM));
    }

    @Test
    void shouldLoadSubtasks() throws IOException, InterruptedException {
        HttpTaskManager managerForTestLoad = new HttpTaskManager("http://localhost:8078");
        assertEquals(managerForTestLoad.getListSubtasks().size(), 2);
        assertTrue(managerForTestLoad.getListAllSubtasks().contains(subtaskForTestHttpM));
        assertTrue(managerForTestLoad.getListAllSubtasks().contains(subtaskForTestHttpM2));
    }

    @Test
    void shouldLoadEpics() throws IOException, InterruptedException {
        HttpTaskManager managerForTestLoad = new HttpTaskManager("http://localhost:8078");
        assertEquals(managerForTestLoad.getListAllEpic().size(), 2);
        assertTrue(managerForTestLoad.getListAllEpic().contains(epicForTestHttpM));
        assertTrue(managerForTestLoad.getListAllEpic().contains(epicForTestHttpMWithoutSub));
    }

    @Test
    void shouldLoadEpicWithSubtasks() throws IOException, InterruptedException {
        HttpTaskManager managerForTestLoad = new HttpTaskManager("http://localhost:8078");
        Epic newEpic = managerForTestLoad.getListEpics().get(2);
        assertEquals(newEpic.getSubtasks().size(), 2);
        assertTrue(newEpic.getSubtasks().contains(subtaskForTestHttpM));
        assertTrue(newEpic.getSubtasks().contains(subtaskForTestHttpM2));
        assertEquals(newEpic.getStartTime(), subtaskForTestHttpM.getStartTime());
        assertEquals(newEpic.getEndTime(), subtaskForTestHttpM2.getEndTime());
    }

    @Test
    void shouldLoadEpicWithoutSubtasks() throws IOException, InterruptedException {
        HttpTaskManager managerForTestLoad = new HttpTaskManager("http://localhost:8078");
        Epic newEpic = managerForTestLoad.getListEpics().get(5);
        assertEquals(newEpic.getSubtasks().size(), 0);
    }

    @Test
    void shouldLoadHistory() throws IOException, InterruptedException {
        HttpTaskManager managerForTestLoad = new HttpTaskManager("http://localhost:8078");
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertEquals(historyManager.getHistory().size(), 5);
        assertEquals(historyManager.getHistory().get(0), managerForTestLoad.getListTasks().get(1));
        assertEquals(historyManager.getHistory().get(1), managerForTestLoad.getListEpics().get(2));
        assertEquals(historyManager.getHistory().get(2), managerForTestLoad.getListSubtasks().get(3));
        assertEquals(historyManager.getHistory().get(3), managerForTestLoad.getListSubtasks().get(4));
        assertEquals(historyManager.getHistory().get(4), managerForTestLoad.getListEpics().get(5));
    }

    @Test
    void shouldLoadSorted() throws IOException, InterruptedException {
        HttpTaskManager managerForTestLoad = new HttpTaskManager("http://localhost:8078");
        assertEquals(managerForTestLoad.getSortedList().size(), 3);
        assertEquals(managerForTestLoad.getSortedList().first(), managerForTestLoad.getListTasks().get(1));
        assertTrue(managerForTestLoad.getSortedList().contains(managerForTestLoad.getListSubtasks().get(3)));
        assertTrue(managerForTestLoad.getSortedList().contains(managerForTestLoad.getListSubtasks().get(4)));
    }
}