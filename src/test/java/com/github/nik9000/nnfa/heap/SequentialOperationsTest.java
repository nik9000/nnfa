package com.github.nik9000.nnfa.heap;

import static com.github.nik9000.nnfa.heap.AcceptsMatcher.accepts;
import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

import com.github.nik9000.nnfa.builder.NfaBuilder;

/**
 * Test for AbstractSequentialOperations.
 */
public class SequentialOperationsTest extends LuceneTestCase {
    @Test
    public void emptyString() {
        exactStringTestCase("");
    }

    @Test
    public void oneCharacter() {
        exactStringTestCase("a");
    }

    @Test
    public void twoCharacter() {
        exactStringTestCase("ab");
    }

    @Test
    public void unicodeCharacters() {
        exactStringTestCase(randomRealisticUnicodeString(random(), 1, 20));
    }

    private void exactStringTestCase(String str) {
        String strWithPrefix = randomRealisticUnicodeString(random(), 1, 20) + str;
        String strWithSuffix = str + randomRealisticUnicodeString(random(), 1, 20);
        String strWithPrefixAndSuffix = strWithPrefix
                + randomRealisticUnicodeString(random(), 1, 20);

        Nfa nfa = new NfaBuilder().string(str).build();
        assertThat(nfa, accepts(str));
        assertThat(nfa, not(accepts(strWithPrefix)));
        assertThat(nfa, not(accepts(strWithSuffix)));
        assertThat(nfa, not(accepts(strWithPrefixAndSuffix)));

        assertThat(nfa, accepts(str).endUnanchored());
        assertThat(nfa, accepts(strWithPrefix).endUnanchored().notIfNonEmpty(str));
        assertThat(nfa, accepts(strWithSuffix).endUnanchored());
        assertThat(nfa, accepts(strWithPrefixAndSuffix).endUnanchored().notIfNonEmpty(str));

        assertThat(nfa, accepts(str).startUnanchored());
        assertThat(nfa, accepts(strWithPrefix).startUnanchored());
        assertThat(nfa, accepts(strWithSuffix).startUnanchored().notIfNonEmpty(str));
        assertThat(nfa, accepts(strWithPrefixAndSuffix).startUnanchored().notIfNonEmpty(str));

        assertThat(nfa, accepts(str).unanchored());
        assertThat(nfa, accepts(strWithPrefix).unanchored());
        assertThat(nfa, accepts(strWithSuffix).unanchored());
        assertThat(nfa, accepts(strWithPrefixAndSuffix).unanchored());
    }
}
