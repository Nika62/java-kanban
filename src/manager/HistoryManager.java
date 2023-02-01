package manager;

import tasks.*;

import java.util.*;

public interface HistoryManager {
    void add(Task task);

    LinkedList<Task> getHistory();
}
