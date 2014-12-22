package com.github.nik9000.nnfa.heap;

import java.util.Locale;

/**
 * A transition from one state to another.
 */
public class ByteMatchingTransition extends AbstractTransition {
    private final byte from;
    private final byte to;

    public ByteMatchingTransition(byte from, byte to, State next) {
        super(next);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(int offset, byte[] target) {
        return offset < target.length && from <= target[offset] && target[offset] <= to;
    }

    public byte from() {
        return from;
    }

    public byte to() {
        return to;
    }

    @Override
    public String toString() {
        if (from == to) {
            return String.format(Locale.ROOT, "%x->%s", from, next());
        }
        return String.format(Locale.ROOT, "%x,%x->%s", from, to, next());
    }

    @Override
    public boolean advances() {
        return true;
    }
}
