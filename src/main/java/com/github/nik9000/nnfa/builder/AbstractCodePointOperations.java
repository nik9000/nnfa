package com.github.nik9000.nnfa.builder;

import java.util.Locale;

/**
 * Operations on code points.
 */
public abstract class AbstractCodePointOperations<Self extends AbstractCodePointOperations<Self>> extends AbstractByteOperations<Self> {
    /**
     * Consume a code point encoded in utf-8.
     */
    public final Self codePoint(int c) {
        if (c <= 0x7f) {
            return aByte(c);
        }
        int from = from();
        int to = to();
        if (c <= 0x07ff) {
            return from(from).aByte(c >>> 6 | 0xc0).to(to).aByte(c & 0x3f | 0x80);
        }
        if (c <= 0xffff) {
            return from(from).aByte(c >>> 12 | 0xe0).aByte(c >>> 6 & 0x3f | 0x80).to(to).aByte(c & 0x3f | 0x80);
        }
        if (c <= 0x1FFFFF) {
            return from(from).aByte(c >>> 18 | 0xf0).aByte(c >>> 12 & 0x3f | 0x80).aByte(c >>> 6 & 0x3f | 0x80).to(to).aByte(c & 0x3f | 0x80);
        }
        throw new IllegalArgumentException(String.format(Locale.ROOT, "Invalid codepoint:  %x", c));
    }

    /**
     * Convenience version of codePoint that accepts a string containing a single code point.
     */
    public final Self codePoint(String cp) {
        int cpCount = cp.codePointCount(0, cp.length());
        if (cpCount != 1) {
            throw new IllegalArgumentException("Only a single code point allowed!  You provided " + cp + " which is " + cpCount + "!");
        }
        return codePoint(cp.codePointAt(0));
    }

    /**
     * Consume any valid utf-8 encoding of a single code point.
     */
    public final Self anyCodePoint() {
        int from = from();
        int to = to();
        from(from).byteRange(0xf0, 0xf7);
        int acceptsTwoBytes = byteRange(0x80, 0xbf).from();
        int acceptsOneByte = byteRange(0x80, 0xbf).from();
        to(to).byteRange(0x80, 0xbf);

        from(from).to(acceptsTwoBytes).byteRange(0xe0, 0xef);
        from(from).to(acceptsOneByte).byteRange(0xc0, 0xdf);
        return from(from).to(to).byteRange(0x00, 0x7f);
    }
}
