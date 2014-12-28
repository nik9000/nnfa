package com.github.nik9000.nnfa.heap;

import static com.github.nik9000.nnfa.heap.AcceptsMatcher.accepts;
import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;

import java.util.Random;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.generators.RandomInts;
import com.github.nik9000.nnfa.builder.NfaBuilder;

/**
 * Test for AbstractByteOperations.
 */
public class ByteOperationsTest extends LuceneTestCase {
    @Test
    public void nothingAcceptsNothing() {
        Nfa nfa = new NfaBuilder().buildNoAccept();
        assertThat(nfa, not(accepts("").randomAnchoring(random())));
        assertThat(nfa,
                not(accepts(randomRealisticUnicodeString(random())).randomAnchoring(random())));
    }

    @Test
    public void emptyMatchesEmptyString() {
        assertThat(new NfaBuilder().build(), accepts("").randomAnchoring(random()));
    }

    @Test
    public void emptyMatchesAnythingUnanchored() {
        Nfa nfa = new NfaBuilder().build();
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).startUnanchored());
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random(), 1, 20))));
    }

    @Test
    public void justEpsilonTransitionToAcceptStateMatchesEmpty() {
        Nfa nfa = new Nfa(new State(0));
        State accept = new State(1).accepts(true);
        nfa.initial().transitions().add(new EpsilonTransition(accept));
        assertThat(nfa, accepts("").randomAnchoring(random()));
    }

    @Test
    public void justEpsilonTransitionToAcceptStateMatchesAnythingUnanchored() {
        Nfa nfa = new Nfa(new State(0));
        State accept = new State(1).accepts(true);
        nfa.initial().transitions().add(new EpsilonTransition(accept));
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).startUnanchored());
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random(), 1, 20))));
    }

    @Test
    public void anyByteAcceptsAnyStringUnanchored() {
        assertThat(new NfaBuilder().anyByte().build(), accepts(randomRealisticUnicodeString(random(), 1, 20)).startUnanchored());
        assertThat(new NfaBuilder().anyByte().build(), accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(new NfaBuilder().anyByte().build(), accepts(randomRealisticUnicodeString(random(), 1, 20)).unanchored());
        assertThat(new NfaBuilder().anyByte().build(), not(accepts(randomRealisticUnicodeString(random(), 2, 20))));
    }

    @Test
    public void anyByteAcceptsAnyByteSequence() {
        assertThat(new NfaBuilder().anyByte().build(), accepts(randomBytes(random(), 1, 1)));
        assertThat(new NfaBuilder().anyByte().build(), accepts(randomBytes(random(), 1, 20)).startUnanchored());
        assertThat(new NfaBuilder().anyByte().build(), accepts(randomBytes(random(), 1, 20)).endUnanchored());
        assertThat(new NfaBuilder().anyByte().build(), accepts(randomBytes(random(), 1, 20)).unanchored());
        assertThat(new NfaBuilder().anyByte().build(), not(accepts(randomBytes(random(), 2, 20))));
    }

    @Test
    public void aByteAcceptsTheByte() {
        byte[] b = new byte[1];
        random().nextBytes(b);
        Nfa nfa = new NfaBuilder().aByte(b[0]).build();
        assertThat(nfa, accepts(b));
        byte[] notB = new byte[1];
        notB[0] = (byte)(b[0] + 1);
        assertThat(nfa, not(accepts(notB)));
        notB = new byte[2];
        random().nextBytes(notB);
        notB[0] = b[0];
        assertThat(nfa, not(accepts(notB)));
        assertThat(nfa, accepts(notB).endUnanchored());
        random().nextBytes(notB);
        notB[1] = b[0];
        assertThat(nfa, not(accepts(notB)));
        assertThat(nfa, accepts(notB).startUnanchored());
    }

    private byte[] randomBytes(Random random, int min, int max) {
        byte[] bytes = new byte[RandomInts.randomIntBetween(random(), min, max)];
        random.nextBytes(bytes);
        return bytes;
    }
}
