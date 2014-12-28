package com.github.nik9000.nnfa.builder;

import java.util.ArrayList;
import java.util.List;

import com.github.nik9000.nnfa.heap.ByteMatchingTransition;
import com.github.nik9000.nnfa.heap.EpsilonTransition;
import com.github.nik9000.nnfa.heap.Nfa;
import com.github.nik9000.nnfa.heap.State;

/**
 * Builds NFAs.  When first built the NFA accepts nothing at all and states must be added to it.
 */
public final class NfaBuilder extends AbstractNfaBuilder<NfaBuilder> {
    private List<State> states = new ArrayList<>();

    public NfaBuilder() {
        states.add(new State(0));
    }

    @Override
    protected NfaBuilder self() {
        return this;
    }

    @Override
    protected Nfa buildInternalInternal() {
        return new Nfa(states.get(0));
    }

    @Override
    public NfaBuilder accepts() {
        states.get(from()).accepts(true);
        return this;
    }

    @Override
    public NfaBuilder byteRange(byte min, byte max) {
        states.get(from()).transitions().add(new ByteMatchingTransition(min, max, states.get(to())));
        return this;
    }

    @Override
    public NfaBuilder epsilon() {
        states.get(from()).transitions().add(new EpsilonTransition(states.get(to())));
        return this;
    }

    @Override
    protected int newState() {
        states.add(new State(states.size()));
        return states.size() - 1;
    }

    @Override
    public String toString() {
        return states.toString();
    }
}
