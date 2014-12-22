package com.github.nik9000.nnfa.heap;

public class AtBeginningTransition extends AbstractTransition {
	public AtBeginningTransition(State next) {
		super(next);
	}

	@Override
	public boolean isSatisfied(int offset, byte[] target) {
		return offset == 0;
	}

	@Override
	public boolean advances() {
		return false;
	}

	@Override
	public String toString() {
		return "^->" + next();
	}
}
