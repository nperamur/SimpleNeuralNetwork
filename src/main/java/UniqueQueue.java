import java.util.HashSet;
import java.util.LinkedList;

public class UniqueQueue<T> extends LinkedList<T> {
    private HashSet<T> set = new HashSet<>();


    @Override
    public boolean add(T t) {
        if (!set.contains(t)) {
            set.add(t);
            super.add(t);
            return true;
        }
        return false;
    }

    @Override
    public T pop() {
        T popped = super.pop();
        set.remove(popped);
        return popped;
    }
}
