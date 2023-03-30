import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.nio.file.*;

import static manager.FileBackedTasksManager.*;
import static org.junit.jupiter.api.Assertions.*;
import static tasks.Task.StatusList.*;

class FileBackedTasksManagerLoadTest {

    FileBackedTasksManager fileBackedTasksManager;

    @Test
    void shouldLoadTask() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_task.csv"));
        Task task = fileBackedTasksManager.getTaskById(1);
        assertEquals(task.getName(), "task");
        assertEquals(task.getDescription(), "описание 1");
        assertEquals(task.getStatus(), NEW);
    }

    @Test
    void shouldLoadEpic() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_epic.csv"));
        Epic epic = fileBackedTasksManager.getEpicById(2);
        assertEquals(epic.getName(), "epic");
        assertEquals(epic.getDescription(), "описание 2");
        assertEquals(epic.getStatus(), IN_PROGRESS);
    }

    @Test
    void shouldLoadSubtasksInEpic() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_loadSubtasksInEpic.csv"));
        Epic epic = fileBackedTasksManager.getEpicById(2);
        Subtask subtask = fileBackedTasksManager.getSubtaskById(3);
        assertEquals(epic.getSubtasks().size(), 1);
        assertTrue(epic.getSubtasks().contains(subtask));
    }

    @Test
    void shouldLoadEpicWithoutSubtasks() {

        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_epicWithoutSub.csv"));
        Epic epicWithoutSubtasks = fileBackedTasksManager.getEpicById(4);
        assertEquals(epicWithoutSubtasks.getName(), "epicWithoutSubtasks");
        assertEquals(epicWithoutSubtasks.getDescription(), "описание 4");
        assertEquals(epicWithoutSubtasks.getStatus(), DONE);
        assertNull(epicWithoutSubtasks.getSubtasks());
    }

    @Test
    void shouldLoadSubtasks() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_subtask.csv"));
        Subtask subtask = fileBackedTasksManager.getSubtaskById(3);
        assertEquals(subtask.getParentId(), 2);
        assertEquals(subtask.getName(), "subtask");
        assertEquals(subtask.getDescription(), "ОПИСАНИЕ3");
        assertEquals(subtask.getStatus(), IN_PROGRESS);
    }

    @Test
    void shouldLoadHistory() {
        fileBackedTasksManager = loadFromFile(Path.of("test1/resources/test_history.csv"));
        HistoryManager historyForTest = Managers.getDefaultHistory();
        assertEquals(historyForTest.getHistory().size(), 4);
        assertEquals(historyForTest.getHistory().get(0), fileBackedTasksManager.getListEpics().get(4));
        assertEquals(historyForTest.getHistory().get(1), fileBackedTasksManager.getListTasks().get(1));
        assertEquals(historyForTest.getHistory().get(2), fileBackedTasksManager.getListEpics().get(2));
        assertEquals(historyForTest.getHistory().get(3), fileBackedTasksManager.getListSubtasks().get(3));
    }
}


