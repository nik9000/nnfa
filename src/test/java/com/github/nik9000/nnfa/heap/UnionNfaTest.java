package com.github.nik9000.nnfa.heap;

import static com.github.nik9000.nnfa.heap.AcceptsMatcher.accepts;
import static org.hamcrest.Matchers.not;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

public class UnionNfaTest extends LuceneTestCase {
    private final NfaFactory factory = new NfaFactory();

    @Test
    public void or() {
        Nfa nfa = factory.string("candle");
        nfa.union(factory.string("light"));
        assertThat(nfa, accepts("candle"));
        assertThat(nfa, accepts("light"));
        assertThat(nfa, not(accepts("lemmon")));
    }
}
