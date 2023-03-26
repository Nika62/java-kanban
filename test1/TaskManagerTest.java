import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Task.StatusList.*;

class TaskManagerTest<T extends TaskManager> {

    TaskManager m = new InMemoryTaskManager();
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        task = new Task("test", "test", 30, LocalDateTime.of(2023, 01, 01, 0, 1));
        epic = new Epic("EPIC", "description EPIC");
        subtask = new Subtask(3, "SUBTASK", "descr SUBTASK", 20, LocalDateTime.of(2023, 01, 01, 0, 50));
    }

    @AfterEach
    void tearDown() {
        m.deleteAllTasks();
        m.deleteAllEpics();
        m.deleteAllSubtasks();
    }

    @Test
    void shouldSaveTaskInTasksList() {
        m.saveTaskAndEpic(task);
        ArrayList<Task> tasks = m.getListAllTasks();
        assertNotNull(tasks);
        assertEquals(tasks.get(0).getId(), 1);
        assertEquals(tasks.get(0).getName(), "test");
        assertEquals(tasks.get(0).getDuration(), 30);
        assertEquals(tasks.get(0).getStartTime(), LocalDateTime.parse("2023-01-01T00:01"));
        assertEquals(tasks.get(0).getEndTime(), LocalDateTime.parse("2023-01-01T00:01").plusMinutes(30));
    }

    @Test
    void shouldSaveTaskNullDontSave() {
        Task taskNull = null;
        m.saveTaskAndEpic(taskNull);
        assertTrue(m.getListAllSubtasks().isEmpty());

    }

    @Test
    void shouldSaveEpicInEpicList() {
        m.saveTaskAndEpic(epic);
        ArrayList<Epic> epics = m.getListAllEpic();
        assertNotNull(epics);
        assertEquals(epics.get(0).getId(), 1);
        assertEquals(epics.get(0).getName(), "EPIC");
        assertEquals(epics.get(0).getDescription(), "description EPIC");
        assertEquals(epics.get(0).getDuration(), 0);
        assertNull(epics.get(0).getStartTime());
        assertNull(epics.get(0).getEndTime());
    }

    @Test
    void shouldSaveEpicNullDontSave() {
        Epic epicNull = null;
        m.saveTaskAndEpic(epicNull);
        assertTrue(m.getListAllEpic().isEmpty());
    }

    @Test
    void shouldSaveSubtaskInListSubtasks() {
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        ArrayList<Subtask> subtasks = m.getListAllSubtasks();
        assertNotNull(subtasks);
        assertEquals(subtasks.get(0).getParentId(), 3);
        assertEquals(subtasks.get(0).getId(), 2);
        assertEquals(subtasks.get(0).getName(), "SUBTASK");
        assertEquals(subtasks.get(0).getDescription(), "descr SUBTASK");
        assertEquals(subtasks.get(0).getDuration(), 20);
        assertEquals(subtasks.get(0).getStartTime(), LocalDateTime.parse("2023-01-01T00:50"));
        assertEquals(subtasks.get(0).getEndTime(), LocalDateTime.parse("2023-01-01T00:50").plusMinutes(20));
    }

    @Test
    void shouldSaveSubtaskNullDontSave() {
        Subtask subNull = null;
        m.saveTaskAndEpic(subNull);
        assertTrue(m.getListAllEpic().isEmpty());
    }

    @Test
    void shouldSaveSubtaskInEpic() {
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        Epic epic = (Epic) m.getEpicById(1);
        Subtask subtask = (Subtask) m.getSubtaskById(2);
        assertNotNull(epic.getSubtasks());
        assertEquals(epic.getSubtasks().get(0), subtask);
        assertEquals(epic.getStartTime(), LocalDateTime.parse("2023-01-01T00:50"));
        assertEquals(epic.getEndTime(), LocalDateTime.parse("2023-01-01T00:50").plusMinutes(20));
        assertEquals(epic.getDuration(), 20);
    }

    @Test
    void shouldDeleteAllEpics() {
        m.saveTaskAndEpic(epic);
        assertFalse(m.getListAllEpic().isEmpty());
        m.deleteAllEpics();
        assertTrue(m.getListAllEpic().isEmpty());
        assertTrue(m.getListAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteAllTasks() {
        m.saveTaskAndEpic(task);
        assertFalse(m.getListAllTasks().isEmpty());
        m.deleteAllTasks();
        assertTrue(m.getListAllTasks().isEmpty());
    }

    @Test
    void shouldDeleteAllSubtasks() {
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        assertFalse(m.getListAllSubtasks().isEmpty());
        m.deleteAllSubtasks();
        assertTrue(m.getListAllSubtasks().isEmpty());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteEmptyListAllEpicsTasksSubtasks() {
        assertTrue(m.getListAllEpic().isEmpty());
        assertTrue(m.getListAllTasks().isEmpty());
        assertTrue(m.getListAllSubtasks().isEmpty());
        m.deleteAllSubtasks();
        m.deleteAllEpics();
        m.deleteAllTasks();
    }

    @Test
    void shouldGetListAllEpic() {
        m.saveTaskAndEpic(epic);
        ArrayList<Epic> epics = m.getListAllEpic();
        assertFalse(epics.isEmpty());
        assertEquals(epics.size(), 1);
        assertEquals(epics.get(0), epic);
    }

    @Test
    void shouldGetListAllTasks() {
        m.saveTaskAndEpic(task);
        ArrayList<Task> tasks = m.getListAllTasks();
        assertFalse(tasks.isEmpty());
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0), task);
    }

    @Test
    void shouldGetListAllSubtasks() {
        m.saveSubtask(subtask, epic);
        ArrayList<Subtask> subtasks = m.getListAllSubtasks();
        assertFalse(subtasks.isEmpty());
        assertEquals(subtasks.size(), 1);
        assertEquals(subtasks.get(0), subtask);
    }

    @Test
    void shouldGetEmptyListAllEpicsTasksSubtasks() {
        assertEquals(m.getListAllEpic().size(), 0);
        assertEquals(m.getListAllSubtasks().size(), 0);
        assertEquals(m.getListAllSubtasks().size(), 0);
    }

    @Test
    void shouldGetTaskById() {
        m.saveTaskAndEpic(task);
        assertEquals(task.getId(), 1);
        assertEquals(m.getTaskById(1), task);
    }

    @Test
    void shouldGetEpicById() {
        m.saveTaskAndEpic(epic);
        assertEquals(epic.getId(), 1);
        assertEquals(m.getEpicById(1), epic);
    }

    @Test
    void shouldGetSubtaskById() {
        m.saveSubtask(subtask, epic);
        assertEquals(subtask.getId(), 1);
        assertEquals(m.getSubtaskById(1), subtask);
    }

    @Test
    void shouldGetEpicTaskSubtaskByWrongId() {
        m.saveTaskAndEpic(task);
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        assertEquals(m.getListAllEpic().size(), 1);
        assertEquals(m.getListAllSubtasks().size(), 1);
        assertEquals(m.getListAllSubtasks().size(), 1);
        assertNull(m.getTaskById(5));
        assertNull(m.getSubtaskById(5));
        assertNull(m.getEpicById(5));
    }

    @Test
    void shouldUpdateSubtask() {
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        assertTrue(m.getListAllSubtasks().contains(subtask));
        Subtask newSub = new Subtask(2, "newSub", "iiiiii", DONE, LocalDateTime.now(), 5, LocalDateTime.now().plusMinutes(5), 1);
        m.updateSubtask(newSub);
        assertTrue(m.getListAllSubtasks().contains(m.getSubtaskById(2)));
        assertEquals(m.getListAllSubtasks().size(), 1);
        var t = (Subtask) m.getListAllSubtasks().get(0);
        System.out.println(t);
        System.out.println(m.getListAllSubtasks().get(0).getClass());
    }
}
