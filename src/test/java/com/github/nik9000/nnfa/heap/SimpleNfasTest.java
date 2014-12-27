package com.github.nik9000.nnfa.heap;

import static com.github.nik9000.nnfa.heap.AcceptsMatcher.accepts;
import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;

import java.nio.charset.Charset;
import java.util.Random;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.generators.RandomInts;

public class SimpleNfasTest extends LuceneTestCase {
    @Test
    public void nothingAcceptsNothing() {
        Nfa nfa = new NfaBuilder().buildNoMark();
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
    public void anyCodePointAcceptsAnySingleChar() {
        assertThat(new NfaBuilder().anyCodePoint().build(), accepts(randomRealisticUnicodeString(random(), 1, 1)).randomAnchoring(random()));
    }

    @Test
    public void anyCodePointDoesNotAcceptEmptyString() {
        assertThat(new NfaBuilder().anyCodePoint().build(), not(accepts("").randomAnchoring(random())));
    }

    @Test
    public void anyCodePointDoesNotAcceptInvalidUtf8() {
        assertThat(new NfaBuilder().anyCodePoint().build(), not(accepts(randomInvalidUtf8Sequence(random()))));
    }

    @Test
    public void anyCodePointAcceptsAnyStringUnanchored() {
        Nfa nfa = new NfaBuilder().anyCodePoint().build();
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).startUnanchored());
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random(), 2, 20))));
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

    @Test
    public void characterAcceptsTheCharacter() {
        String c = randomRealisticUnicodeString(random(), 1, 1);
        Nfa nfa = new NfaBuilder().codePoint(c.codePointAt(0)).build();
        assertThat(nfa, accepts(c));
        assertThat(nfa, not(accepts(c + randomRealisticUnicodeString(random(), 1, 20))));
        assertThat(nfa, accepts(c + randomRealisticUnicodeString(random(), 1, 20)).endUnanchored());
        assertThat(nfa, accepts(randomRealisticUnicodeString(random(), 1, 20) + c).startUnanchored());
    }

    private byte[] randomBytes(Random random, int min, int max) {
        byte[] bytes = new byte[RandomInts.randomIntBetween(random(), min, max)];
        random.nextBytes(bytes);
        return bytes;
    }

    private byte[] randomInvalidUtf8Sequence(Random random) {
        // 0 characters are no fun - they can't be broken.
        byte[] bytes = randomRealisticUnicodeString(random, 1, 20).getBytes(Charset.forName("utf-8"));
        // TODO add more ways to break utf-8
        switch (random.nextInt(3)) {
        case 0:
            // The only valid prefix for continuation is 0b10xxxxxx so if we
            // find one an ruin it we'll break the encoding
            for (int i = 1; i < bytes.length; i++) {
                if ((bytes[i] & 0xc0) == 0x80) {
                    // The high two bits are 0b10
                    bytes[i] &= 0xc0;
                    // Now they are 0b11
                    break;
                }
            }
            // Couldn't find a byte to break - move on to the next method
        case 1:
            // The number of bytes in a multibyte sequence is specified by the
            // first byte so if we find the next byte _after_ the end of a
            // sequence and make it another continuation byte that'd be wrong
            for (int i = 2; i < bytes.length; i++) {
                if ((bytes[i - 1] & 0xc0) == 0x80 && (bytes[i] & 0xc0) != 0x80) {
                    // The high two bits aren't 0b10
                    bytes[i] = (byte)0x80;
                    // Now they are 0b10
                    break;
                }
            }
            // Couldn't find a multibyte character to break - move on to the
            // next method
        case 2:
            // 0xff, 0x is invalid in utf-8
            bytes[random.nextInt(bytes.length)] = (byte)0xff;
            break;
        case 3:
            // TODO overlong encodings
            // Overlong encodings are when you encode a sequence with more than
            // the minimum number of bytes required. This is not allowed by the
            // utf-8 spec.
        default:
            throw new RuntimeException("Fix case statement!");
        }
        return bytes;
    }
}
