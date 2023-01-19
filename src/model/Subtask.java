package model;

public class Subtask extends Task {
    private int parentId;

    public int getParentId() {
        return parentId;
    }
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public Subtask(String name, String description, String status ) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", parentId=" + parentId +'\'' +
                '}';
    }
}
