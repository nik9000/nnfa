package com.github.nik9000.nnfa.heap;

import static com.github.nik9000.nnfa.heap.AcceptsMatcher.accepts;
import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

import com.github.nik9000.nnfa.builder.NfaBuilder;

/**
 * Test for AbstractUnaryOperations.
 */
public class UnaryOperationsTest extends LuceneTestCase {
    @Test
    public void optionalByte() {
        Nfa nfa = new NfaBuilder().codePoint("a").mark().codePoint("b").optional().build();
        assertThat(nfa, accepts("a"));
        assertThat(nfa, accepts("ab"));
        assertThat(nfa, not(accepts("aa")));
        assertThat(nfa, not(accepts("aab")));
    }

    @Test
    public void optionalEmpty() {
        Nfa nfa = new NfaBuilder().codePoint("a").mark().optional().build();
        assertThat(nfa, accepts("a"));
        assertThat(nfa, not(accepts("ab")));
        assertThat(nfa, not(accepts("aa")));
    }

    @Test
    public void optionalOptional() {
        Nfa nfa = new NfaBuilder().mark().codePoint("a").optional().mark().codePoint("a").optional().build();
        assertThat(nfa, accepts(""));
        assertThat(nfa, accepts("a"));
        assertThat(nfa, accepts("aa"));
        assertThat(nfa, not(accepts("ab")));
        assertThat(nfa, not(accepts("aaa")));
    }

    @Test
    public void optionalRequired() {
        Nfa nfa = new NfaBuilder().mark().codePoint("a").optional().codePoint("a").build();
        assertThat(nfa, accepts("a"));
        assertThat(nfa, accepts("aa"));
        assertThat(nfa, not(accepts("ab")));
        assertThat(nfa, not(accepts("aaa")));
    }

    @Test
    public void unicode() {
        Nfa nfa = new NfaBuilder().mark().anyCodePoint().optional().codePoint("a").build();
        assertThat(nfa, accepts("a"));
        assertThat(nfa, accepts("aa"));
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 1) + "a"));
        assertThat(nfa, not(accepts("ab")));
        assertThat(nfa, not(accepts("aaa")));
    }

    @Test
    public void unicodeExact() {
        String c = randomRealisticUnicodeString(random(), 1, 1);
        Nfa nfa = new NfaBuilder().mark().codePoint(c).optional().codePoint("a").build();
        assertThat(nfa, accepts(c + "a"));
        assertThat(nfa, not(accepts("ab")));
        assertThat(nfa, not(accepts("aaa")));
    }
}
