package model;

import static model.Task.statusList.NEW;

public class Task {
    private int id;
    private String name;
    private String description;
    private statusList status;

    public enum statusList {
        NEW,
        IN_PROGRESS,
        DONE,
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

    public statusList getStatus() {
        return status;
    }

    public void setStatus(statusList status) {
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = NEW;
    }

    public Task(String name, String description, statusList status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
