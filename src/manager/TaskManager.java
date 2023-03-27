package manager;

import tasks.*;

import java.util.*;

public interface TaskManager<T extends Task> {

    void saveTaskAndEpic(Task task);

    void saveSubtask(Subtask subtask, Epic epic);

    ArrayList<Epic> getListAllEpic();

    ArrayList<Subtask> getListAllSubtasks();

    ArrayList<Task> getListAllTasks();

    HashMap<Integer, Task> deleteAllTasks();

    HashMap<Integer, Epic> deleteAllEpics();

    HashMap<Integer, Subtask> deleteAllSubtasks();

    T getTaskById(int id);


    T getEpicById(int id);

    T getSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    public void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    ArrayList<T> getListSubtasksOfEpic(Epic epic);

    void defineStatusEpic(Epic epic);

    List getPrioritizedTasks();

}
