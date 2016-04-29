package team1;

import java.util.Stack;

/**
 * A simple wrapper class to make a Stack of fixed size. If the FixedSizeStack
 * exceeds the given size, the oldest contents are discarded.
 */
@SuppressWarnings("serial")
public class FixedSizeStack<T> extends Stack<T> {
    private int size;

    public FixedSizeStack(int size) {
        super();
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public void reset(T object) {
        while (this.size() > 0) {
            super.pop();
        }
        super.push(object);
    }

    @Override
    public T push(T object) {
        while (this.size() >= size) {
            this.remove(0);
        }
        return super.push(object);
    }
}
