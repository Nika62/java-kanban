package tasks;

import java.time.*;
import java.util.*;

import static tasks.Task.StatusList.*;
import static tasks.TypeTask.*;

public class Epic extends Task {
    protected ArrayList<Subtask> subtasks = new ArrayList();


    public Epic(String name, String description) {
        super(name, description);
        this.status = NEW;
        this.startTime = null;
        this.endTime = null;
        this.duration = 0;
    }

    public Epic(int id, String name, String description, StatusList status, LocalDateTime startTime, int duration, LocalDateTime endTime, ArrayList<Subtask> subtasks) {
        super(id, name, description, status, startTime, duration, endTime);
        this.subtasks = subtasks;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks() {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {

        if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            if (subtask.startTime.isBefore(startTime)) {
                startTime = subtask.startTime;
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.endTime;
            }
        } else {
            startTime = subtask.startTime;
            endTime = subtask.endTime;
        }
        duration = duration + subtask.duration;
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        if (subtasks.contains(subtask)) {
            subtasks.remove(subtask.id);
            duration = duration - subtask.duration;
            updateEpicTime(subtask);
        }
    }

    public void updateSubtask(Subtask subtask) {
        int index = 0;
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtask.equals(subtasks.get(i))) {
                index = i;
            }
        }
        subtasks.remove(index);
        subtasks.add(index, subtask);
        updateEpicTime(subtask);
        calculateDuration();
    }

    private void calculateDuration() {
        if (!subtasks.isEmpty()) {
            duration = 0;
            for (Subtask subtask : subtasks) {
                duration = duration + subtask.duration;
            }
        }
    }

    private void updateEpicTime(Subtask subtask) {
        if (startTime.equals(subtask.startTime)) {
            startTime = findNewTimeEpic("startTime");
        }
        if (startTime.equals(subtask.startTime)) {
            startTime = findNewTimeEpic("endTime");
        }
    }

    private LocalDateTime findNewTimeEpic(String epicNameField) {
        LocalDateTime newDate = null;

        for (Subtask subtask : subtasks) {
            if (epicNameField == "startTime") {
                newDate = subtasks.get(0).startTime;
                if (subtask.startTime.isBefore(newDate)) {
                    newDate = subtask.startTime;
                }
            } else {
                newDate = subtasks.get(0).endTime;
                if (subtask.endTime.isAfter(newDate)) {
                    newDate = subtask.endTime;
                }
            }
        }
        return newDate;
    }

    @Override
    public String toString() {
        String epic = id + "," + EPIC + "," + name + "," + description + "," + status + ",";
        epic = startTime != null ? epic + startTime.toString() + "," + duration + "," : epic + null + "," + duration + ",";
        epic = endTime != null ? epic + endTime.toString() + "," + listSubtasksIdToString() : epic + null + "," + listSubtasksIdToString();
        return epic;
    }

    private String listSubtasksIdToString() {
        StringBuilder listId = new StringBuilder();
        String listToString = "";
        if (subtasks != null && !subtasks.isEmpty()) {
            for (Subtask subtask : subtasks) {
                listId.append(subtask.getId() + " ");
            }
            listToString = String.valueOf(listId);
        }
        return listToString;
    }
}






