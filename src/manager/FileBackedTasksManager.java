package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
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
            saveListTasksInFile(writer, listEpics);
            saveListTasksInFile(writer, listSubtasks);
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

    public static void loadFromFile(File file) throws ManagerSaveException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {


            while (reader.ready()) {

                String[] taskString = reader.readLine().split(",");
                if (taskString[0].equals("")) {
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
        return new Task(Integer.valueOf(taskString[0]), taskString[2], taskString[3], getStatus(taskString[4]));
    }

    private static Epic toEpic(String[] taskString) {

        ArrayList<Integer> subtaskId = new ArrayList<>();
        if (taskString.length > 5) {
            String[] listSubtask = (taskString[5]).split(" ");


            for (String id : listSubtask) {
                subtaskId.add(Integer.valueOf(id));
            }
        } else {
            subtaskId = null;
        }

        return new Epic(Integer.valueOf(taskString[0]), taskString[2], taskString[3], getStatus(taskString[4]), subtaskId);
    }

    private static Subtask toSubtask(String[] taskString) {

        return new Subtask(Integer.valueOf(taskString[0]), taskString[2], taskString[3], getStatus(taskString[4]), Integer.valueOf(taskString[5]));
    }

    public static void main(String[] args) {
        FileBackedTasksManager f = new FileBackedTasksManager(new File("./fileBackedTasksManager.csv"));

        Epic epic1 = new Epic("epic1", "описание 1");
        f.saveTaskAndEpic(epic1);
        Epic epic2 = new Epic("epic2", "ОПИСАНИЕ2");
        f.saveTaskAndEpic(epic2);
        Subtask sub1 = new Subtask(epic1.getId(), "sub1", "ОПИСАНИЕ3", NEW);
        f.saveSubtask(sub1, epic1);
        Subtask sub2 = new Subtask(epic1.getId(), "sub2 ", "ОПИСАНИЕ4", DONE);
        f.saveSubtask(sub2, epic1);
        Task task1 = new Task("task1", "ОПИСАНИЕ4", NEW);
        f.saveTaskAndEpic(task1);
        f.getTaskById(task1.getId());
        f.getEpicById(epic1.getId());
        f.getEpicById(epic2.getId());
        f.getSubtaskById(sub1.getId());
        f.getSubtaskById(sub2.getId());


        loadFromFile(Path.of("./fileBackedTasksManager.csv").toFile());

        System.out.println(listEpics + "\n" + listSubtasks + "\n" + listTasks + "\n" + historyManager.getHistory());
    }
}
