package model;
import java.util.ArrayList;
public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList();
    public Epic(String name, String description, String status) {

        super(name, description, status);
    }
    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription()+ '\'' +
                ", status='" + getStatus()+ '\'' +
                ", subtasksId=" + subtasksId + '\''+
                '}';
    }
}
