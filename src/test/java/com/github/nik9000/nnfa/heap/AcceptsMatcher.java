package com.github.nik9000.nnfa.heap;

import java.util.Random;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class AcceptsMatcher extends TypeSafeMatcher<Nfa> {
	private final String target;
	private boolean endAnchored = false;

	public AcceptsMatcher(String target) {
		this.target = target;
	}

	public AcceptsMatcher endAnchored(boolean endAnchored) {
		this.endAnchored = endAnchored;
		return this;
	}

	public AcceptsMatcher endAnchored(Random random) {
		return endAnchored(random.nextBoolean());
	}

	public AcceptsMatcher endAnchored() {
		return endAnchored(true);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("should accept ").appendValue(target);
		if (endAnchored) {
			description.appendText(" in end anchored mode");
		}
	}

	@Override
	protected boolean matchesSafely(Nfa nfa) {
		return nfa.accepts(target, endAnchored);
	}
}
