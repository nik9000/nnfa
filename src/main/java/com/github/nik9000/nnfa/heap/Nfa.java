package com.github.nik9000.nnfa.heap;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * NFA implementation that does everything on the Java heap. This implementation
 * make almost no effort to be efficient, just somewhat sensible.
 */
public class Nfa {
    private final State initial;

    public Nfa(State initial) {
        this.initial = initial;
    }

    public State initial() {
        return initial;
    }

    public boolean accepts(String target, boolean startAnchored, boolean endAnchored) {
        return accepts(target.getBytes(StandardCharsets.UTF_8), startAnchored, endAnchored);
    }

    /**
     * Walks the nfa breadth first checking if target matches.
     */
    public boolean accepts(byte[] target, boolean startAnchored, boolean endAnchored) {
        if (initial.accepts() && (!startAnchored || !endAnchored || target.length == 0)) {
            return true;
        }
        List<AcceptWork> currentWork = new LinkedList<AcceptWork>();
        int offsetForNextInitial = 0;
        List<AcceptWork> nextWork = new ArrayList<AcceptWork>(1);
        while (offsetForNextInitial <= target.length || !currentWork.isEmpty()) {
            if (offsetForNextInitial <= target.length) {
                currentWork.add(new AcceptWork(initial, offsetForNextInitial));
                // If we're start anchored we only ever want the one initial state.
                offsetForNextInitial += startAnchored ? target.length + 1 : 1;
            }
            for (AcceptWork current : currentWork) {
                for (AbstractTransition transition : current.state.transitions()) {
                    if (!transition.matches(current.position, target)) {
                        continue;
                    }
                    int nextPosition = transition.advances() ? current.position + 1 : current.position;
                    if (transition.next().accepts() && (!endAnchored || nextPosition >= target.length)) {
                        return true;
                    }
                    nextWork.add(new AcceptWork(transition.next(), nextPosition));
                }
            }
            List<AcceptWork> tmp = currentWork;
            currentWork = nextWork;
            nextWork = tmp;
            nextWork.clear();
        }
        return false;
    }

    private static class AcceptWork {
        private final State state;
        private final int position;

        private AcceptWork(State state, int position) {
            this.state = state;
            this.position = position;
        }

        @Override
        public String toString() {
            return position + "@" + state;
        }
    }

    @Override
    public String toString() {
        return initial.toString();
    }

    /**
     * Returns a script you can paste into your console to render this nfa as an
     * svg.
     */
    public String toSvgScript() {
        Writer w = new StringWriter();
        try {
            toSvgScript(w);
        } catch (IOException e) {
            throw new RuntimeException("Shouldn't happen", e);
        }
        return w.toString();
    }

    /**
     * Builds a script you can paste into your console to render this nfa as an
     * svg.
     */
    public void toSvgScript(Writer w) throws IOException {
        w.write("dot -Tsvg > dot.svg <<__DOT__\n");
        toDot(w);
        w.write("__DOT__");
    }

    /**
     * Converts this NFA to a dot diagram.
     */
    public void toDot(Writer w) throws IOException {
        w.write("digraph Automaton {\n");
        w.write("  rankdir = LR\n");
        w.write("  initial [shape=plaintext,label=\"\"]\n");
        w.write("  initial -> 0\n");

        Set<Integer> seenStates = new HashSet<Integer>();
        toDot(w, initial, seenStates);
        w.append("}\n");
    }

    /**
     * Writes the dot for a state and its transitions if it hasn't yet been seen.
     */
    private void toDot(Writer w, State state, Set<Integer> seen) throws IOException {
        if (!seen.add(state.id())) {
            return;
        }
        String idString = Integer.toString(state.id());
        w.append("  ").append(idString);
        String shape = "circle";
        if (state.accepts()) {
            shape = "doublecircle";
        }
        w.append(" [shape=").append(shape).append(",label=\"").append(idString).append("\"]\n");
        for (AbstractTransition transition : state.transitions()) {
            transition.toDot(w, state.id());
            toDot(w, transition.next(), seen);
        }
    }

    public void intersect(Nfa nfa) {
        throw new UnsupportedOperationException("Haven't built it yet");
    }

    public void union(Nfa parseCharClass) {
        throw new UnsupportedOperationException("Haven't built it yet");
    }

    public void concat(Nfa nfa, boolean optional) {
        throw new UnsupportedOperationException("Haven't built it yet");        
    }

    public void complement() {
        throw new UnsupportedOperationException("Haven't built it yet");        
    }

    public void optional() {
        throw new UnsupportedOperationException("Moved to NfaBuilder");
    }

    /**
     * Make this nfa repeat at least min times and at most max times.
     * @param max > 0 means it _must_ repeat this many times.  < 0 means any number of times.
     */
    public void makeRepeat(int min, int max) {
        // Min is non-optional concat with a clone of yourself.
        // Max is optional concat with a clone of yourself.
        // max < 0 is add an epsilon transition from all accept states back to the initial state
        throw new UnsupportedOperationException("Haven't built it yet");
    }
}
