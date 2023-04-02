import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Task.StatusList.*;

class TaskManagerTest<T extends TaskManager> {

  private TaskManager managerForTest = new InMemoryTaskManager();
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected Subtask sub2;
    protected Epic epicWithoutSub;

    @BeforeEach
    void beforeEach() {
        task = new Task("test", "test", 30, LocalDateTime.of(2023, 01, 01, 0, 1));
        epic = new Epic("EPIC", "description EPIC");
        subtask = new Subtask(2, "SUBTASK", "descr SUBTASK", 20, LocalDateTime.of(2023, 8, 01, 02, 50));
        sub2 = new Subtask(2, "SUB2", "IUY7UYHUHUH", 0, null);
        epicWithoutSub = new Epic("epicWithoutSub ", "description EPIC");

        managerForTest.saveTaskAndEpic(task);
        managerForTest.saveTaskAndEpic(epic);
        managerForTest.saveSubtask(subtask, epic);
        managerForTest.saveSubtask(sub2, epic);
        managerForTest.saveTaskAndEpic(epicWithoutSub);

        managerForTest.getTaskById(1);
        managerForTest.getEpicById(2);
        managerForTest.getSubtaskById(3);
        managerForTest.getSubtaskById(4);
        managerForTest.getEpicById(5);
    }

    @AfterEach
    void afterEach() {
        managerForTest.deleteAllSubtasks();
        managerForTest.deleteAllTasks();
        managerForTest.deleteAllEpics();
        managerForTest = null;
    }

    @Test
    void shouldSaveTaskInTasksList() {
        assertTrue(managerForTest.getListAllTasks().contains(task));
        assertEquals(managerForTest.getListAllTasks().get(0), task);
    }

    @Test
    void shouldSaveTaskNullDontSave() {
        Task taskNull = null;
        managerForTest.saveTaskAndEpic(taskNull);
        assertFalse(managerForTest.getListAllTasks().contains(taskNull));

    }

    @Test
    void shouldSaveEpicInEpicList() {
        assertTrue(managerForTest.getListAllEpic().contains(epic));
        assertEquals(managerForTest.getListAllEpic().get(0), epic);

    }

    @Test
    void shouldSaveEpicNullDontSave() {
        Epic epicNull = null;
        managerForTest.saveTaskAndEpic(epicNull);
        assertFalse(managerForTest.getListAllEpic().contains(epicNull));
    }

    @Test
    void shouldSaveSubtaskInListSubtasks() {
        assertEquals(managerForTest.getListAllSubtasks().size(), 2);
        assertTrue(managerForTest.getListAllSubtasks().contains(subtask));
        assertTrue(managerForTest.getListAllSubtasks().contains(sub2));
    }

    @Test
    void shouldSaveSubtaskNullNotSave() {
        Subtask subNull = null;
        managerForTest.saveSubtask(subNull, epic);
        assertFalse(managerForTest.getListAllSubtasks().contains(subNull));
    }

    @Test
    void shouldEpicInSubtask() {
        assertEquals(subtask.getParentId(), epic.getId());
        assertEquals(sub2.getParentId(), epic.getId());
    }

    @Test
    void shouldSubtaskWidWrongParentIdNotSaveInEpic() {
        Subtask sub3 = new Subtask(6, "UUUYYUYU", "UYUYUYY7Y7");
        managerForTest.saveSubtask(sub3, epic);
        assertFalse(epic.getSubtasks().contains(sub3));
    }

