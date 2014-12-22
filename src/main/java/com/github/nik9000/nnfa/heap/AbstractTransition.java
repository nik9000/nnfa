package com.github.nik9000.nnfa.heap;

abstract class AbstractTransition {
	private final State next;

	public AbstractTransition(State next) {
		this.next = next;
	}

	public State next() {
		return next;
	}

	public abstract boolean isSatisfied(int offset, byte[] target);

	public abstract boolean advances();
}
