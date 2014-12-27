package com.github.nik9000.nnfa.heap;

import static org.hamcrest.Matchers.not;

import java.util.Random;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that only matches if an NFA accepts a string.
 */
public class AcceptsMatcher extends TypeSafeMatcher<Nfa> {
    public static AcceptsMatcher accepts(String str) {
        return new AcceptsMatcher(str, true, true);
    }

    public static AcceptsMatcher accepts(byte[] bytes) {
        return new AcceptsMatcher(bytes, true, true);
    }

    private final Object target;
    private final boolean startAnchored;
    private final boolean endAnchored;

    public AcceptsMatcher(Object target, boolean startAnchored, boolean endAnchored) {
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

    /**
     * Negates this matches if pattern is non-empty. Use this to handle cases
     * where you want to test random patterns including empty string.
     */
    public Matcher<Nfa> notIfNonEmpty(String pattern) {
        if (pattern.isEmpty()) {
            return this;
        }
        return not(this);
    }

    @Override
    protected void describeMismatchSafely(Nfa item, Description mismatchDescription) {
        mismatchDescription.appendText("but didn't (was ");
        mismatchDescription.appendValue((startAnchored ? "^" : "") + item
                + (endAnchored ? "$" : ""));
        mismatchDescription.appendText(")");
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("should accept ");
        description.appendValue(target);
    }

    @Override
    protected boolean matchesSafely(Nfa nfa) {
        if (target instanceof String) {
            return nfa.accepts((String)target, startAnchored, endAnchored);
        }
        if (target instanceof byte[]) {
            return nfa.accepts((byte[])target, startAnchored, endAnchored);
        }
        throw new IllegalStateException("Target of unknown type:  " + target.getClass());
    }
}
