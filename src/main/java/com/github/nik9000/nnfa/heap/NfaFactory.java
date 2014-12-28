package com.github.nik9000.nnfa.heap;

/**
 * Builds NFAs.  All methods are named after what is accepted by the NFA.
 */
public class NfaFactory {
    public Nfa characterRange(int from, int to) {
        Nfa nfa = new Nfa(new State(0));
        State accept = new State(1).accepts(true);
        // TODO handle multi-byte characters
        nfa.initial().transitions().add(new ByteMatchingTransition((byte)from, (byte)to, accept));
        return nfa;        
    }

    public Nfa interval(int min, int max, int digits) {
        throw new UnsupportedOperationException();
    }
}
