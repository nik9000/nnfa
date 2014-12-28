package com.github.nik9000.nnfa.builder;

import java.nio.charset.StandardCharsets;

/**
 * Operations accepts a run of specific bytes or characters.
 */
public abstract class AbstractSequentialOperations<Self extends AbstractSequentialOperations<Self>> extends AbstractCodePointOperations<Self> {
    /**
     * Consume a sequence of bytes.
     */
    public final Self bytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            aByte(bytes[i]);
        }
        return self();
    }

    /**
     * Consume a utf-8 encoded string.
     */
    public final Self string(String str) {
        return bytes(str.getBytes(StandardCharsets.UTF_8));
    }
}
