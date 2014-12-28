package com.github.nik9000.nnfa.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * A state.
 */
public class State {
	private final List<AbstractTransition> transitions = new ArrayList<AbstractTransition>();
	private final int id;
	private boolean accepts = false;

	public State(int id) {
	    this.id = id;
    }

	public List<AbstractTransition> transitions() {
		return transitions;
	}

	public boolean accepts() {
		return accepts;
	}

	public int id() {
	    return id;
	}

	public State accepts(boolean accepts) {
		this.accepts = accepts;
		return this;
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
