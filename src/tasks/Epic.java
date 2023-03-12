package tasks;

import java.util.*;

import static tasks.TypeTask.*;

public class Epic extends Task {
    protected ArrayList<Integer> subtasksId = new ArrayList();


    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, StatusList status) {
        super(name, description, status);
    }

    public Epic(int id, String name, String description, StatusList status, ArrayList<Integer> subtasksId) {
        super(id, name, description, status);
        this.subtasksId = subtasksId;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId() {
        this.subtasksId = subtasksId;
    }

    @Override
    public String toString() {
        return id + "," + EPIC + "," + name + "," + description + "," + status + "," + listSubtasksIdToString();
    }

    private String listSubtasksIdToString() {
        StringBuilder listId = new StringBuilder();
        String listToString = "";
        if (subtasksId != null) {
            for (int subtaskId : subtasksId) {
                listId.append(subtaskId + " ");
            }
            listToString = String.valueOf(listId);
        }
        return listToString;
    }
}
