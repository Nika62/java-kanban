package ru.javakanban.interfaces;

import ru.javakanban.model.Task;

import java.util.ArrayList;

public interface HistoryManager<T extends Task> {
    void add(T task);

    ArrayList<T> getHistory();
}
