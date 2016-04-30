package team1;

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
    private int numPushes;

    public FixedSizeStack(int size) {
        super();
        this.size = size;
        discarded = 0;
        numPushes = 0;
    }

    public int getTotalSize() {
        return this.size() + discarded;
    }

    public int getSize() {
        return this.size;
    }

    public int getNumPushes() {
        return numPushes;
    }

    public void reset(T object) {
        while (this.size() > 0) {
            super.pop();
        }
        super.push(object);
        numPushes = 0;
        discarded = 0;
    }

    @Override
    public T push(T object) {
        while (this.size() >= size) {
            this.remove(0);
            discarded++;
        }
        numPushes++;
        return super.push(object);
    }

    public T pop() {
        numPushes--;
        return super.pop();
    }
}
