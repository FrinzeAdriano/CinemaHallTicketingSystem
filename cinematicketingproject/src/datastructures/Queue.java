package datastructures;

public class Queue<T> {
    private LinkedList<T> list = new LinkedList<T>();

    // add to the back
    public void enqueue(T data) {
        list.insertAtEnd(data);
    }

    // remove and return the front item, or null if the queue is empty
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T data = list.get(0);
        list.delete(0);
        return data;
    }

    // look at the front item without removing it, or null if empty
    public T peek() {
        return list.get(0);
    }

    public boolean isEmpty() {
        return list.getHead() == null;
    }

    public int size() {
        return list.size();
    }

    // lets a controller or view walk the queue front to back
    public Node<T> getFront() {
        return list.getHead();
    }
}

