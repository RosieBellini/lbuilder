import java.util.Stack;

/**
 * A simple wrapper class to make a Stack of fixed size. If the FixedSizeStack
 * exceeds the given size, the oldest contents are discarded. Keeps track of
 * how long the FixedSizeStack *would* be if items weren't discarded.
 */
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
