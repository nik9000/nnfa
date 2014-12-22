package com.github.nik9000.nnfa.heap;

/**
 * State transition.
 */
abstract class AbstractTransition {
    private final State next;

    public AbstractTransition(State next) {
        this.next = next;
    }

    /**
     * @return the state this transitions into
     */
    public State next() {
        return next;
    }

    /**
     * @return does the provided offset into the provided target move us to the
     *         next() state?
     */
    public abstract boolean matches(int offset, byte[] target);

    /**
     * @return does moving to the next state advance offset or leave it alone?
     */
    public abstract boolean advances();
}
