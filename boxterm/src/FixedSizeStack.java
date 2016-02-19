import java.util.Stack;

@SuppressWarnings("serial")
public class FixedSizeStack<T> extends Stack<T> {
    private int size;
    private int discarded;

    public FixedSizeStack(int size) {
        super();
        this.size = size;
        discarded = 0;
    }

    public int getTotalSize() {
        return this.size() + discarded;
    }

    @Override
    public T push(T object) {
        while (this.size() >= size) {
            this.remove(0);
            discarded++;
        }
        return super.push(object);
    }
}
