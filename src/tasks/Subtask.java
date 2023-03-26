package tasks;

import java.time.*;

import static tasks.TypeTask.*;

public class Subtask extends Task {
    protected int parentId;


    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public Subtask(int id, String name, String description, StatusList status, LocalDateTime startTime, int duration, LocalDateTime endTime, int parentId) {
        super(id, name, description, status, startTime, duration, endTime);
        this.parentId = parentId;
    }

    public Subtask(int parentId, String name, String description) {
        super(name, description);
        this.parentId = parentId;
    }

    public Subtask(int parentId, String name, String description, int duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return id + "," + SUBTASK + "," + name + "," + description + "," + status +
                "," + startTime.toString() + "," + duration + "," + endTime.toString() + "," + parentId;
    }
}
