package com.github.nik9000.nnfa.heap;

/**
 * Epsilon transition.
 */
public class EpsilonTransition extends AbstractTransition {
    public EpsilonTransition(State next) {
        super(next);
    }

    @Override
    public boolean matches(int offset, byte[] target) {
        return true;
    }

    @Override
    public boolean advances() {
        return false;
    }

    @Override
    public String toString() {
        return "->" + next();
    }
}
