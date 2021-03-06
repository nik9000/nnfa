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
        assert from <= to : String.format(Locale.ROOT, "from > to (%s > %s)", from, to);
        this.from = from;
        this.to = to;
    }

    public ByteMatchingTransition(int from, int to, State next) {
        this((byte)from, (byte)to, next);
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
    protected String dotLabel() {
        if (from == to) {
            return formatByte(from);
        }
        return formatByte(from) + "-" + formatByte(to);
    }

    private String formatByte(byte b) {
        if ('a' <= b && b <= 'z') {
            return Character.toString((char)b);
        }
        if ('A' <= b && b <= 'Z') {
            return Character.toString((char)b);
        }
        return String.format(Locale.ROOT, "%x", b);
    }

    @Override
    public boolean advances() {
        return true;
    }
}
