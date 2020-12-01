package queue;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Queue {
    // Let queue - sequence of elements
    // size - number of items in the queue
    // queue[size - 1] - the last element
    // queue[0] - the first element

    // pre: element != null
    // post: size' = size + 1 && queue[size] = element && (∀i = 0..size - 1: queue'[i] == queue[i])
    void enqueue(Object element);

    // pre: size > 0
    // post: R = queue[0] && size' = size && (∀i = 0..size - 1: queue'[i] == queue[i])
    Object element();

    // pre: size > 0
    // post: R = queue[0] && size' = size - 1 && (∀i = 1..size' - 1: queue'[i] == queue[i])
    Object dequeue();

    // pre: true
    // post: R = size && size' = size && (∀i = 0..size - 1: queue'[i] == queue[i])
    int size();

    // pre: true
    // post: R = (size == 0) && size' = size && (∀i = 0..size - 1: queue'[i] == queue[i])
    boolean isEmpty();

    // pre: true
    // post: size' = 0 && queue' = {}
    void clear();

    // :NOTE: Можно вернуть все элементы
    // pre: predicate != null
    // post: R - sequence of elements && R = {q_i1, q_i2,... q_iR.size} && 0 <= i1 < i2 < .. iR.size <= size &&
    // ∀i = 0..size - 1: i in {i1, i2,... iR.size} if (predicate(queue[i]) == true) && queue' = queue
    Queue filter(Predicate<Object> predicate);

    // pre: function != null
    // :NOTE: Забыто function(queues[i]) != null
    // post: R - sequence of elements && R.size == size && (∀i = 0..size - 1: R[i] = function(queue[i]) && queue'[i] == queue[i])
    Queue map(Function<Object, Object> function);
}
