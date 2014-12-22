package com.github.nik9000.nnfa.heap;

public class EpsilonTransition extends AbstractTransition {
	public EpsilonTransition(State next) {
		super(next);
	}

	@Override
	public boolean isSatisfied(int offset, byte[] target) {
		return true;
	}

	@Override
	public boolean advances() {
		return false;
	}
}
