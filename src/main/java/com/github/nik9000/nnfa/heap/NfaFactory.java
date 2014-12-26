package com.github.nik9000.nnfa.heap;

import java.nio.charset.Charset;

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

    public Nfa acceptsEmptyString() {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        return nfa;
    }

    public Nfa acceptsNothing() {
        return new Nfa();
    }

    public Nfa acceptsAnyString() {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        nfa.initial().transitions().add(new EpsilonTransition(nfa.initial()));
        return nfa;
    }

    public Nfa acceptsAnyChar() {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        // TODO accept valid utf8
        nfa.initial().transitions().add(new ByteMatchingTransition(Byte.MIN_VALUE, Byte.MAX_VALUE, accept));
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
