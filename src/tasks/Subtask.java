package tasks;

import static tasks.TypeTask.*;

public class Subtask extends Task {
    protected int parentId;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public Subtask(int parentId, String name, String description, StatusList status) {
        super(name, description, status);
        this.parentId = parentId;
    }

    public Subtask(int parentId, String name, String description) {
        super(name, description);
        this.parentId = parentId;
    }

    public Subtask(int id, String name, String description, StatusList status, int parentId) {
        super(id, name, description, status);
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return id + "," + SUBTASK + "," + name + "," + description + "," + status + "," + parentId;
    }
}
