import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

import static model.Task.statusList.*;

public class Manager {
    int taskId = 0;
    private final HashMap<Integer, Task> listEpics = new HashMap<>();
    public final HashMap<Integer, Task> listTasks = new HashMap<>();
    public final HashMap<Integer, Task> listSubtasks = new HashMap<>();

    public HashMap<Integer, Task> getListEpics() {
        return listEpics;
    }

    public HashMap<Integer, Task> getListTasks() {
        return listTasks;
    }

    public HashMap<Integer, Task> getListSubtasks() {
        return listSubtasks;
    }

    public int assignId(Task newTask) {
        newTask.setId(++taskId);
        return taskId;
    }

    public void saveTaskAndEpic(Task task) {
        assignId(task);
        if (task.getClass().equals(Epic.class) && task != null) {
            listEpics.put(task.getId(), task);
            defineStatusEpic((Epic) task);
        } else if (task.getClass().equals(Task.class) && task != null) {
            listTasks.put(task.getId(), task);
        }
    }
    public void saveSubtask(Subtask subtask,Epic epic) {
            assignId(subtask);
            epic.getSubtasksId().add(subtask.getId());
        listSubtasks.put(subtask.getId(), subtask);
        defineStatusEpic(epic);
    }

    public ArrayList<Task> getListAllEpic() {
        ArrayList<Task> epics = new ArrayList<>();
        for (int key : listEpics.keySet()) {
            epics.add(listEpics.get(key));
        }
        return epics;
    }

    public ArrayList<Task> getListAllSubtasks() {
        ArrayList<Task> subtasks = new ArrayList<>();
        for (int key : listSubtasks.keySet()) {
            subtasks.add(listSubtasks.get(key));
        }
        return subtasks;
    }

    public ArrayList<Task> getListAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int key : listTasks.keySet()) {
            tasks.add(listTasks.get(key));
        }
        return tasks;
    }
    public HashMap<Integer, Task> deleteAllTasks() {
        if(!listTasks.isEmpty()){
            listTasks.clear();
        }
        return listTasks;
    }
    public HashMap<Integer, Task> deleteAllEpics() {
        if (!listEpics.isEmpty()) {
            if (!listSubtasks.isEmpty()) {
                listSubtasks.clear();
            }
            listEpics.clear();
        }
        return listEpics;
    }
    public HashMap<Integer, Task> deleteAllSubtasks() {
        if (!listSubtasks.isEmpty()) {
            listSubtasks.clear();
            for (int i = 1; i < listEpics.size(); i++) {
                if (!listEpics.get(i).equals(null)) {
                    Epic epic = (Epic) listEpics.get(i);
                    epic.getSubtasksId().clear();
                    defineStatusEpic(epic);
                }
            }
        }
        return listSubtasks;
    }
    public Task getTaskById(int id) {
        return listTasks.get(id);
    }

    public Task getEpicById(int id) {
        return listEpics.get(id);
    }

    public Task getSubtasksById(int id) {
        return listSubtasks.get(id);
    }
    public void updateTask(Task task) {
        if (listTasks.containsKey(task.getId())) {
            listTasks.put(task.getId(), task);
        }
    }
    public void updateEpic(Epic epic) {
        if (listEpics.containsKey(epic.getId())) {
            listEpics.put(epic.getId(), epic);
            defineStatusEpic(epic);
        }
    }
    public void updateSubtask(Subtask subtask){
        if (listSubtasks.containsKey(subtask.getId())) {
            listSubtasks.put(subtask.getId(), subtask);
            Epic epic = (Epic) listEpics.get(subtask.getParentId());
            ArrayList<Integer> subtasksId = epic.getSubtasksId();
            int idSubtaskInEpic = 0;
            for (int i = 0; i < subtasksId.size(); i++) {
                if (subtask.getId() == epic.getSubtasksId().get(i)) {
                    idSubtaskInEpic = i;
                }
            }
            subtasksId.remove(idSubtaskInEpic);
            subtasksId.add(idSubtaskInEpic, subtask.getId());
            updateEpic(epic);
        }
    }
    public void deleteTaskById(int id) {
        if(listTasks.containsKey(id)){
            listTasks.remove(id);
        }
    }
    public void deleteSubtaskById(int id) {
        if(listSubtasks.containsKey(id)) {
            Subtask subtask = (Subtask) listSubtasks.get(id);
            int epicId = subtask.getParentId();
            listSubtasks.remove(id);
            Epic epic = (Epic) listEpics.get(epicId);
            epic.getSubtasksId().remove((Object) subtask.getId());
            defineStatusEpic(epic);
        }
    }
    public void deleteEpicById(int id) {
        if(listEpics.containsKey(id)) {
            Epic epic = (Epic) listEpics.get(id);
            if (!epic.getSubtasksId().isEmpty()) {
                for (int value : epic.getSubtasksId()) {
                    listSubtasks.remove(value);
                }
                epic.getSubtasksId().clear();
            }
            listEpics.remove(id);

        }
    }
    public ArrayList<Task> getListSubtasksOfEpic(Epic epic) {
        ArrayList<Task> subtasksOfEpic = new ArrayList<>();
        if (!epic.getSubtasksId().isEmpty()) {
            for (int id : epic.getSubtasksId()) {
                subtasksOfEpic.add(listSubtasks.get(id));
            }
        }
        return subtasksOfEpic;
    }
    public void defineStatusEpic(Epic epic) {
        ArrayList<Task> list = getListSubtasksOfEpic(epic);
        if (list.isEmpty()) {
            epic.setStatus(NEW);
        } else {
            boolean isDone = list.stream()
                    .allMatch(subtask -> subtask.getStatus().equals(DONE));
            boolean isNew = list.stream()
                    .allMatch(subtask -> subtask.getStatus().equals(NEW));
            if (isDone) {
                epic.setStatus(DONE);
            } else if (isNew) {
                epic.setStatus(NEW);
            } else {
                epic.setStatus(IN_PROGRESS);
            }
        }
    }

}