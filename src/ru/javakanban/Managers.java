package ru.javakanban;

import ru.javakanban.impl.*;
import ru.javakanban.interfaces.*;

public class Managers {
    public static TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
