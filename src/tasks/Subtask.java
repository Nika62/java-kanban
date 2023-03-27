package tasks;

import java.time.*;
import java.util.*;

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
        String sub = id + "," + SUBTASK + "," + name + "," + description + "," + status + ",";
        sub = startTime != null ? sub + startTime.toString() + "," + duration + "," : sub + null + "," + duration + ",";
        sub = endTime != null ? sub + endTime.toString() + "," + parentId : sub + null + "," + parentId;
        return sub;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return parentId == subtask.parentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentId);
    }
}
