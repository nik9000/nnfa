package com.github.nik9000.nnfa.heap;

public class AtEndTransition extends AbstractTransition {
	public AtEndTransition(State next) {
		super(next);
	}

	@Override
	public boolean isSatisfied(int offset, byte[] target) {
		return target.length == offset;
	}

	@Override
	public boolean advances() {
		return false;
	}

	@Override
	public String toString() {
		return "$->" + next();
	}
}
