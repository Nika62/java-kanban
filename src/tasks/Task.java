package tasks;

import java.time.*;

import static tasks.Task.StatusList.*;
import static tasks.TypeTask.*;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected StatusList status;
    protected int duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public enum StatusList {
        NEW,
        IN_PROGRESS,
        DONE,
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusList getStatus() {
        return status;
    }

    public void setStatus(StatusList status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = NEW;
        this.startTime = null;
        this.endTime = null;
    }

    public Task(int id, String name, String description, StatusList status, LocalDateTime startTime, int duration, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(String name, String description, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = NEW;
        this.endTime = startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        String task = id + "," + TASK + "," + name + "," + description + "," + status + ",";
        task = startTime != null ? task + startTime.toString() + "," + duration + "," : task + null + "," + duration + ",";
        task = endTime != null ? task + endTime.toString() : task + null;
        return task;
    }

}
