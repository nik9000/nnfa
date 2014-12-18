package com.github.nik9000.nnfa.heap;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * NFA implementation that does everything on the Java heap.
 */
public class Nfa {
	private final State initial = new State();

	public State initial() {
		return initial;
	}

	public boolean accepts(String target, boolean endAnchored) {
		return accepts(target.getBytes(Charset.forName("utf-8")), endAnchored);
	}

	/**
	 * Walks the nfa breadth first checking if target matches.
	 */
	public boolean accepts(byte[] target, boolean endAnchored) {
		// TODO startAnchored
		List<State> currentStates = new ArrayList<State>(1 + initial.epsilonTransitions().size());
		currentStates.add(initial);
		currentStates.addAll(initial.epsilonTransitions());
		for (State state: currentStates) {
			if (state.accepts() && (!endAnchored || target.length == 0)) {
				return true;
			}
		}
		int position = 0;
		List<State> nextStates = new ArrayList<State>(currentStates.size());
		while (!currentStates.isEmpty() && position < target.length) {
			for (State current : currentStates) {
				for (State next : current.followStandardTransitions(target[position])) {
					if (next.accepts() && (!endAnchored || target.length - 1 == position)) {
						return true;
					}
					for (State nextViaEpsilon: next.epsilonTransitions()) {
						if (nextViaEpsilon.accepts() && (!endAnchored || target.length - 1 == position)) {
							return true;
						}
					}
					nextStates.add(next);
					nextStates.addAll(next.epsilonTransitions());
				}
			}
			List<State> tmp = currentStates;
			currentStates = nextStates;
			nextStates = tmp;
			nextStates.clear();
			position++;
		}
		return false;
	}

	@Override
	public String toString() {
		return initial.toString();
	}
}
