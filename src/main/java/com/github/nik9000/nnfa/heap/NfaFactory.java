package com.github.nik9000.nnfa.heap;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Builds NFAs.  All methods are named after what is accepted by the NFA.
 */
public class NfaFactory {
    public Nfa string(String str) {
        return string(str.getBytes(StandardCharsets.UTF_8));
    }

    public Nfa string(byte[] bytes) {
        Nfa nfa = new Nfa();
        State from = nfa.initial();
        State to = nfa.initial();
        for (int i = 0; i < bytes.length; i++) {
            to = new State();
            from.transitions().add(new ByteMatchingTransition(bytes[i], bytes[i], to));
            from = to;
        }
        to.accepts(true);
        return nfa;
    }

    public Nfa nothing() {
        return new Nfa();
    }

    public Nfa empty() {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        return nfa;
    }

    public Nfa anyByte() {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        nfa.initial().transitions().add(new ByteMatchingTransition(Byte.MIN_VALUE, Byte.MAX_VALUE, nfa.initial()));
        return nfa;
    }

    public Nfa anyChar() {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        State a1 = new State();
        State a2 = new State();
        State a3 = new State();

        nfa.initial().transitions().add(new ByteMatchingTransition(0x00, 0x7f, accept));
        nfa.initial().transitions().add(new ByteMatchingTransition(0xc0, 0xdf, a1));
        nfa.initial().transitions().add(new ByteMatchingTransition(0xe0, 0xef, a2));
        nfa.initial().transitions().add(new ByteMatchingTransition(0xf0, 0xf7, a3));
        a1.transitions().add(new ByteMatchingTransition(0x80, 0xbf, accept));
        a2.transitions().add(new ByteMatchingTransition(0x80, 0xbf, a1));
        a3.transitions().add(new ByteMatchingTransition(0x80, 0xbf, a2));

        return nfa;
    }

    /**
     * @return an NFA accepting the byte b
     */
    public Nfa aByte(byte b) {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        nfa.initial().transitions().add(new ByteMatchingTransition(b, b, nfa.initial()));
        return nfa;
    }

    /**
     * @return an NFA accepting the unicode codepoint c encoded in utf-8
     */
    public Nfa character(int c) {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        if (c <= 0x7f) {
            nfa.initial().transitions().add(new ByteMatchingTransition(c, c, accept));
            return nfa;
        }
        if (c <= 0x07ff) {
            State next = new State();
            byte c1 = (byte)(c >>> 6 | 0xc0);
            byte c2 = (byte)(c & 0x3f | 0x80);
            nfa.initial().transitions().add(new ByteMatchingTransition(c1, c1, next));
            next.transitions().add(new ByteMatchingTransition(c2, c2, accept));
            return nfa;
        }
        if (c <= 0xffff) {
            State next1 = new State();
            State next2 = new State();
            byte c1 = (byte)(c >>> 12 | 0xe0);
            byte c2 = (byte)(c >>> 6 & 0x3f | 0x80);
            byte c3 = (byte)(c & 0x3f | 0x80);
            nfa.initial().transitions().add(new ByteMatchingTransition(c1, c1, next1));
            next1.transitions().add(new ByteMatchingTransition(c2, c2, next2));
            next2.transitions().add(new ByteMatchingTransition(c3, c3, accept));
            return nfa;
        }
        if (c > 0x1FFFFF) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Invalid codepoint:  %x", c));
        }
        State next1 = new State();
        State next2 = new State();
        State next3 = new State();
        byte c1 = (byte)(c >>> 18 | 0xf0);
        byte c2 = (byte)(c >>> 12 & 0x3f | 0x80);
        byte c3 = (byte)(c >>> 6 & 0x3f | 0x80);
        byte c4 = (byte)(c & 0x3f | 0x80);
        nfa.initial().transitions().add(new ByteMatchingTransition(c1, c1, next1));
        next1.transitions().add(new ByteMatchingTransition(c2, c2, next2));
        next2.transitions().add(new ByteMatchingTransition(c3, c3, next3));
        next3.transitions().add(new ByteMatchingTransition(c4, c4, accept));
        return nfa;

    }

    public Nfa characterRange(int from, int to) {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        // TODO handle multi-byte characters
        nfa.initial().transitions().add(new ByteMatchingTransition((byte)from, (byte)to, accept));
        return nfa;        
    }

    public Nfa interval(int min, int max, int digits) {
        throw new UnsupportedOperationException();
    }
}
