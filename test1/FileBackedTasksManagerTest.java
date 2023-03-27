import manager.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static manager.FileBackedTasksManager.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    FileBackedTasksManager f;
    File file;

    @BeforeEach
    void beforeEach() {
        super.beforeEach();
        f = new FileBackedTasksManager(new File("./testFileBackedM.csv"));
    }

    @Test
    void shouldSaveTaskEpicSubtaskHistoryInFileAndLoad() {
        f.saveTaskAndEpic(epic);
        f.saveSubtask(subtask, epic);
        f.saveTaskAndEpic(task);
        f.getTaskById(task.getId());
        f.getEpicById(epic.getId());
        f.getSubtaskById(subtask.getId());
        f.getTaskById(task.getId());
        loadFromFile(Path.of("./testFileBackedM.csv").toFile());
        assertEquals(f.getListAllTasks().size(), 1);
        assertEquals(f.getListAllTasks().get(0), task);
        assertEquals(f.getListAllEpic().size(), 1);
        assertEquals(f.getListAllEpic().get(0), epic);
        assertEquals(f.getListAllSubtasks().size(), 1);
        assertEquals(f.getListAllSubtasks().get(0), subtask);
        assertEquals(Managers.getDefaultHistory().getHistory().size(), 3);
        assertEquals(Managers.getDefaultHistory().getHistory().get(0), epic);
        assertEquals(Managers.getDefaultHistory().getHistory().get(1), subtask);
        assertEquals(Managers.getDefaultHistory().getHistory().get(2), task);


    }
}

