package com.github.nik9000.nnfa.heap;

import java.util.Locale;

/**
 * A transition from one state to another.
 */
public class Transition {
	private byte from;
	private byte to;
	private State next;

	public Transition(byte from, byte to, State next) {
		this.from = from;
		this.to = to;
		this.next = next;
	}

	public Transition() {
	}

	public byte from() {
		return from;
	}

	public Transition from(byte from) {
		this.from = from;
		return this;
	}

	public byte to() {
		return to;
	}

	public Transition to(byte to) {
		this.to = to;
		return this;
	}

	public State next() {
		return next;
	}

	public Transition next(State next) {
		this.next = next;
		return this;
	}

	@Override
	public String toString() {
		if (from == to) {
			return String.format(Locale.ROOT, "%x --> %s", from, next);
		}
		return String.format(Locale.ROOT, "[%x,%x] --> %s", from, to, next);
	}
}
