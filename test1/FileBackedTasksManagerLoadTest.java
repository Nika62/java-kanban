import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;

import static manager.FileBackedTasksManager.*;
import static org.junit.jupiter.api.Assertions.*;
import static tasks.Task.StatusList.*;

class FileBackedTasksManagerLoadTest {
    FileBackedTasksManager fileBackedTasksManager;
    FileBackedTasksManager fileBackedTasksManagerSaveTest;

    @BeforeEach
    void beforeEach() {
        fileBackedTasksManagerSaveTest = new FileBackedTasksManager(new File("test1/resources/test_save.csv"));
        Epic epic = new Epic("epic", "описание эпик");
        Task task = new Task("task", "описание таск", 10, LocalDateTime.parse("2001-01-01T00:00"));
        Subtask subtask = new Subtask(1, "subtask", "описание subtask", 20, LocalDateTime.parse("2001-02-02T02:20"));
        Epic epicWithoutSub = new Epic("epicWithoutSub ", "описание эпик4");
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(epic);
        fileBackedTasksManagerSaveTest.saveSubtask(subtask, epic);
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(task);
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(epicWithoutSub);
        fileBackedTasksManagerSaveTest.getEpicById(4);
        fileBackedTasksManagerSaveTest.getEpicById(1);
        fileBackedTasksManagerSaveTest.getSubtaskById(2);
        fileBackedTasksManagerSaveTest.getTaskById(3);

    }

    @AfterEach
    void afterEach() {
        fileBackedTasksManager.deleteAllTasks();
        fileBackedTasksManager.deleteAllSubtasks();
        fileBackedTasksManager.deleteAllEpics();
        fileBackedTasksManagerSaveTest.deleteAllTasks();
        fileBackedTasksManagerSaveTest.deleteAllSubtasks();
        fileBackedTasksManagerSaveTest.deleteAllEpics();
        fileBackedTasksManager = null;
        fileBackedTasksManagerSaveTest = null;
    }

