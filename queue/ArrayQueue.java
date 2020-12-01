package queue;

import java.util.Arrays;
import java.util.function.Function;

public class ArrayQueue extends AbstractQueue{
    private int head = 0;
    private int tail = 0;
    private Object[] elements = new Object[8];

    public void enqueueImpl(final Object element) {
        if (size == elements.length) {
            resize();
        }
        elements[tail++] = element;
        tail %= elements.length;
    }

    private void resize() {
        elements = Arrays.copyOf(elements, 2 * size);
        if (head >= tail) {
            if (tail >= 0) {
                System.arraycopy(elements, 0, elements, size, tail);
            }
        }
        tail += size;

    }

    public Object elementImpl() {
        return elements[head];
    }

    public Object dequeueImpl() {
        final Object element = elements[head];
        head = (head + 1) % elements.length;
        return element;
    }

    public void clearImpl() {
        head = 0;
        tail = 0;
    }

    protected ArrayQueue makeQueue(final Function<Object, Object> function) {
        final ArrayQueue result = new ArrayQueue();
        for (int i = head; i < head + size; i++) {
            final Object element = elements[i % elements.length];
            // :NOTE: Можно было вынести в базовый класс
            final Object x = function.apply(element);
            if (x != null) {
                result.enqueue(x);
            }
            //
        }
        return result;
    }
}
