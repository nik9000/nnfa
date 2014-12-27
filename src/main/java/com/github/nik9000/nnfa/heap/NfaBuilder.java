package com.github.nik9000.nnfa.heap;

import java.util.Locale;

/**
 * Builds NFAs.  When first built the NFA accepts nothing at all and states must be added to it.
 */
public class NfaBuilder {
    private final Nfa nfa = new Nfa();
    private State lastAdded = nfa.initial();

    /**
     * Build the nfa and mark the last state as accepts.
     */
    public Nfa build() {
        accepts();
        return nfa;
    }

    /**
     * Build the nfa without marking the last state as accepts.
     */
    public Nfa buildNoMark() {
        return nfa;
    }

    /**
     * Marks that last state as accepting.
     */
    public NfaBuilder accepts() {
        lastAdded.accepts(true);
        return this;
    }

    /**
     * Consume any byte in a range.
     */
    public NfaBuilder byteRange(byte min, byte max) {
        State next = new State();
        lastAdded.transitions().add(new ByteMatchingTransition(min, max, next));
        lastAdded = next;
        return this;        
    }

    /**
     * Consume a byte.
     */
    public NfaBuilder aByte(byte b) {
        return byteRange(b, b);
    }

    /**
     * Consume a byte.  Convenience takes an int and casts it.
     */
    public NfaBuilder aByte(int b) {
        return aByte((byte)b);
    }

    /**
     * Consume any byte.
     */
    public NfaBuilder anyByte() {
        return byteRange(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    /**
     * Consume a code point encoded in utf-8.
     */
    public NfaBuilder codePoint(int c) {
        if (c <= 0x7f) {
            return aByte(c);
        }
        if (c <= 0x07ff) {
            return aByte(c >>> 6 | 0xc0).aByte(c & 0x3f | 0x80);
        }
        if (c <= 0xffff) {
            return aByte(c >>> 12 | 0xe0).aByte(c >>> 6 & 0x3f | 0x80).aByte(c & 0x3f | 0x80);
        }
        if (c <= 0x1FFFFF) {
            return aByte(c >>> 18 | 0xf0).aByte(c >>> 12 & 0x3f | 0x80).aByte(c >>> 6 & 0x3f | 0x80).aByte(c & 0x3f | 0x80);
        }
        throw new IllegalArgumentException(String.format(Locale.ROOT, "Invalid codepoint:  %x", c));
    }

    /**
     * Consume any valid utf-8 encoding of a single code point.
     */
    public NfaBuilder anyCodePoint() {
        State initial = lastAdded;
        State a1 = new State();
        State a2 = new State();
        State a3 = new State();
        lastAdded = new State();

        initial.transitions().add(new ByteMatchingTransition(0x00, 0x7f, lastAdded));
        initial.transitions().add(new ByteMatchingTransition(0xc0, 0xdf, a1));
        initial.transitions().add(new ByteMatchingTransition(0xe0, 0xef, a2));
        initial.transitions().add(new ByteMatchingTransition(0xf0, 0xf7, a3));
        a1.transitions().add(new ByteMatchingTransition(0x80, 0xbf, lastAdded));
        a2.transitions().add(new ByteMatchingTransition(0x80, 0xbf, a1));
        a3.transitions().add(new ByteMatchingTransition(0x80, 0xbf, a2));
        return this;
    }
}
