package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    protected File savingFileManager = null;

    public FileBackedTasksManager(File file) throws IOException {
        this.savingFileManager = file;
        writeHeader();
    }


    private void save(Task task) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savingFileManager, true))) {
            writer.append(task.toString() + "\r\n");
        } catch (IOException e) {
            System.out.println("Oшибка");
        }

    }

    private void writeHeader() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savingFileManager, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,description,status,listSubtasks/parentId" + "\r\n");
        } catch (IOException e) {
            System.out.println("Oшибка");
        }
    }

    public void saveTaskAndEpicInFile(Task task) throws IOException {
        super.saveTaskAndEpic(task);
        save(task);
    }

    public void saveSubtaskInFile(Subtask subtask, Epic epic) throws IOException {
        super.saveSubtask(subtask, epic);
        save(subtask);
    }

    public void updateTaskInFile(Task task) throws IOException {
        super.updateTask(task);
        saveStringInFile(saveFailInString(task));
    }

    public void updateSubtaskInFile(Subtask subtask) {
        super.updateSubtask(subtask);
        saveStringInFile(saveFailInString(subtask));
    }

    public void updateEpicInFile(Epic epic) {
        super.updateEpic(epic);
        saveStringInFile(saveFailInString(epic));
    }

    private String saveFailInString(Task task) {
        StringBuilder stringInMemory = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(savingFileManager, StandardCharsets.UTF_8))) {
            int taskIdSize = (int) (String.valueOf(task.getId()).length());
            while (reader.ready()) {
                String taskInFile = reader.readLine();
                String taskInFileId = taskInFile.substring(0, taskIdSize);

                if (taskInFile.equals("id,type,name,description,status,listSubtasks/parentId")) {
                    stringInMemory.append(taskInFile + "\r\n");

                } else if (taskInFileId.equals(String.valueOf(task.getId()))) {
                    stringInMemory.append(task.toString() + "\r\n");

                } else {
                    stringInMemory.append(taskInFile + "\r\n");


                }
            }
        } catch (IOException e) {
            System.out.println("Oшибка");
        }
        return String.valueOf(stringInMemory);
    }

    private void saveStringInFile(String string) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savingFileManager, StandardCharsets.UTF_8))) {
            writer.write(string);

        } catch (IOException e) {
            System.out.println("Oшибка");
        }
    }

}