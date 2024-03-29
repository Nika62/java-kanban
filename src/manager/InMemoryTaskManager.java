package manager;

import tasks.*;

import java.util.*;
import java.util.stream.*;

import static tasks.Task.StatusList.*;


public class InMemoryTaskManager implements TaskManager {
    protected static int taskId = 0;
    protected static final HashMap<Integer, Epic> listEpics = new HashMap<>();
    protected static final HashMap<Integer, Task> listTasks = new HashMap<>();
    protected static final HashMap<Integer, Subtask> listSubtasks = new HashMap<>();
    protected static final TreeSet<Task> sortedList = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            return t1.compareTo(t2);
        }
    });
    static HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        taskId = 0;
    }

    public HashMap<Integer, Epic> getListEpics() {
        return listEpics;
    }

    public HashMap<Integer, Task> getListTasks() {
        return listTasks;
    }

    public HashMap<Integer, Subtask> getListSubtasks() {
        return listSubtasks;
    }

    public TreeSet<Task> getSortedList() {
        return sortedList;
    }


    protected int assignId(Task newTask) {
        newTask.setId(++taskId);
        return taskId;
    }

    protected void checkFreeTime(Task newTask) throws ManagerSaveException {
        try {
            if (Objects.nonNull(newTask.getStartTime())) {
                for (Task task : sortedList) {
                    if (newTask.getStartTime().equals(task.getStartTime()) || newTask.getStartTime().equals(task.getEndTime()) &&
                            newTask.getEndTime().equals(task.getStartTime()) || newTask.getEndTime().equals(task.getEndTime())) {
                        throw new ManagerSaveException("Задача не сохранена, время занято");
                    }
                }
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    public List getPrioritizedTasks() {
        ArrayList<Task> prioritizedTasks = new ArrayList();
        List<Task> tasks;
        List<Subtask> sub;

        prioritizedTasks.addAll(sortedList);

        if (!listTasks.isEmpty()) {
            tasks = listTasks.values()
                    .stream()
                    .filter(e -> e.getStartTime() == null)
                    .collect(Collectors.toList());

            prioritizedTasks.addAll(tasks);
        }
        if (!listSubtasks.isEmpty()) {
            sub = listSubtasks.values()
                    .stream()
                    .filter(e -> e.getStartTime() == null)
                    .collect(Collectors.toList());

            prioritizedTasks.addAll(sub);
        }

        return prioritizedTasks;
    }

    private void addToSortedList(Task task) {
        if (Objects.nonNull(task.getStartTime())) {
            sortedList.add(task);
        }
    }

    @Override
    public boolean saveTaskAndEpic(Task task) {
        if (Objects.nonNull(task)) {
            if (task instanceof Epic) {
                assignId(task);
                listEpics.put(task.getId(), (Epic) task);
                defineStatusEpic((Epic) task);
            } else {
                checkFreeTime(task);
                assignId(task);
                addToSortedList(task);
                listTasks.put(task.getId(), task);
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean saveSubtask(Subtask subtask, Epic epic) {
        if (Objects.nonNull(subtask) && Objects.nonNull(epic) && getListAllEpic().contains(epic) && subtask.getParentId() == epic.getId()) {
            checkFreeTime(subtask);
            assignId(subtask);
            subtask.setParentId(epic.getId());
            addToSortedList(subtask);
            epic.addSubtask(subtask);
            listSubtasks.put(subtask.getId(), subtask);
            updateEpic(epic);
            return true;
        }
        return false;
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
            for (Task task : listTasks.values()) {
                if (Objects.nonNull(task.getStartTime())) {
                    sortedList.remove(task);
                }
                historyManager.remove(task.getId());
            }
            listTasks.clear();

        }
        return listTasks;
    }

    @Override
    public HashMap<Integer, Epic> deleteAllEpics() {
        if (!listEpics.isEmpty()) {
            if (!listSubtasks.isEmpty()) {
                deleteAllSubtasks();
            }
            for (int id : listEpics.keySet()) {
                historyManager.remove(id);
            }
            listEpics.clear();
        }
        return listEpics;
    }

    @Override
    public HashMap<Integer, Subtask> deleteAllSubtasks() {
        if (!listSubtasks.isEmpty()) {
            for (Epic epic : listEpics.values()) {
                if (Objects.nonNull(epic.getSubtasks())) {
                    epic.getSubtasks().clear();
                    updateEpic(epic);
                }
            }
            for (Subtask subtask : listSubtasks.values()) {
                if (Objects.nonNull(subtask.getStartTime())) {
                    sortedList.remove(subtask);
                }
                historyManager.remove(subtask.getId());
            }
            }
            listSubtasks.clear();
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
    public boolean updateTask(Task task) {
        if (Objects.nonNull(task) && listTasks.containsKey(task.getId())) {
            if (Objects.nonNull(task.getStartTime())) {
                checkFreeTime(task);
                sortedList.remove(listTasks.get(task.getId()));
                sortedList.add(task);
            }
            listTasks.put(task.getId(), task);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (Objects.nonNull(epic) && listEpics.containsKey(epic.getId())) {
            listEpics.put(epic.getId(), epic);
            defineStatusEpic(epic);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (Objects.nonNull(subtask) && listSubtasks.containsKey(subtask.getId())) {
            sortedList.remove(listSubtasks.get(subtask.getId()));
            if (Objects.nonNull(subtask.getStartTime())) {
                checkFreeTime(subtask);
                sortedList.add(subtask);
            }
            listSubtasks.put(subtask.getId(), subtask);
            Epic epic = listEpics.get(subtask.getParentId());
            epic.updateSubtask(subtask);
            updateEpic(epic);
            return true;
        }
        return false;
    }

    @Override
    public void deleteTaskById(int id) {
        if (listTasks.containsKey(id)) {
            historyManager.remove(id);
            if (Objects.nonNull(listTasks.get(id).getStartTime())) {
                sortedList.remove(listTasks.get(id));
            }
            listTasks.remove(id);
        }
    }
    @Override
    public void deleteSubtaskById(int id) {
        if (listSubtasks.containsKey(id)) {
            Epic epic = listEpics.get(listSubtasks.get(id).getParentId());
            epic.removeSubtask(listSubtasks.get(id));
            if (Objects.nonNull(listSubtasks.get(id).getStartTime())) {
                sortedList.remove(listSubtasks.get(id));
            }
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
                    if (Objects.nonNull(subtask.getStartTime())) {
                        sortedList.remove(subtask);
                    }
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


    protected void defineStatusEpic(Epic epic) {
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

