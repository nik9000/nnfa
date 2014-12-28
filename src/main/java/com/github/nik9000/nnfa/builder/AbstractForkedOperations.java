package com.github.nik9000.nnfa.builder;


public abstract class AbstractForkedOperations<Self extends AbstractForkedOperations<Self>> extends AbstractMarkOperations<Self> {
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
        from(currentFrom).to(to).epsilon();
        return popMarks(clauses);
    }

    /**
     * Union the last two clauses.
     */
    public final Self union() {
        return union(2);
    }
}
