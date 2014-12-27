package com.github.nik9000.nnfa.heap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * NFA implementation that does everything on the Java heap. This implementation
 * make almost no effort to be efficient, just somewhat sensible.
 */
public class Nfa {
    private final State initial = new State();

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
                    if (transition.next().accepts() && (!endAnchored || current.position >= target.length - 1)) {
                        return true;
                    }
                    nextWork.add(new AcceptWork(transition.next(),
                            transition.advances() ? current.position + 1 : current.position));
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
    }

    @Override
    public String toString() {
        return initial.toString();
    }

    /**
     * Union this nfa with another in place.
     */
    public void union(Nfa nfa) {
        /*
         * This implementation is kind of cheating - because we walk both NFAs
         * in a parallel we can just smash them together, no reduction required.
         */
        initial().intersect(nfa.initial());
    }

    public void intersect(Nfa nfa) {
        throw new UnsupportedOperationException("Haven't built it yet");
    }

    public void concat(Nfa nfa, boolean optional) {
        throw new UnsupportedOperationException("Haven't built it yet");        
    }

    public void complement() {
        throw new UnsupportedOperationException("Haven't built it yet");        
    }

    public void optional() {
        initial.accepts(true);
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
