package com.github.nik9000.nnfa.builder;

/**
 * Operations for accepting a single byte or character.
 */
public abstract class AbstractByteOperations<Self extends AbstractByteOperations<Self>> extends
        AbstractBaseOperations<Self> {
    /**
     * Consume a byte. Convenience version accepting ints and casting.
     */
    public Self byteRange(int min, int max) {
        return byteRange((byte) min, (byte) max);
    }

    /**
     * Consume a byte.
     */
    public Self aByte(byte b) {
        return byteRange(b, b);
    }

    /**
     * Consume a byte. Convenience takes an int and casts it.
     */
    public Self aByte(int b) {
        return aByte((byte) b);
    }

    /**
     * Consume any byte.
     */
    public Self anyByte() {
        return byteRange(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
}
