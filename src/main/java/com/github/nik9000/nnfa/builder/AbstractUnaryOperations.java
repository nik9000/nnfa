package com.github.nik9000.nnfa.builder;

/**
 * Operations on a single marked sub-NFA.
 */
public abstract class AbstractUnaryOperations<Self extends AbstractUnaryOperations<Self>> extends AbstractMarkOperations<Self> {
    /**
     * Make the last marked NFA optional.
     */
    public Self optional() {
        int oldFrom = from();
        int oldInitial = initial();
        popMarks(1);
        int from = from();
        from(from).to(oldInitial).epsilon();
        return from(from).to(oldFrom).epsilon();
    }
}
