package com.github.nik9000.nnfa.heap;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Nfa {
	private final State initial = new State();

	public State initial() {
		return initial;
	}

	public boolean accepts(String target, boolean endAnchored) {
		return accepts(target.getBytes(Charset.forName("utf-8")), endAnchored);
	}

	public boolean accepts(byte[] target, boolean endAnchored) {
		// TODO startAnchored
		List<State> currentStates = new ArrayList<State>(1);
		currentStates.add(initial);
		List<State> nextStates = new ArrayList<State>(1);
		int position = 0;
		while (!currentStates.isEmpty() && position < target.length) {
			for (State current : currentStates) {
				for (State next : current.nextStates(target[position])) {
					if (next.accepts() && (!endAnchored || target.length - 1 == position)) {
						return true;
					}
					nextStates.add(next);
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
}
