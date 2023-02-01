package manager;

import tasks.*;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static LinkedList<Task> historyTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (historyTasks.size() == 10) {
            historyTasks.remove(0);
            historyTasks.add(9, task);
        } else {
            historyTasks.add(task);
        }
    }

    @Override
    public LinkedList<Task> getHistory() {

        return historyTasks;
    }
}
