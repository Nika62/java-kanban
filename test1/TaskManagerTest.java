import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Task.StatusList.*;

class TaskManagerTest<T extends TaskManager> {

    TaskManager m = new InMemoryTaskManager();
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

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
        assertEquals(m.getListAllTasks().get(0), task);
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
        assertEquals(epics.get(0), epic);

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
        assertEquals(subtasks.get(0), subtask);
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
        assertEquals(epic.getStartTime(), subtask.getStartTime());
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
        assertTrue(m.getListAllSubtasks().contains(newSub));
        assertEquals(m.getListAllSubtasks().size(), 1);
        assertNotEquals(subtask, newSub);
        assertEquals(epic.getSubtasks().size(), 1);
        assertEquals(epic.getSubtasks().get(0), newSub);
    }

    @Test
    void shouldUpdateTask() {
        m.saveTaskAndEpic(task);
        assertTrue(m.getListAllTasks().contains(task));
        Task newTask = new Task(1, "newTask", "YYYYYYYYY", IN_PROGRESS, LocalDateTime.now(), 78, LocalDateTime.now().plusMinutes(78));
        m.updateTask(newTask);
        assertEquals(m.getListAllTasks().size(), 1);
        assertNotEquals(task, newTask);
        assertTrue(m.getListAllTasks().contains(newTask));
    }

    @Test
    void shouldUpdateEpic() {
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        assertTrue(m.getListAllEpic().contains(epic));
        ArrayList<Subtask> l = new ArrayList<>();
        l.add(subtask);
        Epic newEpic = new Epic(1, "newEpic", "mYmYmY", DONE, LocalDateTime.now(), 78, LocalDateTime.now().plusMinutes(78), l);
        m.updateEpic(newEpic);
        assertEquals(m.getListAllEpic().size(), 1);
        assertNotEquals(epic, newEpic);
        assertTrue(m.getListAllEpic().contains(newEpic));
        assertEquals(m.getEpicById(subtask.getParentId()), newEpic);
    }

    @Test
    void shouldUpdateEpicTaskSubtaskNull() {
        Epic epicN = null;
        Subtask subN = null;
        Task taskN = null;
        m.updateEpic(epicN);
        m.updateSubtask(subN);
        m.updateTask(taskN);
    }

    @Test
    void shouldDeleteTaskById() {
        HistoryManager history = Managers.getDefaultHistory();
        m.saveTaskAndEpic(task);
        assertTrue(m.getListAllTasks().contains(task));
        m.getTaskById(task.getId());
        assertTrue(history.getHistory().contains(task));
        m.deleteTaskById(task.getId());
        assertFalse(m.getListAllTasks().contains(task));
        assertFalse(history.getHistory().contains(task));

    }

    @Test
    void shouldDeleteEpicById() {
        HistoryManager history = Managers.getDefaultHistory();
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        m.getEpicById(epic.getId());
        assertTrue(m.getListAllEpic().contains(epic));
        assertTrue(history.getHistory().contains(epic));
        assertTrue(m.getListAllSubtasks().contains(subtask));
        m.deleteEpicById(epic.getId());
        assertFalse(m.getListAllEpic().contains(epic));
        assertFalse(history.getHistory().contains(epic));
        assertFalse(m.getListAllSubtasks().contains(subtask));
        assertFalse(history.getHistory().contains(subtask));
    }

    @Test
    void shouldDeleteSubtaskById() {
        HistoryManager history = Managers.getDefaultHistory();
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        m.getSubtaskById(subtask.getId());
        assertTrue(m.getListAllSubtasks().contains(subtask));
        assertTrue(history.getHistory().contains(subtask));
        assertTrue(epic.getSubtasks().contains(subtask));
        m.deleteSubtaskById(subtask.getId());
        assertFalse(m.getListAllSubtasks().contains(subtask));
        assertFalse(history.getHistory().contains(subtask));
        assertFalse(epic.getSubtasks().contains(subtask));
    }

    @Test
    void shouldDeleteEpicTaskSubtaskByWrongId() {
        m.deleteSubtaskById(8);
        m.deleteEpicById(8);
        m.deleteTaskById(8);
    }

    @Test
    void shouldGetListSubtasksOfEpic() {
        Subtask sub2 = new Subtask(1, "SUB2", "IUY7UYHUHUH", 19, LocalDateTime.of(2023, 02, 01, 0, 50));
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        m.saveSubtask(sub2, epic);
        assertEquals(epic.getSubtasks().size(), 2);
        assertEquals(epic.getSubtasks().get(0), subtask);
        assertEquals(epic.getSubtasks().get(1), sub2);
    }

    @Test
    void shouldDefineStatusEpic() {
        Subtask sub2 = new Subtask(1, "SUB2", "IUY7UYHUHUH", DONE, LocalDateTime.of(2023, 02, 01, 0, 50), 19, LocalDateTime.of(2023, 02, 01, 0, 50).plusMinutes(19), 3);
        m.saveTaskAndEpic(epic);
        assertEquals(epic.getStatus(), NEW);
        subtask.setStatus(DONE);
        m.saveSubtask(subtask, epic);
        assertEquals(epic.getStatus(), DONE);
        sub2.setStatus(IN_PROGRESS);
        m.saveSubtask(sub2, epic);
        assertEquals(epic.getStatus(), IN_PROGRESS);
        m.deleteAllSubtasks();
        assertEquals(epic.getStatus(), NEW);
    }

    @Test
    void shouldGetPrioritizedTasks() {
        Subtask sub2 = new Subtask(1, "SUB2", "IUY7UYHUHUH", 19, null);
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        m.saveSubtask(sub2, epic);
        m.saveTaskAndEpic(task);
        System.out.println(m.getPrioritizedTasks());
        assertEquals(m.getPrioritizedTasks().get(0), task);
        assertEquals(m.getPrioritizedTasks().get(1), subtask);
        assertEquals(m.getPrioritizedTasks().get(2), sub2);
    }
}
