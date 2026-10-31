package datastructures;
import controllers.*;
import models.*;
import repositories.*;
import view.*;

public class LinkedList<T> {
    private Node<T> head;

    public void insertAtStart(T data) {
        Node<T> node = new Node<T>(data);
        node.setNext(head);
        head = node;
    }

    public void insertAtEnd(T data) {
        if (head == null) {
            insertAtStart(data);
            return;
        }
        Node<T> n = head;
        while (n.getNext() != null) {
            n = n.getNext();
        }
        n.setNext(new Node<T>(data));
    }

    public boolean insertAtIndex(int index, T data) {
        if (index < 0) {
            return false;
        }
        if (index == 0) {
            insertAtStart(data);
            return true;
        }
        Node<T> n = head;
        for (int i = 0; i < index - 1 && n != null; i++) {
            n = n.getNext();
        }
        if (n == null) {
            return false;               // index is past the end
        }
        Node<T> node = new Node<T>(data);
        node.setNext(n.getNext());
        n.setNext(node);
        return true;
    }

    public boolean delete(int index) {
        if (index < 0 || head == null) {
            return false;
        }
        if (index == 0) {
            head = head.getNext();
            return true;
        }
        Node<T> n = head;
        for (int i = 0; i < index - 1 && n != null; i++) {
            n = n.getNext();
        }
        if (n == null || n.getNext() == null) {
            return false;               // no node at that index
        }
        n.setNext(n.getNext().getNext());
        return true;
    }

    public T get(int index) {
        if (index < 0) {
            return null;
        }
        Node<T> n = head;
        for (int i = 0; i < index && n != null; i++) {
            n = n.getNext();
        }
        return (n == null) ? null : n.getData();
    }

    public boolean contains(T data) {
        Node<T> n = head;
        while (n != null) {
            if (n.getData().equals(data)) {
                return true;
            }
            n = n.getNext();
        }
        return false;
    }

    public boolean remove(T data) {
        Node<T> prev = null;
        Node<T> n = head;
        while (n != null) {
            if (n.getData().equals(data)) {
                if (prev == null) {
                    head = n.getNext();         // removing the first node
                } else {
                    prev.setNext(n.getNext());
                }
                return true;
            }
            prev = n;
            n = n.getNext();
        }
        return false;
    }

    public void display() {
        Node<T> n = head;
        while (n != null) {
            System.out.println(n.getData());
            n = n.getNext();
        }
    }

    public int size() {
        int count = 0;
        Node<T> n = head;
        while (n != null) {
            count++;
            n = n.getNext();
        }
        return count;
    }

    public Node<T> getHead() { return head; }
}
