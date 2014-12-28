package com.github.nik9000.nnfa.heap;

import java.io.IOException;
import java.io.Writer;

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

    @Override
    public String toString() {
        return dotLabel() + "->" + next().id();
    }

    public void toDot(Writer w, int from) throws IOException {
        w.append("  ").append(Integer.toString(from));
        w.append(" -> ").append(Integer.toString(next.id()));
        w.append(" [label=\"").append(dotLabel()).append("\"]\n");
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

    /**
     * @return label used in .toString and .toDot.
     */
    protected abstract String dotLabel();
}
