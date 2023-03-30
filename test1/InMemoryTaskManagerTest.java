import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Task.StatusList.*;

class InMemoryTaskManagerTest {

    private final InMemoryTaskManager manager = new InMemoryTaskManager();

    @Test
    void shouldSaveAndDeleteTaskSubtaskFromSortedlist() {
        Task task = new Task("test", "test", 30, LocalDateTime.parse("2023-03-27T10:00"));
        Task task1 = new Task("test1", "test1", 30, LocalDateTime.parse("2023-03-26T10:00"));
        Epic epic = new Epic("RRRRR", "LLLLLL");
        Subtask subtask = new Subtask(0, "JJJJJJ", "IIIIIII", NEW, LocalDateTime.parse("2023-03-28T10:00"), 60, LocalDateTime.parse("2023-03-26T10:00").plusMinutes(60), 3);
        manager.saveTaskAndEpic(task1);
        manager.saveTaskAndEpic(task);
        manager.saveTaskAndEpic(epic);
        manager.saveSubtask(subtask, epic);
        System.out.println(manager.getSortedList());
        assertFalse(manager.getSortedList().isEmpty());
        assertEquals(manager.getSortedList().size(), 3);
        assertEquals(manager.getSortedList().first(), task1);
        assertTrue(manager.getSortedList().contains(task));
        assertTrue(manager.getSortedList().contains(task1));
        manager.deleteAllTasks();
        assertEquals(manager.getSortedList().size(), 1);

    }
}