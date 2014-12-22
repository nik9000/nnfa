package com.github.nik9000.nnfa.heap;

/**
 * Transition that matches the beginning of a string.
 */
public class AtBeginningTransition extends AbstractTransition {
    public AtBeginningTransition(State next) {
        super(next);
    }

    @Override
    public boolean matches(int offset, byte[] target) {
        return offset == 0;
    }

    @Override
    public boolean advances() {
        return false;
    }

    @Override
    public String toString() {
        return "^->" + next();
    }
}