    @Test
    void shouldLoadTask() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        Task task = fileBackedTasksManager.getListTasks().get(3);
        assertEquals(task.getName(), "task");
        assertEquals(task.getDescription(), "описание таск");
        assertEquals(task.getStartTime(), LocalDateTime.parse("2001-01-01T00:00"));
        assertEquals(task.getStatus(), NEW);
    }

    @Test
    void shouldLoadEpic() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        Epic epicAfterLoad = fileBackedTasksManager.getListEpics().get(1);
        assertEquals(epicAfterLoad.getName(), "epic");
        assertEquals(epicAfterLoad.getDescription(), "описание эпик");
        assertEquals(epicAfterLoad.getStatus(), NEW);
    }


    @Test
    void shouldLoadSubtasksInEpic() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        Epic epicAfterLoad = fileBackedTasksManager.getListEpics().get(1);
        Subtask subtaskAfterLoad = fileBackedTasksManager.getListSubtasks().get(2);
        assertEquals(epicAfterLoad.getSubtasks().size(), 1);
        assertTrue(epicAfterLoad.getSubtasks().contains(subtaskAfterLoad));
        assertEquals(epicAfterLoad.getId(), subtaskAfterLoad.getParentId());
    }

    @Test
    void shouldLoadEpicWithoutSubtasks() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        Epic epicWithoutSubtasksAfterLoad = fileBackedTasksManager.getListEpics().get(4);
        assertEquals(epicWithoutSubtasksAfterLoad.getName().trim(), "epicWithoutSub");
        assertEquals(epicWithoutSubtasksAfterLoad.getDescription(), "описание эпик4");
        assertEquals(epicWithoutSubtasksAfterLoad.getStatus(), NEW);
        assertNull(epicWithoutSubtasksAfterLoad.getSubtasks());
    }

    @Test
    void shouldLoadSubtasks() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        Subtask subtaskAfterLoad = fileBackedTasksManager.getListSubtasks().get(2);
        assertEquals(subtaskAfterLoad.getParentId(), 1);
        assertEquals(subtaskAfterLoad.getName(), "subtask");
        assertEquals(subtaskAfterLoad.getDescription(), "описание subtask");
        assertEquals(subtaskAfterLoad.getStatus(), NEW);
    }

    @Test
    void shouldLoadHistory() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        HistoryManager historyForTest = Managers.getDefaultHistory();
        assertEquals(historyForTest.getHistory().size(), 4);
        assertEquals(historyForTest.getHistory().get(0), fileBackedTasksManager.getListEpics().get(4));
        assertEquals(historyForTest.getHistory().get(1), fileBackedTasksManager.getListEpics().get(1));
        assertEquals(historyForTest.getHistory().get(2), fileBackedTasksManager.getListSubtasks().get(2));
        assertEquals(historyForTest.getHistory().get(3), fileBackedTasksManager.getListTasks().get(3));
    }

    @Test
    void shouldHistoryNotDouble() {
        fileBackedTasksManagerSaveTest.getEpicById(1);
        fileBackedTasksManagerSaveTest.getSubtaskById(2);
        fileBackedTasksManagerSaveTest.getTaskById(3);
        fileBackedTasksManagerSaveTest.getEpicById(4);
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        HistoryManager historyForTest = Managers.getDefaultHistory();
        assertEquals(historyForTest.getHistory().size(), 4);
        assertEquals(historyForTest.getHistory().get(0), fileBackedTasksManager.getListEpics().get(1));
        assertEquals(historyForTest.getHistory().get(1), fileBackedTasksManager.getListSubtasks().get(2));
        assertEquals(historyForTest.getHistory().get(2), fileBackedTasksManager.getListTasks().get(3));
        assertEquals(historyForTest.getHistory().get(3), fileBackedTasksManager.getListEpics().get(4));
    }

    @Test
    void shouldHistoryRemoveFirst() {
        fileBackedTasksManagerSaveTest.deleteEpicById(4);
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        HistoryManager historyForTest = Managers.getDefaultHistory();
        assertEquals(historyForTest.getHistory().size(), 3);
        assertEquals(historyForTest.getHistory().get(0), fileBackedTasksManager.getListEpics().get(1));
        assertEquals(historyForTest.getHistory().get(1), fileBackedTasksManager.getListSubtasks().get(2));
        assertEquals(historyForTest.getHistory().get(2), fileBackedTasksManager.getListTasks().get(3));
    }

    @Test
    void shouldHistoryRemoveFromMiddle() {
        fileBackedTasksManagerSaveTest.deleteSubtaskById(2);
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        HistoryManager historyForTest = Managers.getDefaultHistory();
        assertEquals(historyForTest.getHistory().size(), 3);
        assertEquals(historyForTest.getHistory().get(0), fileBackedTasksManager.getListEpics().get(4));
        assertEquals(historyForTest.getHistory().get(1), fileBackedTasksManager.getListEpics().get(1));
        assertEquals(historyForTest.getHistory().get(2), fileBackedTasksManager.getListTasks().get(3));
    }

    @Test
    void shouldHistoryRemoveEpicWithSubtask() {
        fileBackedTasksManagerSaveTest.deleteEpicById(1);
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        HistoryManager historyForTest = Managers.getDefaultHistory();
        assertEquals(historyForTest.getHistory().size(), 2);
        assertEquals(historyForTest.getHistory().get(0), fileBackedTasksManager.getListEpics().get(4));
        assertEquals(historyForTest.getHistory().get(1), fileBackedTasksManager.getListTasks().get(3));
    }

    @Test
    void shouldHistoryRemoveLast() {
        fileBackedTasksManagerSaveTest.deleteTaskById(3);
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_save.csv"));
        HistoryManager historyForTest = Managers.getDefaultHistory();
        assertEquals(historyForTest.getHistory().size(), 3);
        assertEquals(historyForTest.getHistory().get(0), fileBackedTasksManager.getListEpics().get(4));
        assertEquals(historyForTest.getHistory().get(1), fileBackedTasksManager.getListEpics().get(1));
        assertEquals(historyForTest.getHistory().get(2), fileBackedTasksManager.getListSubtasks().get(2));

    }
}

