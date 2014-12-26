package com.github.nik9000.nnfa.heap;

import static com.github.nik9000.nnfa.heap.AcceptsMatcher.accepts;
import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

public class SimpleNfasTest extends LuceneTestCase {
    private final NfaFactory factory = new NfaFactory();

    @Test
    public void nothingAcceptsNothing() {
        Nfa nfa = factory.nothing();
        assertThat(nfa, not(accepts("").randomAnchoring(random())));
        assertThat(nfa,
                not(accepts(randomRealisticUnicodeString(random())).randomAnchoring(random())));
    }

    @Test
    public void emptyMatchesEmptyString() {
        assertThat(factory.empty(), accepts("").randomAnchoring(random()));
    }

    @Test
    public void emptyMatchesAnythingUnanchored() {
        Nfa nfa = factory.empty();
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).startUnanchored());
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
    public void justEpsilonTransitionToAcceptStateMatchesAnythingUnanchored() {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        nfa.initial().transitions().add(new EpsilonTransition(accept));
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).startUnanchored());
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random(), 1, 20))));
    }
}
