package manager;

import tasks.*;

import java.util.*;
public class InMemoryHistoryManager<T extends Task> implements HistoryManager {
    private static InMemoryHistoryManager<Task> historyTasks = new InMemoryHistoryManager<>();
    public static HashMap<Integer, Node<Task>> idAndNode = new HashMap<>();
    private Node<Task> headNode;
    private Node<Task> lastNode;
    private int size = 0;

    public void linkLast(Task task) {

        if (headNode == null) {
            headNode = new Node<>(task);
            lastNode = headNode;
        } else {
            Node<Task> oldLastNode = lastNode;
            lastNode = new Node<>(task);
            oldLastNode.setNext(lastNode);
            lastNode.setPrev(oldLastNode);
        }
        size++;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> arrayList = new ArrayList<>();
        if (size > 0) {
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
        size--;
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        boolean isContainsTask = idAndNode.containsKey(taskId);
        if (isContainsTask) {
            remove(taskId);
        }
        historyTasks.linkLast(task);
        idAndNode.put(taskId, historyTasks.lastNode);

    }

    @Override
    public List<Task> getHistory() {

        return historyTasks.getTasks();
    }

    @Override
    public void remove(int id) {
        boolean isId = idAndNode.get(id) != null ? true : false;

        if (isId) {
            Node<Task> nodeForDelete = idAndNode.get(id);
            historyTasks.removeNode(nodeForDelete);
            idAndNode.remove(id);
        }
    }
}
