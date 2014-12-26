package com.github.nik9000.nnfa.heap;

import java.util.Random;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that only matches if an NFA accepts a string.
 */
public class AcceptsMatcher extends TypeSafeMatcher<Nfa> {
    public static AcceptsMatcher accepts(String str) {
        return new AcceptsMatcher(str, true, true);
    }

    private final String target;
    private final boolean startAnchored;
    private final boolean endAnchored;

    public AcceptsMatcher(String target, boolean startAnchored, boolean endAnchored) {
        this.target = target;
        this.startAnchored = startAnchored;
        this.endAnchored = endAnchored;
    }

    public AcceptsMatcher unAnchored() {
        return new AcceptsMatcher(target, false, false);
    }

    public AcceptsMatcher startUnanchored() {
        return new AcceptsMatcher(target, false, endAnchored);
    }

    public AcceptsMatcher endUnanchored() {
        return new AcceptsMatcher(target, startAnchored, false);
    }

    public AcceptsMatcher randomAnchoring(Random random) {
        return new AcceptsMatcher(target, random.nextBoolean(), random.nextBoolean());
    }

    @Override
    protected void describeMismatchSafely(Nfa item, Description mismatchDescription) {
        mismatchDescription.appendText("but didn't (was ");
        mismatchDescription.appendValue((startAnchored ? "^" : "") + item + (endAnchored ? "$" : ""));
        mismatchDescription.appendText(")");
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("should accept ");
        description.appendValue(target);
    }

    @Override
    protected boolean matchesSafely(Nfa nfa) {
        return nfa.accepts(target, startAnchored, endAnchored);
    }
}
