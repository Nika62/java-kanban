package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    protected static File savingFileManager = null;

    public FileBackedTasksManager(File file) {
        this.savingFileManager = file;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(savingFileManager, StandardCharsets.UTF_8)) {
            saveEpicInFile(writer);
            saveSubtasksInFile(writer);
            saveTasksInFile(writer);
            writer.write(System.lineSeparator());
            saveHistoryInFile(writer);

        } catch (IOException e) {
            System.out.println("Oшибка");
        }
    }

    private void saveEpicInFile(FileWriter writer) throws IOException {
        if (!listEpics.isEmpty()) {
            writer.write("id, name, description, status, subtasksId");
            writer.write(System.lineSeparator());
            for (int key : listEpics.keySet()) {
                Epic epic = listEpics.get(key);
                String listSubtasksId = EpicSubtasksIdToString(epic.getSubtasksId());
                writer.write(String.valueOf(epic.getId() + ", " + epic.getName() + ", " + epic.getDescription() + ", " + String.valueOf(epic.getStatus()) + ", " + listSubtasksId));
                writer.write(System.lineSeparator());
            }
        }
    }

    private String EpicSubtasksIdToString(ArrayList<Integer> list) {
        StringBuilder listId = new StringBuilder();
        for (int subtasksId : list) {
            listId.append(subtasksId);
        }
        return String.valueOf(listId);
    }

    private void saveSubtasksInFile(FileWriter writer) throws IOException {
        if (!listSubtasks.isEmpty()) {
            writer.write("id, name, description, status, parentId");
            writer.write(System.lineSeparator());
            for (int key : listSubtasks.keySet()) {
                Subtask subtask = listSubtasks.get(key);
                writer.write(String.valueOf(subtask.getId() + ", " + subtask.getName() + ", " + subtask.getDescription() + ", " + String.valueOf(subtask.getStatus()) + ", " + String.valueOf(subtask.getParentId())));
                writer.write(System.lineSeparator());
            }
        }
    }

    private void saveTasksInFile(FileWriter writer) throws IOException {
        if (!listTasks.isEmpty()) {
            writer.write("id, name, description, status");
            writer.write(System.lineSeparator());
            for (int key : listTasks.keySet()) {
                Task task = listTasks.get(key);
                writer.write(String.valueOf(task.getId() + ", " + task.getName() + ", " + task.getDescription() + ", " + String.valueOf(task.getStatus())));
                writer.write(System.lineSeparator());
            }
        }
    }

    private void saveHistoryInFile(FileWriter writer) throws IOException {
        List<Task> list = historyManager.getHistory();

        for (Task task : list) {
            if (list.size() != (list.indexOf(task) + 1)) {
                writer.write(task.getId() + ",");
            } else {
                writer.write(task.getId());
            }
        }
    }
}