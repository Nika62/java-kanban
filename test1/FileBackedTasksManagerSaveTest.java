import manager.*;
import org.junit.jupiter.api.*;
import tasks.*;

import java.io.*;
import java.nio.charset.*;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TypeTask.*;

public class FileBackedTasksManagerSaveTest {
    FileBackedTasksManager fileBackedTasksManagerSaveTest;

    @BeforeEach
    void beforeEach() {
        fileBackedTasksManagerSaveTest = new FileBackedTasksManager(new File("test1/resources/test_save.csv"));
    }

    @AfterEach
    void afterEach() {
        fileBackedTasksManagerSaveTest.deleteAllEpics();
        fileBackedTasksManagerSaveTest.deleteAllTasks();
        fileBackedTasksManagerSaveTest.deleteAllSubtasks();
        fileBackedTasksManagerSaveTest = null;
    }

    @Test
    void shouldSaveTaskInFile() {
        fileBackedTasksManagerSaveTest = new FileBackedTasksManager(new File("test1/resources/test_saveTask.csv"));
        Task task = new Task("task", "описание таск", 10, LocalDateTime.parse("2001-01-01T00:00"));
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(task);
        try (BufferedReader reader = new BufferedReader(new FileReader("test1/resources/test_saveTask.csv", StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String[] taskString = reader.readLine().split(",");
                if (taskString[0].equals("id")) {
                    continue;
                } else if (taskString[1].equals(TASK.toString()))
                    assertEquals(taskString[2], "task");
                assertEquals(taskString[7], "2001-01-01T00:10");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задачи");
        }
    }

    @Test
    void shouldSaveEpicWithoutSubtasksInFile() {
        fileBackedTasksManagerSaveTest = new FileBackedTasksManager(new File("test1/resources/test_saveEpicWithoutSub.csv"));
        Epic epic = new Epic("epic", "описание эпик");
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(epic);
        try (BufferedReader reader = new BufferedReader(new FileReader("test1/resources/test_saveEpicWithoutSub.csv", StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String[] taskString = reader.readLine().split(",");
                if (taskString[0].equals("id")) {
                    continue;
                } else if (taskString[1].equals(EPIC.toString())) {
                    assertEquals(taskString[1], EPIC.toString());
                    assertEquals(taskString[2], "epic");
                    assertEquals(taskString[5], "null");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения эпика без подзадач");
        }
    }

    @Test
    void shouldSaveEpicAndSubtaskInFile() {
        fileBackedTasksManagerSaveTest = new FileBackedTasksManager(new File("test1/resources/test_saveEpicAndSub.csv"));
        Epic epic = new Epic("epic", "описание эпик");
        Subtask subtask = new Subtask(1, "subtask", "описание subtask", 20, LocalDateTime.parse("2001-02-02T02:20"));
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(epic);
        fileBackedTasksManagerSaveTest.saveSubtask(subtask, epic);
        try (BufferedReader reader = new BufferedReader(new FileReader("test1/resources/test_saveEpicAndSub.csv", StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String[] taskString = reader.readLine().split(",");
                if (taskString[0].equals("id")) {
                    continue;
                } else if (taskString[1].equals(SUBTASK.toString())) {
                    assertEquals(taskString[2], "subtask");
                    assertEquals(taskString[5], "2001-02-02T02:20");
                    assertEquals(taskString[8], "1");
                } else if (taskString[1].equals(EPIC.toString())) {
                    assertEquals(taskString[2], "epic");
                    assertEquals(taskString[5], "2001-02-02T02:20");
                    assertEquals(taskString[7], "2001-02-02T02:40");
                    assertEquals((taskString[8]), "2 ");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения эпика и подзадачи");
        }
    }

    @Test
    void shouldSaveHistoryInFile() {
        fileBackedTasksManagerSaveTest = new FileBackedTasksManager(new File("test1/resources/test_historyInFile.csv"));
        Epic epic = new Epic("epic", "описание эпик");
        Task task = new Task("task", "описание таск", 10, LocalDateTime.parse("2001-01-01T00:00"));
        Subtask subtask = new Subtask(1, "subtask", "описание subtask", 20, LocalDateTime.parse("2001-02-02T02:20"));
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(epic);
        fileBackedTasksManagerSaveTest.saveSubtask(subtask, epic);
        fileBackedTasksManagerSaveTest.saveTaskAndEpic(task);
        fileBackedTasksManagerSaveTest.getTaskById(3);
        fileBackedTasksManagerSaveTest.getEpicById(1);
        fileBackedTasksManagerSaveTest.getSubtaskById(2);
        try (BufferedReader reader = new BufferedReader(new FileReader("test1/resources/test_historyInFile.csv", StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String[] taskString = reader.readLine().split(",");
                if (taskString[0].isEmpty() || taskString[0].equals("id") || taskString[1].equals(TASK.toString())
                        || taskString[1].equals(SUBTASK.toString()) || taskString[1].equals(EPIC.toString())) {
                    continue;
                }
                assertEquals(taskString[0], "3");
                assertEquals(taskString[1], "1");
                assertEquals(taskString[2], "2");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения эпика и подзадачи");
        }
    }
}
