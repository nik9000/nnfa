package com.github.nik9000.nnfa.builder;

import java.util.ArrayList;
import java.util.List;

import com.github.nik9000.nnfa.heap.Nfa;

public abstract class AbstractMarkOperations<Self extends AbstractMarkOperations<Self>> extends AbstractSequentialOperations<Self> {
    // TODO got to be a better list structure
    private final List<Integer> marks = new ArrayList<>();
    private int initial = 0;

    /**
     * The initial state of this sub-NFA.
     */
    protected final int initial() {
        return initial;
    }

    /**
     * Push the current NFA work on the mark stack and start a fresh one.
     */
    public final Self mark() {
        // TODO probably should support this
        if (!toIsDefault()) {
            throw new IllegalStateException("Can't mark when to has been overridden.");
        }
        marks.add(from());
        marks.add(initial());
        initial = newState();
        from(initial);
        return self();
    }

    /**
     * Pop markCount marks off the mark stack.
     */
    public final Self popMarks(int markCount) {
        for (int i = 0; i < markCount; i++) {
            marks.remove(marks.size() - 1);
            marks.remove(marks.size() - 1);
        }
        return self();
    }

    /**
     * The number of marks left in the mark stack.
     */
    public final int markCount() {
        return marks.size() / 2;
    }

    /**
     * Get the from at the specified index. 0 is the last mark, 1 is the next to
     * last mark, etc.
     */
    public final int fromAt(int index) {
        return marks.get(marks.size() - index * 2 - 2);
    }

    /**
     * Get the initial state at the specified index. 0 is the last mark, 1 is the next to
     * last mark, etc.
     */
    public final int initialAt(int index) {
        return marks.get(marks.size() - index * 2 - 1);
    }

    @Override
    protected final Nfa buildInternal() {
        if (markCount() != 0) {
            throw new IllegalStateException(markCount() + " marks left behind!");
        }
        return buildInternalInternal();
    }

    protected abstract Nfa buildInternalInternal();    
}
