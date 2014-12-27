package com.github.nik9000.nnfa.heap;

import java.nio.charset.StandardCharsets;

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
