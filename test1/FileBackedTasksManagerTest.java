import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;

import static manager.FileBackedTasksManager.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    FileBackedTasksManager managerFail;
    File file;
    Epic epicF;
    Task taskF;
    Subtask subtaskF;

    @BeforeEach
    void beforeAll() {
        taskF = new Task("task", "test", 30, LocalDateTime.of(2023, 01, 01, 0, 1));
        epicF = new Epic("EPIC", "description EPIC");
        subtaskF = new Subtask(2, "SUBTASK", "descr SUBTASK", 20, LocalDateTime.of(2023, 8, 01, 02, 50));
        managerFail = new FileBackedTasksManager(new File("./testFileBackedM.csv"));
        managerFail.saveTaskAndEpic(taskF);
        managerFail.saveTaskAndEpic(epicF);
        managerFail.saveSubtask(subtaskF, epicF);

    }

    @AfterEach
    void tearDown() {
        managerFail.deleteAllTasks();
        managerFail.deleteAllSubtasks();
        managerFail.deleteAllEpics();
    }


    @Test
    void shouldHistorySave() {
        managerFail.getTaskById(taskF.getId());
        managerFail.getEpicById(epicF.getId());
        managerFail.getSubtaskById(subtaskF.getId());
        managerFail.getTaskById(taskF.getId());
        System.out.println(historyManager.getHistory());
        assertTrue(historyManager.getHistory().contains(epicF));
        assertTrue(historyManager.getHistory().contains(taskF));
        assertTrue(historyManager.getHistory().contains(subtaskF));
    }

    @Test
    void shouldSaveEpicWithoutSubtasks() {
        Epic epicWithoutSub = new Epic("epicWithoutSub", "jhkjgugtuti7ti7t7t");
        managerFail.saveTaskAndEpic(epicWithoutSub);
        managerFail.getEpicById(epicWithoutSub.getId());
        assertTrue(epicWithoutSub.getSubtasks().isEmpty());
    }

    @Test
    void shouldSaveTaskEpicSubtaskHistoryInFileAndLoad() {

        FileBackedTasksManager fileBackedTasksManager = loadFromFile(Path.of("./testFileBackedM.csv"));
        Epic epicWithoutSub = fileBackedTasksManager.getListAllEpic().get(1);
        assertFalse(fileBackedTasksManager.getListAllEpic().isEmpty());
        assertFalse(fileBackedTasksManager.getListAllSubtasks().isEmpty());
        assertFalse(fileBackedTasksManager.getListAllTasks().isEmpty());
        assertEquals(Managers.getDefaultHistory().getHistory().size(), 4);
        assertEquals(Managers.getDefaultHistory().getHistory().get(0).getName(), "epicWithoutSub");
        assertEquals(Managers.getDefaultHistory().getHistory().get(1).getName(), "EPIC");
        assertEquals(Managers.getDefaultHistory().getHistory().get(2).getName(), "SUBTASK");
        assertEquals(Managers.getDefaultHistory().getHistory().get(3).getName(), "task");
        assertNull(epicWithoutSub.getSubtasks());
    }
}

