package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

import static tasks.Task.StatusList.*;
import static tasks.TypeTask.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    protected final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public void saveTaskAndEpic(Task task) {
        super.saveTaskAndEpic(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void saveSubtask(Subtask subtask, Epic epic) {
        super.saveSubtask(subtask, epic);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = (Epic) super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = (Subtask) super.getSubtaskById(id);
        save();
        return subtask;
    }

    private void writeHeader() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            String header = "id,type,name,description,status,listSubtasks/parentId";
            writer.write(header + System.lineSeparator());

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при записи в файл");
        }
    }

    private void save() {
        writeHeader();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            saveListTasksInFile(writer, listTasks);
            saveListTasksInFile(writer, listSubtasks);
            saveListTasksInFile(writer, listEpics);
            saveHistoryInFile(writer);


        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при чтении из файла");
        }
    }

    private void saveListTasksInFile(BufferedWriter writer, HashMap<Integer, ? extends Task> list) throws IOException {
        if (!list.isEmpty()) {
            for (int key : list.keySet()) {
                writer.append(list.get(key).toString() + System.lineSeparator());
            }
        }
    }

    private void saveHistoryInFile(BufferedWriter writer) throws IOException {
        List<Task> list = historyManager.getHistory();
        if (!list.isEmpty()) {
            writer.append(System.lineSeparator());
            for (Task task : list) {
                writer.append(task.getId() + ",");
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(Path path) throws ManagerSaveException {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager((path).toFile());

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {


            while (reader.ready()) {

                String[] taskString = reader.readLine().split(",");
                if (taskString[0].isEmpty()) {
                    continue;
                }

                if (taskString[1].equals(EPIC.toString())) {
                    listEpics.put(Integer.valueOf(taskString[0]), toEpic(taskString));

                } else if (taskString[1].equals(SUBTASK.toString())) {
                    listSubtasks.put(Integer.valueOf(taskString[0]), toSubtask(taskString));

                } else if (taskString[1].equals(TASK.toString())) {
                    listTasks.put(Integer.valueOf(taskString[0]), toTask(taskString));
                } else if (!taskString[0].equals("id")) {
                    getHistoryFromFile(taskString);
                }

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при чтении из файла");
        }
        return fileBackedTasksManager;
    }

    private static void getHistoryFromFile(String[] taskString) {
        for (String id : taskString) {
            if (listEpics.containsKey(Integer.valueOf(id))) {
                historyManager.add(listEpics.get(Integer.valueOf(id)));
            } else if (listTasks.containsKey(Integer.valueOf(id))) {
                historyManager.add(listTasks.get(Integer.valueOf(id)));
            } else if (listSubtasks.containsKey(Integer.valueOf(id))) {
                historyManager.add(listSubtasks.get(Integer.valueOf(id)));
            }
        }
    }


    private static Task.StatusList getStatus(String string) {
        Task.StatusList status = null;

        if (string.equals(NEW.toString())) {
            status = NEW;
        } else if (string.equals(IN_PROGRESS.toString())) {
            status = IN_PROGRESS;
        } else if (string.equals(DONE.toString())) {
            status = DONE;
        }
        return status;
    }

    private static Task toTask(String[] taskString) {
        LocalDateTime start = taskString[5].equals("null") ? null : LocalDateTime.parse(taskString[5]);
        LocalDateTime end = taskString[7].equals("null") ? null : LocalDateTime.parse(taskString[7]);
        return new Task(Integer.parseInt(taskString[0]), taskString[2], taskString[3], getStatus(taskString[4]), start, Integer.parseInt(taskString[6]), end);
    }

    private static Epic toEpic(String[] taskString) {

        ArrayList<Subtask> subtask = new ArrayList<>();
        if (taskString.length > 8) {
            String[] listSubtaskId = (taskString[8]).split(" ");


            for (String id : listSubtaskId) {
                subtask.add(listSubtasks.get(Integer.parseInt(id)));
            }
        } else {
            subtask = null;
        }
        LocalDateTime start = taskString[5].equals("null") ? null : LocalDateTime.parse(taskString[5]);
        LocalDateTime end = taskString[7].equals("null") ? null : LocalDateTime.parse(taskString[7]);
        return new Epic(Integer.parseInt(taskString[0]), taskString[2], taskString[3], getStatus(taskString[4]), start, Integer.parseInt(taskString[6]), end, subtask);
    }

    private static Subtask toSubtask(String[] taskString) {
        LocalDateTime start = taskString[5].equals("null") ? null : LocalDateTime.parse(taskString[5]);
        LocalDateTime end = taskString[7].equals("null") ? null : LocalDateTime.parse(taskString[7]);
        return new Subtask(Integer.parseInt(taskString[0]), taskString[2], taskString[3], getStatus(taskString[4]), start,
                Integer.parseInt(taskString[6]), end, Integer.parseInt(taskString[8]));
    }
}
