package com.github.nik9000.nnfa.builder;

import com.github.nik9000.nnfa.heap.Nfa;

/**
 * Lowest level operations for building NFAs.
 */
public abstract class AbstractBaseOperations<Self extends AbstractBaseOperations<Self>> {
    /**
     * The source for the next operation.  Starts at 0 which all implementations must understand as the initial state in the NFA.
     */
    private int from;
    /**
     * The destination for the next operation.  Starts at -1 meaning create a new state for the next destination.
     */
    private int to = -1;
    /**
     * The destination for the previous operation.  Starts at -1 meaning invalid.
     */
    private int lastTo = -1;

    /**
     * Build the nfa and mark the last state as accepts.
     */
    public final Nfa build() {
        accepts();
        return buildNoAccept();
    }

    /**
     * Build the nfa without marking the last state as accepts.
     */
    public final Nfa buildNoAccept() {
        return buildInternal();
    }

    /**
     * Override the default "from" for the next operation. The default "from" is
     * the "to".
     */
    public final Self from(int from) {
        this.from = from;
        return self();
    }

    /**
     * Overrides the default "to" for the next operation. The default "to" is to
     * create a new state.
     */
    public final Self to(int to) {
        this.to = to;
        return self();
    }

    /**
     * Get the source state for the next operation. Unless overridden this is
     * the destination of the last operation. When overridden calling this will
     * return the override and clear it so the next call will again default.
     */
    protected final int from() {
        if (from >= 0) {
            int result = from;
            from = -1;
            return result;
        }
        assert lastTo >= 0 : "There hasn't been a last operation so we can't default to its destination!";
        return lastTo;
    }

    /**
     * Get the destination state for the next operation. If to
     * has not been set since last calling this then this will create a new
     * state and return it.
     * 
     * Note: if you want to call this method make sure you call from() first
     * and save its value as calling this method can alter its default.
     */
    protected final int to() {
        if (to >= 0) {
            lastTo = to;
            to = -1;
        } else {
            lastTo = newState();
        }
        return lastTo;
    }

    /**
     * Return yourself cast Self.  Used to prevent unchecked casts.
     */
    protected abstract Self self();

    /**
     * Build the NFA from stored state.
     */
    protected abstract Nfa buildInternal();

    /**
     * Build a new state.
     */
    protected abstract int newState();

    /**
     * Marks that last state as accepting.
     */
    public abstract Self accepts();

    /**
     * Consume any byte in a range.
     */
    public abstract Self byteRange(byte min, byte max);

    /**
     * Construct an epsilon transition.
     */
    public abstract Self epsilon();
}
