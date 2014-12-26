package com.github.nik9000.nnfa.heap;

import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;
import static com.github.nik9000.nnfa.heap.AcceptsMatcher.*;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

public class NfaTest extends LuceneTestCase {
    private final NfaFactory factory = new NfaFactory();

    @Test
    public void noAcceptStateMatchesNothing() {
        Nfa nfa = new Nfa();
        assertThat(nfa, not(accepts("").randomAnchoring(random())));
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random())).randomAnchoring(random())));
    }

    @Test
    public void justAcceptStateMatchesEmpty() {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        assertThat(nfa, accepts("").randomAnchoring(random()));
    }
    
    @Test
    public void justAcceptStateMatchesAnythingEndUnanchored() {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random(), 1, 20))));
    }

    @Test
    public void justEpsilonTransitionToAcceptStateMatchesEmpty() {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        nfa.initial().transitions().add(new EpsilonTransition(accept));
        assertThat(nfa, accepts("").randomAnchoring(random()));
    }

    @Test
    public void justEpsilonTransitionToAcceptStateMatchesAnythingEndUnanchored() {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        nfa.initial().transitions().add(new EpsilonTransition(accept));
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random(), 1, 20))));
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
        exactStringTestCase(randomRealisticUnicodeString(random()));
    }

    @Test
    public void or() {
        Nfa nfa = factory.string("candle");
        nfa.union(factory.string("light"));
        assertThat(nfa, accepts("candle"));
        assertThat(nfa, accepts("light"));
        assertThat(nfa, not(accepts("lemmon")));
    }

    private void exactStringTestCase(String str) {
        String strWithPrefix = randomRealisticUnicodeString(random(), 1, 20) + str;
        String strWithSuffix = str + randomRealisticUnicodeString(random(), 1, 20);
        String strWithPrefixAndSuffix = strWithPrefix
                + randomRealisticUnicodeString(random(), 1, 20);

        Nfa nfa = factory.string(str);
        assertThat(nfa, accepts(str));
        assertThat(nfa, not(accepts(strWithPrefix)));
        assertThat(nfa, not(accepts(strWithSuffix)));
        assertThat(nfa, not(accepts(strWithPrefixAndSuffix)));

        assertThat(nfa, accepts(str).endUnanchored());
        assertThat(nfa, not(accepts(strWithPrefix).endUnanchored()));
        assertThat(nfa, accepts(strWithSuffix).endUnanchored());
        assertThat(nfa, not(accepts(strWithPrefixAndSuffix).endUnanchored()));

        assertThat(nfa, accepts(str).startUnanchored());
        assertThat(nfa, accepts(strWithPrefix).startUnanchored());
        assertThat(nfa, not(accepts(strWithSuffix).startUnanchored()));
        assertThat(nfa, not(accepts(strWithPrefixAndSuffix).startUnanchored()));

        assertThat(nfa, accepts(str).unAnchored());
        assertThat(nfa, accepts(strWithPrefix).unAnchored());
        assertThat(nfa, accepts(strWithSuffix).unAnchored());
        assertThat(nfa, accepts(strWithPrefixAndSuffix).unAnchored());
    }
}
