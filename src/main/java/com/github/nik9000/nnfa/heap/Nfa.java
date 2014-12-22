package com.github.nik9000.nnfa.heap;

import java.nio.charset.Charset;
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

	public boolean accepts(String target) {
		return accepts(target.getBytes(Charset.forName("utf-8")));
	}

	/**
	 * Walks the nfa breadth first checking if target matches.
	 */
	public boolean accepts(byte[] target) {
		if (initial.accepts()) {
			return true;
		}
		List<AcceptWork> currentWork = new LinkedList<AcceptWork>();
		int offsetForInitial = 0;
		List<AcceptWork> nextWork = new ArrayList<AcceptWork>(1);
		while (offsetForInitial <= target.length || !currentWork.isEmpty()) {
			if (offsetForInitial <= target.length) {
				currentWork.add(new AcceptWork(initial, offsetForInitial));
				offsetForInitial += 1;
			}
			for (AcceptWork current : currentWork) {
				for (AbstractTransition transition : current.state.transitions()) {
					if (!transition.isSatisfied(current.position, target)) {
						continue;
					}
					if (transition.next().accepts()) {
						return true;
					}
					nextWork.add(new AcceptWork(transition.next(), transition.advances() ? current.position + 1 : current.position));
				}
			}
			List<AcceptWork> tmp = currentWork;
			currentWork = nextWork;
			nextWork = tmp;
			nextWork.clear();
		}
		return false;
	}

	private class AcceptWork {
		private State state;
		private int position;

		private AcceptWork(State state, int position) {
			this.state = state;
			this.position = position;
		}
	}

	@Override
	public String toString() {
		return initial.toString();
	}
}
