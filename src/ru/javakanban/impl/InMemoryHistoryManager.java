package ru.javakanban.impl;

import ru.javakanban.interfaces.*;
import ru.javakanban.model.*;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static ArrayList<Task> historyTasks = new ArrayList<>();

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
    public ArrayList<Task> getHistory() {
        return historyTasks;
    }
}
