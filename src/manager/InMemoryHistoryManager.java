package manager;

import tasks.*;

import java.util.*;
public class InMemoryHistoryManager<T extends Task> implements HistoryManager {
    private static InMemoryHistoryManager<Task> historyTasks = new InMemoryHistoryManager<>();
    private static HashMap<Integer, Node<Task>> nodeMap = new HashMap<>();
    private Node<Task> headNode;
    private Node<Task> lastNode;

    private void linkLast(Task task) {

        if (headNode == null) {
            headNode = new Node<>(task);
            lastNode = headNode;
        } else {
            Node<Task> oldLastNode = lastNode;
            lastNode = new Node<>(task);
            oldLastNode.setNext(lastNode);
            lastNode.setPrev(oldLastNode);
        }
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> arrayList = new ArrayList<>();
        if (headNode != null) {
            arrayList.add(headNode.getDate());
            Node<Task> nextNode = headNode.getNext();

            while (nextNode != null) {
                arrayList.add(nextNode.getDate());
                nextNode = nextNode.getNext();
            }
        }
        return arrayList;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> nodePrev = node.getPrev();
        Node<Task> nodeNext = node.getNext();

        if (nodePrev != null && nodeNext != null) {
            nodePrev.setNext(nodeNext);
            nodeNext.setPrev(nodePrev);
            node.setPrev(null);
            node.setNext(null);
        } else if (nodePrev == null && nodeNext != null) {
            headNode = nodeNext;
            headNode.setPrev(null);
            node.setNext(null);
        } else if (nodeNext == null && nodePrev != null) {
            lastNode = nodePrev;
            lastNode.setNext(null);
            node.setPrev(null);
        }
        node.setDate(null);
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if (nodeMap.containsKey(taskId)) {
            remove(taskId);
        }
        historyTasks.linkLast(task);
        nodeMap.put(taskId, historyTasks.lastNode);

    }

    @Override
    public List<Task> getHistory() {

        return historyTasks.getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodeMap.get(id) != null) {
            Node<Task> nodeForDelete = nodeMap.get(id);
            historyTasks.removeNode(nodeForDelete);
        }
    }
}
