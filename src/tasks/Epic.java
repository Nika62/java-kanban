package tasks;

import java.util.*;
public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList();
    public Epic(String name, String description) {
        super(name, description);
    }
    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
    public void setSubtasksId() {
        this.subtasksId = subtasksId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtasksId=" + subtasksId + '\'' +
                '}';
    }
}
