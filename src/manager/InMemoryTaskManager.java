package manager;

import tasks.*;

import java.util.*;
import java.util.stream.*;

import static tasks.Task.StatusList.*;


public class InMemoryTaskManager implements TaskManager {
    protected int taskId = 0;
    protected static final HashMap<Integer, Epic> listEpics = new HashMap<>();
    protected static final HashMap<Integer, Task> listTasks = new HashMap<>();
    protected static final HashMap<Integer, Subtask> listSubtasks = new HashMap<>();
    protected static final Set<Task> sortedList = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            return t2.getStartTime().isBefore(t1.getStartTime()) ? 1 : -1;
        }
    });
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

    public List getPrioritizedTasks() {
        List<Task> tasks = listTasks.values()
                .stream()
                .filter(e -> e.getStartTime() == null)
                .collect(Collectors.toList());

        List<Subtask> sub = listSubtasks.values()
                .stream()
                .filter(e -> e.getStartTime() == null)
                .collect(Collectors.toList());

        ArrayList prioritizedTasks = new ArrayList();

        prioritizedTasks.addAll(sortedList);
        prioritizedTasks.addAll(tasks);
        prioritizedTasks.addAll(sub);
        return prioritizedTasks;
    }

    private void addToSortedList(Task task) {
        if (Objects.nonNull(task.getStartTime())) {
            sortedList.add(task);
        }
    }

    @Override
    public void saveTaskAndEpic(Task task) {
        if (Objects.nonNull(task)) {
            assignId(task);
            if (task instanceof Epic) {
                listEpics.put(task.getId(), (Epic) task);
                defineStatusEpic((Epic) task);
            } else {
                addToSortedList(task);
                listTasks.put(task.getId(), task);
            }
        }
    }


    @Override
    public void saveSubtask(Subtask subtask, Epic epic) {
        if (Objects.nonNull(subtask) && Objects.nonNull(epic)) {
            assignId(subtask);
            subtask.setParentId(epic.getId());
            addToSortedList(subtask);
            epic.addSubtask(subtask);
            listSubtasks.put(subtask.getId(), subtask);
            updateEpic(epic);
        }
    }


    @Override
    public ArrayList<Epic> getListAllEpic() {
        ArrayList<Epic> epics = new ArrayList<>();
        if (!listEpics.isEmpty()) {
            for (Epic epic : listEpics.values()) {
                epics.add(epic);
            }
        }
        return epics;
    }

    @Override
    public ArrayList<Subtask> getListAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (!listSubtasks.isEmpty()) {
            for (Subtask subtask : listSubtasks.values()) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    @Override
    public ArrayList<Task> getListAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!listTasks.isEmpty()) {
            for (Task task : listTasks.values()) {
                tasks.add(task);
            }
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
            for (Epic epic : listEpics.values()) {
                epic.getSubtasks().clear();
                updateEpic(epic);
            }
        }
        return listSubtasks;
    }

    @Override
    public Task getTaskById(int id) {
        if (listTasks.containsKey(id)) {
            Managers.getDefaultHistory().add(listTasks.get(id));
        }
        return listTasks.getOrDefault(id, null);

    }

    @Override
    public Epic getEpicById(int id) {
        if (listEpics.containsKey(id)) {
            Managers.getDefaultHistory().add(listEpics.get(id));
        }
        return listEpics.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (listSubtasks.containsKey(id)) {
            Managers.getDefaultHistory().add(listSubtasks.get(id));
        }
        return listSubtasks.getOrDefault(id, null);
    }

    @Override
    public void updateTask(Task task) {
        if (Objects.nonNull(task) && listTasks.containsKey(task.getId())) {
            sortedList.remove(listTasks.get(task.getId()));
            sortedList.add(task);
            listTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (Objects.nonNull(epic) && listEpics.containsKey(epic.getId())) {
            listEpics.put(epic.getId(), epic);
            defineStatusEpic(epic);
        }
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        if (Objects.nonNull(subtask) && listSubtasks.containsKey(subtask.getId())) {
            sortedList.remove(listSubtasks.get(subtask.getId()));
            sortedList.add(subtask);
            listSubtasks.put(subtask.getId(), subtask);
            Epic epic = listEpics.get(subtask.getParentId());
            epic.updateSubtask(subtask);
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
            ;
            Epic epic = listEpics.get(listSubtasks.get(id).getParentId());
            epic.removeSubtask(listSubtasks.get(id));
            listSubtasks.remove(id);
            updateEpic(epic);
            historyManager.remove(id);
        }
    }
    @Override
    public void deleteEpicById(int id) {
        if (listEpics.containsKey(id)) {
            Epic epic = listEpics.get(id);
            if (!epic.getSubtasks().isEmpty()) {
                for (Subtask subtask : epic.getSubtasks()) {
                    listSubtasks.remove(subtask.getId());
                    historyManager.remove(subtask.getId());
                }
                epic.getSubtasks().clear();
            }
            listEpics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getListSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        if (!epic.getSubtasks().isEmpty()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasksOfEpic.add(subtask);
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

