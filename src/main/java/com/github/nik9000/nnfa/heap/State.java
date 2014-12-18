package com.github.nik9000.nnfa.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * A state.
 */
public class State {
	private final List<Transition> standardTransitions = new ArrayList<Transition>();
	private final List<State> epsilonTransitions = new ArrayList<State>(1);
	private boolean accepts = false;

	public List<Transition> standardTransitions() {
		return standardTransitions;
	}

	public List<State> epsilonTransitions() {
		return epsilonTransitions;
	}

	public boolean accepts() {
		return accepts;
	}

	public State accepts(boolean accepts) {
		this.accepts = accepts;
		return this;
	}

	/**
	 * Combine the passed in state with this one.
	 */
	public void combine(State state) {
		accepts |= state.accepts;
		standardTransitions.addAll(state.standardTransitions);
		epsilonTransitions.addAll(state.epsilonTransitions);
	}

	public List<State> followStandardTransitions(byte b) {
		List<State> result = new ArrayList<State>();
		for (Transition transition: standardTransitions) {
			if (transition.from() <= b && b <= transition.to()) {
				result.add(transition.next());
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("(");
		if (accepts) {
			b.append("(");
		}
		boolean first = true;
		for (State state: epsilonTransitions) {
			if (first) {
				first = false;
			} else {
				b.append(", ");
			}
			b.append("-->").append(state);
		}
		for (Transition transition: standardTransitions) {
			if (first) {
				first = false;
			} else {
				b.append(", ");
			}
			b.append(transition);
		}
		b.append(")");
		if (accepts) {
			b.append(")");
		}
		return b.toString();
	}
}
