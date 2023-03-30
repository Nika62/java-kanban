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
    protected Subtask sub2;

    @BeforeEach
    void beforeEach() {
        task = new Task("test", "test", 30, LocalDateTime.of(2023, 01, 01, 0, 1));
        epic = new Epic("EPIC", "description EPIC");
        subtask = new Subtask(2, "SUBTASK", "descr SUBTASK", 20, LocalDateTime.of(2023, 8, 01, 02, 50));
        sub2 = new Subtask(2, "SUB2", "IUY7UYHUHUH", 19, null);
        m.saveTaskAndEpic(task);
        m.saveTaskAndEpic(epic);
        m.saveSubtask(subtask, epic);
        m.saveSubtask(sub2, epic);

    }

    @AfterEach
    void tearDown() {
        m.deleteAllTasks();
        m.deleteAllSubtasks();
        m.deleteAllEpics();
    }

    @Test
    void shouldSaveTaskInTasksList() {
        ArrayList<Task> tasks = m.getListAllTasks();
        assertNotNull(tasks);
        assertEquals(m.getListAllTasks().get(0), task);
    }

    @Test
    void shouldSaveTaskNullDontSave() {
        Task taskNull = null;
        m.saveTaskAndEpic(taskNull);
        assertFalse(m.getListAllTasks().contains(taskNull));

    }

    @Test
    void shouldSaveEpicInEpicList() {
        ArrayList<Epic> epics = m.getListAllEpic();
        assertNotNull(epics);
        assertEquals(epics.get(0), epic);

    }

    @Test
    void shouldSaveEpicNullDontSave() {
        Epic epicNull = null;
        m.saveTaskAndEpic(epicNull);
        assertFalse(m.getListAllEpic().contains(epicNull));
    }

    @Test
    void shouldSaveSubtaskInListSubtasks() {
        ArrayList<Subtask> subtasks = m.getListAllSubtasks();
        assertNotNull(subtasks);
        assertEquals(subtasks.get(0), subtask);
    }

    @Test
    void shouldSaveSubtaskNullDontSave() {
        Subtask subNull = null;
        m.saveSubtask(subNull, epic);
        assertFalse(m.getListAllSubtasks().contains(subNull));
    }

    @Test
    void shouldEpicInSubtask() {
        assertEquals(subtask.getParentId(), epic.getId());
    }

    @Test
    void shouldEpicWrongIdInSubtask() {
        Epic epicWrong = new Epic("EPICw", "description EPIC");
        Subtask sub3 = new Subtask(6, "UUUYYUYU", "UYUYUYY7Y7");
        assertNotEquals(subtask.getParentId(), epicWrong.getId());
        m.saveSubtask(sub3, epicWrong);
        assertFalse(epicWrong.getSubtasks().contains(sub3));
    }

    @Test
    void shouldDeleteAllEpics() {
        assertFalse(m.getListAllEpic().isEmpty());
        m.deleteAllEpics();
        assertTrue(m.getListAllEpic().isEmpty());
        assertTrue(m.getListAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteAllTasks() {
        assertFalse(m.getListAllTasks().isEmpty());
        m.deleteAllTasks();
        assertTrue(m.getListAllTasks().isEmpty());
    }

    @Test
    void shouldDeleteAllSubtasks() {
        assertFalse(m.getListAllSubtasks().isEmpty());
        m.deleteAllSubtasks();
        assertTrue(m.getListAllSubtasks().isEmpty());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteEmptyListAllEpicsTasksSubtasks() {
        m.deleteAllSubtasks();
        m.deleteAllEpics();
        m.deleteAllTasks();
        assertTrue(m.getListAllEpic().isEmpty());
        assertTrue(m.getListAllTasks().isEmpty());
        assertTrue(m.getListAllSubtasks().isEmpty());
        m.deleteAllSubtasks();
        m.deleteAllEpics();
        m.deleteAllTasks();
    }

    @Test
    void shouldGetListAllEpic() {
        ArrayList<Epic> epics = m.getListAllEpic();
        assertFalse(epics.isEmpty());
        assertEquals(epics.size(), 1);
        assertEquals(epics.get(0), epic);
    }

    @Test
    void shouldGetListAllTasks() {
        ArrayList<Task> tasks = m.getListAllTasks();
        assertFalse(tasks.isEmpty());
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0), task);
    }

    @Test
    void shouldGetListAllSubtasks() {
        ArrayList<Subtask> subtasks = m.getListAllSubtasks();
        assertFalse(subtasks.isEmpty());
        assertEquals(subtasks.get(0), subtask);
    }

    @Test
    void shouldGetEmptyListAllEpicsTasksSubtasks() {
        m.deleteAllEpics();
        m.deleteAllTasks();
        m.deleteAllSubtasks();
        assertEquals(m.getListAllEpic().size(), 0);
        assertEquals(m.getListAllSubtasks().size(), 0);
        assertEquals(m.getListAllSubtasks().size(), 0);
    }

    @Test
    void shouldGetTaskById() {
        assertEquals(task.getId(), 1);
        assertEquals(m.getTaskById(1), task);
    }

    @Test
    void shouldGetEpicById() {
        assertEquals(epic.getId(), 2);
        assertEquals(m.getEpicById(2), epic);
    }

    @Test
    void shouldGetSubtaskById() {
        assertEquals(subtask.getId(), 3);
        assertEquals(m.getSubtaskById(3), subtask);
    }

    @Test
    void shouldGetEpicTaskSubtaskByWrongId() {

        assertEquals(m.getListAllEpic().size(), 1);
        assertEquals(m.getListAllSubtasks().size(), 2);
        assertEquals(m.getListAllEpic().size(), 1);
        assertNull(m.getTaskById(5));
        assertNull(m.getSubtaskById(5));
        assertNull(m.getEpicById(5));
    }

    @Test
    void shouldUpdateSubtask() {
        assertTrue(m.getListAllSubtasks().contains(subtask));
        subtask = new Subtask(3, "newSub", "iiiiii", DONE, LocalDateTime.now(), 5, LocalDateTime.now().plusMinutes(5), 2);
        m.updateSubtask(subtask);
        assertEquals(epic.getSubtasks().get(0).getName(), "newSub");
        assertEquals(epic.getSubtasks().get(0).getName(), "newSub");
    }

    @Test
    void shouldUpdateTask() {
        assertTrue(m.getListAllTasks().contains(task));
        Task newTask = new Task(1, "newTask", "YYYYYYYYY", IN_PROGRESS, LocalDateTime.now(), 78, LocalDateTime.now().plusMinutes(78));
        m.updateTask(newTask);
        assertEquals(m.getListAllTasks().size(), 1);
        assertNotEquals(task, newTask);
        assertTrue(m.getListAllTasks().contains(newTask));
    }

    @Test
    void shouldUpdateEpic() {
        assertTrue(m.getListAllEpic().contains(epic));
        ArrayList<Subtask> l = new ArrayList<>();
        l.add(subtask);
        Epic newEpic = new Epic(2, "newEpic", "mYmYmY", DONE, LocalDateTime.now(), 78, LocalDateTime.now().plusMinutes(78), l);
        m.updateEpic(newEpic);
        assertEquals(m.getListAllEpic().size(), 1);
        assertNotEquals(epic, newEpic);
        assertTrue(m.getListAllEpic().contains(newEpic));
        assertEquals(m.getEpicById(subtask.getParentId()), newEpic);
    }

    @Test
    void shouldUpdateEpicTaskSubtaskNull() {
        epic = null;
        task = null;
        subtask = null;
        m.updateEpic(epic);
        m.updateSubtask(subtask);
        m.updateTask(task);
        assertNotNull(m.getListAllEpic().get(0));
        assertNotNull(m.getListAllTasks().get(0));
        assertNotNull(m.getListAllSubtasks().get(0));
    }

    @Test
    void shouldDeleteTaskById() {
        HistoryManager history = Managers.getDefaultHistory();
        assertTrue(m.getListAllTasks().contains(task));
        m.getTaskById(task.getId());
        assertTrue(history.getHistory().contains(task));
        m.deleteTaskById(1);
        assertFalse(m.getListAllTasks().contains(task));
        assertFalse(history.getHistory().contains(task));

    }

    @Test
    void shouldDeleteEpicById() {
        HistoryManager history = Managers.getDefaultHistory();
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
        assertEquals(epic.getSubtasks().size(), 2);
        assertEquals(epic.getSubtasks().get(0), subtask);
        assertEquals(epic.getSubtasks().get(1), sub2);
    }

    void shouldGetEmptyListSubtasksOfEpic() {
        m.deleteAllSubtasks();
        assertEquals(epic.getSubtasks().size(), 0);
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldDefineStatusEpic() {
        Epic epicWithoutSubtasks = new Epic("epicW", "EPIC without sub");
        ;
        assertEquals(epicWithoutSubtasks.getStatus(), NEW);

        subtask.setStatus(IN_PROGRESS);
        m.updateEpic(epic);
        assertEquals(epic.getStatus(), IN_PROGRESS);

        subtask.setStatus(NEW);
        m.updateSubtask(subtask);
        assertEquals(epic.getStatus(), NEW);

        subtask.setStatus(DONE);
        sub2.setStatus(DONE);
        m.updateEpic(epic);
        assertEquals(epic.getStatus(), DONE);

        subtask.setStatus(NEW);
        m.updateSubtask(subtask);
        assertEquals(epic.getStatus(), IN_PROGRESS);


    }
    @Test
    void shouldGetPrioritizedTasks() {
        assertEquals(m.getPrioritizedTasks().get(0), task);
        assertEquals(m.getPrioritizedTasks().get(1), subtask);
        assertEquals(m.getPrioritizedTasks().get(2), sub2);
    }

    @Test
    void shouldEmptyGetPrioritizedTasks() {
        m.deleteAllTasks();
        m.deleteAllSubtasks();
        assertEquals(m.getPrioritizedTasks().size(), 0);
    }

}
