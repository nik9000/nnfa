package com.github.nik9000.nnfa.builder;

/**
 * Operations on more than a single marked sub-NFA.
 */
public abstract class AbstractForkedOperations<Self extends AbstractForkedOperations<Self>> extends AbstractUnaryOperations<Self> {
    /**
     * Union the last clauses clauses.
     */
    public final Self union(int clauses) {
        int currentFrom = from();
        int to = to();
        int from = fromAt(clauses - 1);
        for (int clause = clauses - 2; clause >= 0; clause--) {
            from(from).to(initialAt(clause)).epsilon();
            from(fromAt(clause)).to(to).epsilon();
        }
        from(from).to(initial()).epsilon();
        popMarks(clauses);
        return from(currentFrom).to(to).epsilon();
    }

    /**
     * Union the last two clauses.
     */
    public final Self union() {
        return union(2);
    }
}
