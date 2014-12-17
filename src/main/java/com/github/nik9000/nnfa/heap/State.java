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

	public List<State> nextStates(byte b) {
		List<State> result = new ArrayList<State>();
		result.addAll(epsilonTransitions);
		for (Transition transition: standardTransitions) {
			if (transition.from() <= b && b <= transition.to()) {
				result.add(transition.next());
			}
		}
		return result;
	}
}
