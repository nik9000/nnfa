package com.github.nik9000.nnfa.heap;

import static com.github.nik9000.nnfa.heap.AcceptsMatcher.accepts;
import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

import com.github.nik9000.nnfa.builder.NfaBuilder;

public class UnionTest extends LuceneTestCase {
    @Test
    public void basic() {
        Nfa nfa = new NfaBuilder().mark().string("candle").mark().string("light").union().build();
        assertThat(nfa, accepts("candle"));
        assertThat(nfa, accepts("light"));
        assertThat(nfa, not(accepts("lemmon")));
    }

    @Test
    public void unicode() {
        String str1 = randomRealisticUnicodeString(random(), 1, 20);
        String str2 = randomRealisticUnicodeString(random(), 1, 20);
        Nfa nfa = new NfaBuilder().mark().string(str1).mark().string(str2).union().build();
        assertThat(nfa, accepts(str1));
        assertThat(nfa, accepts(str2));
        assertThat(nfa, not(accepts(str1 + str2)));
        assertThat(nfa, not(accepts(str2 + str1)));
        assertThat(nfa, not(accepts(str1 + str1)));
        assertThat(nfa, not(accepts(str2 + str2)));
    }

    @Test
    public void prefix() {
        Nfa nfa = new NfaBuilder().string("candle").mark().string(" flame").mark().string(" light").union().build();
        assertThat(nfa, accepts("candle flame"));
        assertThat(nfa, accepts("candle light"));
        assertThat(nfa, not(accepts("candle")));
        assertThat(nfa, not(accepts(" flame")));
        assertThat(nfa, not(accepts(" light")));
    }

    @Test
    public void suffix() {
        Nfa nfa = new NfaBuilder().mark().string("best").mark().string("state").union().string(" actor").build();
        assertThat(nfa, accepts("best actor"));
        assertThat(nfa, accepts("state actor"));
        assertThat(nfa, not(accepts("best")));
        assertThat(nfa, not(accepts("state")));
        assertThat(nfa, not(accepts(" actor")));
    }

    @Test
    public void empty() {
        Nfa nfa = new NfaBuilder().string("candle").mark().mark().string(" light").union().build();
        assertThat(nfa, accepts("candle"));
        assertThat(nfa, accepts("candle light"));
        assertThat(nfa, not(accepts(" light")));
    }

    @Test
    public void single() {
        Nfa nfa = new NfaBuilder().mark().string("candle").union(1).build();
        assertThat(nfa, accepts("candle"));
        assertThat(nfa, not(accepts("lemmon")));
    }

    @Test
    public void many() {
        int total = 20;
        String prefix = randomRealisticUnicodeString(random());
        String suffix = randomRealisticUnicodeString(random());
        String[] option = new String[total];
        NfaBuilder builder = new NfaBuilder().string(prefix);
        for (int i = 0; i < total; i++) {
            option[i] = randomRealisticUnicodeString(random());
            builder.mark().string(option[i]);
        }
        builder.union(total).string(suffix);
        Nfa nfa = builder.build();
        for (int i = 0; i < total; i++) {
            assertThat(nfa, accepts(prefix + option[i] + suffix));
            assertThat(nfa, accepts(option[i] + suffix).notIfNonEmpty(prefix));
            assertThat(nfa, accepts(prefix + option[i]).notIfNonEmpty(suffix));
        }
    }
}
