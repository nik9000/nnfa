package com.github.nik9000.nnfa.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * A state.
 */
public class State {
	private final List<AbstractTransition> transitions = new ArrayList<AbstractTransition>();
	private boolean accepts = false;

	public List<AbstractTransition> transitions() {
		return transitions;
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
		transitions.addAll(state.transitions);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("(");
		if (accepts) {
			b.append("(");
		}
		boolean first = true;
		for (AbstractTransition transition: transitions) {
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
