package manager;

import tasks.*;

public class Node<T extends Task> {
    private Task date;
    private Node<tasks.Task> prev;
    private Node<tasks.Task> next;

    public Node(Task date) {
        this.date = date;
        this.prev = null;
        this.next = null;
    }

    public Node(Task dateHead, Task dateLast) {
        Node<Task> headNode = new Node<Task>(dateHead);
        Node<Task> lastNode = new Node<Task>(dateLast);
        headNode.next = lastNode;
        lastNode.prev = headNode;
    }


    public void setPrev(Node<Task> prev) {
        this.prev = prev;
    }

    public void setNext(Node<Task> next) {
        this.next = next;
    }

    public void setDate(Task date) {
        this.date = date;
    }

    public tasks.Task getDate() {
        return date;
    }

    public Node<Task> getPrev() {
        return prev;
    }

    public Node<Task> getNext() {
        return next;
    }
}