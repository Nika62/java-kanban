package model;
public class Subtask extends Task {
    private int parentId;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public Subtask(int parentId, String name, String description, statusList status) {
        super(name, description, status);
        this.parentId = parentId;
    }

    public Subtask(int parentId, String name, String description) {
        super(name, description);
        this.parentId = parentId;

    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                " parentId= " + parentId + '\'' +
                ", id= " + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