    @Test
    void shouldDeleteAllEpics() {
        managerForTest.deleteAllEpics();
        assertTrue(managerForTest.getListAllEpic().isEmpty());
        assertTrue(managerForTest.getListAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteAllTasks() {
        managerForTest.deleteAllTasks();
        assertTrue(managerForTest.getListAllTasks().isEmpty());
    }

    @Test
    void shouldDeleteAllSubtasks() {
        managerForTest.deleteAllSubtasks();
        assertTrue(managerForTest.getListAllSubtasks().isEmpty());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteEmptyListAllEpicsTasksSubtasks() {
        managerForTest.deleteAllSubtasks();
        managerForTest.deleteAllEpics();
        managerForTest.deleteAllTasks();
        assertTrue(managerForTest.getListAllEpic().isEmpty());
        assertTrue(managerForTest.getListAllTasks().isEmpty());
        assertTrue(managerForTest.getListAllSubtasks().isEmpty());
        managerForTest.deleteAllSubtasks();
        managerForTest.deleteAllEpics();
        managerForTest.deleteAllTasks();
    }

    @Test
    void shouldGetListAllEpic() {
        ArrayList<Epic> epics = managerForTest.getListAllEpic();
        assertFalse(epics.isEmpty());
        assertEquals(epics.size(), 2);
        assertEquals(epics.get(0), epic);
        assertEquals(epics.get(1), epicWithoutSub);
    }

    @Test
    void shouldGetListAllTasks() {
        ArrayList<Task> tasks = managerForTest.getListAllTasks();
        assertFalse(tasks.isEmpty());
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0), task);
    }

    @Test
    void shouldGetListAllSubtasks() {
        ArrayList<Subtask> subtasks = managerForTest.getListAllSubtasks();
        assertFalse(subtasks.isEmpty());
        assertEquals(subtasks.get(0), subtask);
    }

    @Test
    void shouldGetEmptyListAllEpicsTasksSubtasks() {
        managerForTest.deleteAllEpics();
        managerForTest.deleteAllTasks();
        managerForTest.deleteAllSubtasks();
        assertEquals(managerForTest.getListAllEpic().size(), 0);
        assertEquals(managerForTest.getListAllSubtasks().size(), 0);
        assertEquals(managerForTest.getListAllSubtasks().size(), 0);
    }

    @Test
    void shouldGetTaskById() {
        assertEquals(managerForTest.getTaskById(1), task);
    }

    @Test
    void shouldGetEpicById() {
        assertEquals(managerForTest.getEpicById(2), epic);
    }

    @Test
    void shouldGetSubtaskById() {
        assertEquals(managerForTest.getSubtaskById(3), subtask);
    }

    @Test
    void shouldGetEpicTaskSubtaskByWrongId() {

        assertEquals(managerForTest.getListAllTasks().size(), 1);
        assertEquals(managerForTest.getListAllSubtasks().size(), 2);
        assertEquals(managerForTest.getListAllEpic().size(), 2);
        assertNull(managerForTest.getTaskById(8));
        assertNull(managerForTest.getSubtaskById(8));
        assertNull(managerForTest.getEpicById(8));
    }

    @Test
    void shouldUpdateSubtask() {
        assertTrue(managerForTest.getListAllSubtasks().contains(subtask));
        subtask = new Subtask(3, "newSub", "iiiiii", DONE, LocalDateTime.now(), 5, LocalDateTime.now().plusMinutes(5), 2);
        managerForTest.updateSubtask(subtask);
        assertEquals(epic.getSubtasks().get(0).getName(), "newSub");
        assertEquals(epic.getSubtasks().get(0).getStatus(), DONE);
    }

    @Test
    void shouldUpdateTask() {
        assertTrue(managerForTest.getListAllTasks().contains(task));
        Task newTask = new Task(1, "newTask", "YYYYYYYYY", IN_PROGRESS, LocalDateTime.now(), 78, LocalDateTime.now().plusMinutes(78));
        managerForTest.updateTask(newTask);
        assertFalse(managerForTest.getListAllTasks().contains(task));
        assertEquals(managerForTest.getListAllTasks().get(0), newTask);
    }

    @Test
    void shouldUpdateEpic() {
        ArrayList<Subtask> list = new ArrayList<>();
        list.add(subtask);
        Epic newEpic = new Epic(2, "newEpic", "mYmYmY", DONE, LocalDateTime.now(), 78, LocalDateTime.now().plusMinutes(78), list);
        managerForTest.updateEpic(newEpic);
        assertFalse(managerForTest.getListAllEpic().contains(epic));
        assertTrue(managerForTest.getListAllEpic().contains(newEpic));
        assertEquals(managerForTest.getListAllEpic().get(0), newEpic);
        assertEquals(managerForTest.getListAllEpic().size(), 2);
        assertEquals(managerForTest.getEpicById(subtask.getParentId()), newEpic);
        assertTrue(newEpic.getSubtasks().contains(subtask));
        assertEquals(newEpic.getSubtasks().size(), 1);
    }

    @Test
    void shouldUpdateEpicTaskSubtaskNull() {
        epic = null;
        task = null;
        subtask = null;
        managerForTest.updateEpic(epic);
        managerForTest.updateSubtask(subtask);
        managerForTest.updateTask(task);
        assertNotNull(managerForTest.getListAllEpic().get(0));
        assertNotNull(managerForTest.getListAllTasks().get(0));
        assertNotNull(managerForTest.getListAllSubtasks().get(0));
    }

    @Test
    void shouldDeleteTaskById() {
        managerForTest.deleteTaskById(1);
        assertFalse(managerForTest.getListAllTasks().contains(task));
        assertEquals(managerForTest.getListAllTasks().size(), 0);

    }

    @Test
    void shouldDeleteEpicById() {
        managerForTest.deleteEpicById(epic.getId());
        assertFalse(managerForTest.getListAllEpic().contains(epic));
        assertEquals(managerForTest.getListAllEpic().size(), 1);
        assertFalse(managerForTest.getListAllSubtasks().contains(subtask));
        assertFalse(managerForTest.getListAllSubtasks().contains(sub2));
        assertEquals(managerForTest.getListAllSubtasks().size(), 0);
    }

    @Test
    void shouldDeleteSubtaskById() {
        managerForTest.deleteSubtaskById(subtask.getId());
        assertFalse(managerForTest.getListAllSubtasks().contains(subtask));
        assertEquals(managerForTest.getListAllSubtasks().size(), 1);
        assertFalse(epic.getSubtasks().contains(subtask));
        assertEquals(epic.getSubtasks().size(), 1);

    }

    @Test
    void shouldDeleteEpicTaskSubtaskByWrongId() {
        managerForTest.deleteSubtaskById(8);
        managerForTest.deleteEpicById(8);
        managerForTest.deleteTaskById(8);
    }

    @Test
    void shouldGetListSubtasksOfEpic() {
        assertEquals(epic.getSubtasks().size(), 2);
        assertTrue(epic.getSubtasks().contains(subtask));
        assertTrue(epic.getSubtasks().contains(sub2));
    }

    @Test
    void shouldGetEmptyListSubtasksOfEpic() {
        managerForTest.deleteAllSubtasks();
        assertEquals(epic.getSubtasks().size(), 0);
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldDefineStatusEpic() {
        subtask.setStatus(IN_PROGRESS);
        managerForTest.updateEpic(epic);
        assertEquals(epic.getStatus(), IN_PROGRESS);

        subtask.setStatus(NEW);
        managerForTest.updateSubtask(subtask);
        assertEquals(epic.getStatus(), NEW);

        subtask.setStatus(DONE);
        sub2.setStatus(DONE);
        managerForTest.updateEpic(epic);
        assertEquals(epic.getStatus(), DONE);

        subtask.setStatus(NEW);
        managerForTest.updateSubtask(subtask);
        assertEquals(epic.getStatus(), IN_PROGRESS);
    }

    @Test
    void shouldDefineStatusEpicWithoutSub() {
        assertEquals(epicWithoutSub.getStatus(), NEW);
        epicWithoutSub.setStatus(IN_PROGRESS);
        assertEquals(epicWithoutSub.getStatus(), IN_PROGRESS);
        epicWithoutSub.setStatus(DONE);
        assertEquals(epicWithoutSub.getStatus(), DONE);
    }

    @Test
    void shouldGetPrioritizedTasks() {
        assertEquals(managerForTest.getPrioritizedTasks().get(0), task);
        assertEquals(managerForTest.getPrioritizedTasks().get(1), subtask);
        assertEquals(managerForTest.getPrioritizedTasks().get(2), sub2);
    }

    @Test
    void shouldDefineStatusEpicDone() {
        epic.setStatus(DONE);
        managerForTest.updateEpic(epic);
        assertEquals(epic.getStatus(), NEW);
        assertEquals(epic.getSubtasks().get(0).getStatus(), NEW);

        subtask.setStatus(IN_PROGRESS);
        managerForTest.updateSubtask(subtask);
        epic.setStatus(DONE);
        managerForTest.updateEpic(epic);
        assertEquals(epic.getStatus(), IN_PROGRESS);
    }

    @Test
    void shouldEmptyGetPrioritizedTasks() {
        managerForTest.deleteAllTasks();
        managerForTest.deleteAllSubtasks();
        assertEquals(managerForTest.getPrioritizedTasks().size(), 0);
    }

    @Test
    void shouldGetHistoryInMemoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertEquals(historyManager.getHistory().get(0), task);
        assertEquals(historyManager.getHistory().get(1), epic);
        assertEquals(historyManager.getHistory().get(2), subtask);
        assertEquals(historyManager.getHistory().get(3), sub2);
        assertEquals(historyManager.getHistory().get(4), epicWithoutSub);
        assertEquals(historyManager.getHistory().size(), 5);
    }

    @Test
    void shouldDeleteHistoryFirstInMemoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        managerForTest.deleteTaskById(1);
        assertEquals(historyManager.getHistory().get(0), epic);
        assertEquals(historyManager.getHistory().get(1), subtask);
        assertEquals(historyManager.getHistory().get(2), sub2);
        assertEquals(historyManager.getHistory().get(3), epicWithoutSub);
        assertEquals(historyManager.getHistory().size(), 4);
    }

