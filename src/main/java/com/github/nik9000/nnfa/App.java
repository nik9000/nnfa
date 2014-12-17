package com.github.nik9000.nnfa;

import com.github.nik9000.nnfa.heap.Nfa;
import com.github.nik9000.nnfa.heap.State;
import com.github.nik9000.nnfa.heap.Transition;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Nfa nfa = new Nfa();
		State accept = new State().accepts(true);
		nfa.initial().standardTransitions().add(new Transition((byte)'a', (byte)'z', accept));
		System.err.println(nfa.accepts("a", true));
	}
}
