package queue;

import java.util.function.Function;

public class LinkedQueue extends AbstractQueue {
    private Node head;
    private Node tail;

    public void enqueueImpl(Object element) {
        Node tmp = new Node (element, null);
        if (size == 0) {
            head = tmp;
        } else {
            tail.next = tmp;
        }
        tail = tmp;
    }

    public Object elementImpl() {
        return head.value;
    }

    public Object dequeueImpl() {
        Object result = head.value;
        head = head.next;
        return result;
    }

    public void clearImpl() {
        tail = new Node(null, null);
        head = new Node(null, tail);
    }

    private class Node {
        private Object value;
        private Node next;

        public Node(Object value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    protected LinkedQueue makeQueue(Function<Object, Object> function) {
        LinkedQueue result = new LinkedQueue();
        Node it = head;
        while(it != null) {
            Object x = function.apply(it.value);
            if (x != null) {
                result.enqueue(x);
            }
            it = it.next;
        }
        return result;
    }
}
