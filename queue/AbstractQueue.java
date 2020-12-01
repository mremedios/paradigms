package queue;


import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Object element() {
        assert size > 0;
        return elementImpl();
    }
    protected abstract Object elementImpl();

    public void enqueue(final Object element) {
        assert element != null;
        enqueueImpl(element);
        size++;
    }
    protected abstract void enqueueImpl(Object element);

    public Object dequeue() {
        assert size > 0;
        size--;
        return dequeueImpl();
    }
    protected abstract Object dequeueImpl();

    public void clear() {
        size = 0;
        clearImpl();
    }
    protected abstract void clearImpl();

    protected abstract Queue makeQueue(Function<Object, Object> function);

    public Queue filter(final Predicate<Object> predicate) {
        return makeQueue(o -> predicate.test(o) ? o: null);
    }

    public Queue map(final Function<Object, Object> function) {
        return makeQueue(function);
    }
}
