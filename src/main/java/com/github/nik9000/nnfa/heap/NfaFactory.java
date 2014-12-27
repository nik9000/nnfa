package com.github.nik9000.nnfa.heap;

import java.nio.charset.Charset;

/**
 * Builds NFAs.  All methods are named after what is accepted by the NFA.
 */
public class NfaFactory {
    public Nfa string(String str) {
        return string(str.getBytes(Charset.forName("utf-8")));
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

    public Nfa character(int c) {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        // TODO handle multi-byte characters
        nfa.initial().transitions().add(new ByteMatchingTransition((byte)c, (byte)c, accept));
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
