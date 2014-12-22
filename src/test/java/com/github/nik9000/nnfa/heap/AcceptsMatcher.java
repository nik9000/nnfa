package com.github.nik9000.nnfa.heap;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that only matches if an NFA accepts a string.
 */
public class AcceptsMatcher extends TypeSafeMatcher<Nfa> {
    private final String target;

    public AcceptsMatcher(String target) {
        this.target = target;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("should accept ").appendValue(target);

    }

    @Override
    protected boolean matchesSafely(Nfa nfa) {
        return nfa.accepts(target);
    }
}
