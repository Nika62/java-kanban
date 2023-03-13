package manager;

import tasks.*;

import java.util.*;

import static tasks.Task.StatusList.*;


public class InMemoryTaskManager implements TaskManager {
    protected int taskId = 0;
    protected static final HashMap<Integer, Epic> listEpics = new HashMap<>();
    protected static final HashMap<Integer, Task> listTasks = new HashMap<>();
    protected static final HashMap<Integer, Subtask> listSubtasks = new HashMap<>();
    static HistoryManager historyManager = Managers.getDefaultHistory();

    public HashMap<Integer, Epic> getListEpics() {
        return listEpics;
    }

    public HashMap<Integer, Task> getListTasks() {
        return listTasks;
    }

    public HashMap<Integer, Subtask> getListSubtasks() {
        return listSubtasks;
    }

    protected int assignId(Task newTask) {
        newTask.setId(++taskId);
        return taskId;
    }

    @Override
    public void saveTaskAndEpic(Task task) {
        assignId(task);
        if (task.getClass().equals(Epic.class) && task != null) {
            listEpics.put(task.getId(), (Epic) task);
            defineStatusEpic((Epic) task);
        } else if (task.getClass().equals(Task.class) && task != null) {
            listTasks.put(task.getId(), task);
        }
    }
    @Override
    public void saveSubtask(Subtask subtask, Epic epic) {
        assignId(subtask);
        epic.getSubtasksId().add(subtask.getId());
        listSubtasks.put(subtask.getId(), subtask);
        updateEpic(epic);
    }
    @Override
    public ArrayList<Task> getListAllEpic() {
        ArrayList<Task> epics = new ArrayList<>();
        for (int key : listEpics.keySet()) {
            epics.add(listEpics.get(key));
        }
        return epics;
    }
    @Override
    public ArrayList<Task> getListAllSubtasks() {
        ArrayList<Task> subtasks = new ArrayList<>();
        for (int key : listSubtasks.keySet()) {
            subtasks.add(listSubtasks.get(key));
        }
        return subtasks;
    }
    @Override
    public ArrayList<Task> getListAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int key : listTasks.keySet()) {
            tasks.add(listTasks.get(key));
        }
        return tasks;
    }

    @Override
    public HashMap<Integer, Task> deleteAllTasks() {
        if (!listTasks.isEmpty()) {
            listTasks.clear();
        }
        return listTasks;
    }

    @Override
    public HashMap<Integer, Epic> deleteAllEpics() {
        if (!listEpics.isEmpty()) {
            if (!listSubtasks.isEmpty()) {
                listSubtasks.clear();
            }
            listEpics.clear();
        }
        return listEpics;
    }

    @Override
    public HashMap<Integer, Subtask> deleteAllSubtasks() {
        if (!listSubtasks.isEmpty()) {
            listSubtasks.clear();
            for (int i = 1; i < listEpics.size(); i++) {
                if (!listEpics.get(i).equals(null)) {
                    Epic epic = listEpics.get(i);
                    epic.getSubtasksId().clear();
                    updateEpic(epic);
                }
            }
        }
        return listSubtasks;
    }
    @Override
    public Task getTaskById(int id) {
        Managers.getDefaultHistory().add(listTasks.get(id));
        return listTasks.get(id);
    }
    @Override
    public Task getEpicById(int id) {
        Managers.getDefaultHistory().add(listEpics.get(id));
        return listEpics.get(id);
    }

    @Override
    public Task getSubtaskById(int id) {
        Managers.getDefaultHistory().add(listSubtasks.get(id));
        return listSubtasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (listTasks.containsKey(task.getId())) {
            listTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (listEpics.containsKey(epic.getId())) {
            listEpics.put(epic.getId(), epic);
            defineStatusEpic(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (listSubtasks.containsKey(subtask.getId())) {
            listSubtasks.put(subtask.getId(), subtask);
            Epic epic = listEpics.get(subtask.getParentId());
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
    @Override
    public void deleteTaskById(int id) {
        if (listTasks.containsKey(id)) {
            listTasks.remove(id);
            historyManager.remove(id);
        }
    }
    @Override
    public void deleteSubtaskById(int id) {
        if (listSubtasks.containsKey(id)) {
            Subtask subtask = (Subtask) listSubtasks.get(id);
            int epicId = subtask.getParentId();
            listSubtasks.remove(id);
            Epic epic = (Epic) listEpics.get(epicId);
            epic.getSubtasksId().remove((Object) subtask.getId());
            updateEpic(epic);
            historyManager.remove(id);
        }
    }
    @Override
    public void deleteEpicById(int id) {
        if (listEpics.containsKey(id)) {
            Epic epic = listEpics.get(id);
            if (!epic.getSubtasksId().isEmpty()) {
                for (int value : epic.getSubtasksId()) {
                    listSubtasks.remove(value);
                    historyManager.remove(value);
                }
                epic.getSubtasksId().clear();
            }
            listEpics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getListSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        if (!epic.getSubtasksId().isEmpty()) {
            for (int id : epic.getSubtasksId()) {
                subtasksOfEpic.add(listSubtasks.get(id));
            }
        }
        return subtasksOfEpic;
    }

    @Override
    public void defineStatusEpic(Epic epic) {
        ArrayList<Subtask> list = getListSubtasksOfEpic(epic);
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
