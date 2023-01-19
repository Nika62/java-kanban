import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
public class Manager {
    int taskId = 0;
    HashMap<Integer, Task> listEpics = new HashMap<>();
    HashMap<Integer, Task> listTasks = new HashMap<>();
    HashMap<Integer, Task> listSubtasks = new HashMap<>();
    public int assignId(Task newtask) {
        newtask.setId(++taskId);
        return taskId;
    }
    public void saveTaskAndEpic(Task task) {
        assignId(task);
        if(task.getClass().equals(Epic.class)) {
            listEpics.put(task.getId(), task);
        }else if (task.getClass().equals(Task.class)) {
            listTasks.put(task.getId(), task);
        }
    }
    public void saveSubtask(Subtask subtask,Epic epic) {
            assignId(subtask);
            epic.getSubtasksId().add(subtask.getId());
            subtask.setParentId(epic.getId());
            listSubtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Task> getListAllEpic() {
        ArrayList<Task> epics = new ArrayList<>();
        for (int key : listEpics.keySet()){
            epics.add(listEpics.get(key));
        }
        return epics;
    }
    public ArrayList<Task> getListAllSubasks() {
        ArrayList<Task> subtasks = new ArrayList<>();
        for (int key : listSubtasks.keySet()){
            subtasks.add(listSubtasks.get(key));
        }
        return subtasks;
    }

    public ArrayList<Task> getListAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int key : listTasks.keySet()){
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
            listEpics.clear();
        }
        return listEpics;
    }

    public HashMap<Integer, Task> deleteAllSubtasks() {
        if (!listSubtasks.isEmpty()) {
            listSubtasks.clear();
        }
        return listSubtasks;
    }

    public Task getTaskById(int id) {
        return listTasks.get(id);
    }

    public Task getEpickById(int id) {
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
            defineStatusEpic(epic);
            listEpics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask){
        if (listSubtasks.containsKey(subtask.getId())) {
            listSubtasks.put(subtask.getId(), subtask);
        }
    }
    public void deleteTaskById(int id) {
        if(listTasks.containsKey(id)){
            listTasks.remove(id);
        }
    }

    public void deleteSubtaskById(int id) {
        if(listSubtasks.containsKey(id)){
            listSubtasks.remove(id);
        }
    }

    public void deleteEpicById(int id) {
        if(listEpics.containsKey(id)){
            listEpics.remove(id);
        }
    }
    public ArrayList<Task> getListSubtasksOfEpic(Epic epic) {
        ArrayList<Task> subtasksOfEpic = new ArrayList<>();
        for (int id : epic.getSubtasksId()){
            subtasksOfEpic.add(listSubtasks.get(id));
        }
        return subtasksOfEpic;
    }
    public void defineStatusEpic(Epic epic) {
        ArrayList<Task> list = getListSubtasksOfEpic(epic);
        if(list.isEmpty()){
           epic.setStatus("NEW");
        }
        boolean isDone = list.stream()
                .allMatch(status->status.equals("DONE"));
        boolean isNew = list.stream()
                .allMatch(status->status.equals("NEW"));
        if(isDone){
            epic.setStatus("DONE");
        } else if(isNew){
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

}