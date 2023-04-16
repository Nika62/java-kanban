package manager;

import tasks.*;

import java.util.*;

public interface TaskManager<T extends Task> {

    boolean saveTaskAndEpic(Task task);

    boolean saveSubtask(Subtask subtask, Epic epic);

    ArrayList<Epic> getListAllEpic();

    ArrayList<Subtask> getListAllSubtasks();

    ArrayList<Task> getListAllTasks();

    HashMap<Integer, Task> deleteAllTasks();

    HashMap<Integer, Epic> deleteAllEpics();

    HashMap<Integer, Subtask> deleteAllSubtasks();

    T getTaskById(int id);


    T getEpicById(int id);

    T getSubtaskById(int id);

    boolean updateTask(Task task);

    boolean updateEpic(Epic epic);

    boolean updateSubtask(Subtask subtask);

    public void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    ArrayList<T> getListSubtasksOfEpic(Epic epic);

    List getPrioritizedTasks();

}