    @Test
    void shouldDeleteHistoryLastInMemoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        managerForTest.deleteEpicById(5);
        assertEquals(historyManager.getHistory().get(0), task);
        assertEquals(historyManager.getHistory().get(1), epic);
        assertEquals(historyManager.getHistory().get(2), subtask);
        assertEquals(historyManager.getHistory().get(3), sub2);
        assertEquals(historyManager.getHistory().size(), 4);
    }

    @Test
    void shouldDeleteHistoryFromMiddleInMemoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        managerForTest.deleteSubtaskById(3);
        assertEquals(historyManager.getHistory().get(0), task);
        assertEquals(historyManager.getHistory().get(1), epic);
        assertEquals(historyManager.getHistory().get(2), sub2);
        assertEquals(historyManager.getHistory().get(3), epicWithoutSub);
        assertEquals(historyManager.getHistory().size(), 4);
    }

    @Test
    void shouldDeleteHistoryEpicWithSubtasksInMemoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        managerForTest.deleteEpicById(2);
        assertEquals(historyManager.getHistory().get(0), task);
        assertEquals(historyManager.getHistory().get(1), epicWithoutSub);
        assertEquals(historyManager.getHistory().size(), 2);
    }

    @Test
    void shouldHistoryNotDouble() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        managerForTest.getSubtaskById(3);
        managerForTest.getSubtaskById(4);
        managerForTest.getEpicById(5);
        managerForTest.getTaskById(1);
        managerForTest.getEpicById(2);
        assertEquals(historyManager.getHistory().get(0), subtask);
        assertEquals(historyManager.getHistory().get(1), sub2);
        assertEquals(historyManager.getHistory().get(2), epicWithoutSub);
        assertEquals(historyManager.getHistory().get(3), task);
        assertEquals(historyManager.getHistory().get(4), epic);
        assertEquals(historyManager.getHistory().size(), 5);
    }
}
