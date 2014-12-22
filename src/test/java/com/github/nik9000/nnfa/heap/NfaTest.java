package com.github.nik9000.nnfa.heap;

import static org.apache.lucene.util.TestUtil.randomRealisticUnicodeString;
import static org.hamcrest.Matchers.not;

import java.nio.charset.Charset;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

public class NfaTest extends LuceneTestCase {
    @Test
    public void noAcceptStateMatchesNothing() {
        Nfa nfa = new Nfa();
        assertThat(nfa, not(accepts("")));
        assertThat(nfa, not(accepts(randomRealisticUnicodeString(random()))));
    }

    @Test
    public void justAcceptStateMatchesEverything() {
        Nfa nfa = new Nfa();
        nfa.initial().accepts(true);
        assertThat(nfa, accepts(""));
        assertThat(nfa, accepts(randomRealisticUnicodeString(random())));
    }

    @Test
    public void justEpsilonTransitionToAcceptStateMatchesEverything() {
        Nfa nfa = new Nfa();
        State accept = new State().accepts(true);
        nfa.initial().transitions().add(new EpsilonTransition(accept));
        assertThat(nfa, accepts(""));
        assertThat(nfa, accepts(randomRealisticUnicodeString(random())));
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
        Nfa nfa = exactString("candle", true, true);
        nfa.initial().combine(exactString("light", true, true).initial());
        assertThat(nfa, accepts("candle"));
        assertThat(nfa, accepts("light"));
        assertThat(nfa, not(accepts("lemmon")));
    }

    private void exactStringTestCase(String str) {
        String strWithPrefix = randomRealisticUnicodeString(random(), 1, 20) + str;
        String strWithSuffix = str + randomRealisticUnicodeString(random(), 1, 20);
        String strWithPrefixAndSuffix = strWithPrefix
                + randomRealisticUnicodeString(random(), 1, 20);

        Nfa nfa = exactString(str, true, true);
        assertThat(nfa, accepts(str));
        assertThat(nfa, not(accepts(strWithPrefix)));
        assertThat(nfa, not(accepts(strWithSuffix)));
        assertThat(nfa, not(accepts(strWithPrefixAndSuffix)));

        nfa = exactString(str, true, false);
        assertThat(nfa, accepts(str));
        assertThat(nfa, not(accepts(strWithPrefix)));
        assertThat(nfa, accepts(strWithSuffix));
        assertThat(nfa, not(accepts(strWithPrefixAndSuffix)));

        nfa = exactString(str, false, true);
        assertThat(nfa, accepts(str));
        assertThat(nfa, accepts(strWithPrefix));
        assertThat(nfa, not(accepts(strWithSuffix)));
        assertThat(nfa, not(accepts(strWithPrefixAndSuffix)));

        nfa = exactString(str, false, false);
        assertThat(nfa, accepts(str));
        assertThat(nfa, accepts(strWithPrefix));
        assertThat(nfa, accepts(strWithSuffix));
        assertThat(nfa, accepts(strWithPrefixAndSuffix));
    }

    private Nfa exactString(String str, boolean startAnchor, boolean endAnchor) {
        Nfa nfa = new Nfa();
        byte[] bytes = str.getBytes(Charset.forName("utf-8"));
        State from = nfa.initial();
        State to = nfa.initial();
        if (startAnchor) {
            to = new State();
            from.transitions().add(new AtBeginningTransition(to));
            from = to;
        }
        for (int i = 0; i < bytes.length; i++) {
            to = new State();
            from.transitions().add(new ByteMatchingTransition(bytes[i], bytes[i], to));
            from = to;
        }
        if (endAnchor) {
            from = to;
            to = new State();
            from.transitions().add(new AtEndTransition(to));
        }
        to.accepts(true);
        return nfa;
    }

    public AcceptsMatcher accepts(String str) {
        return new AcceptsMatcher(str);
    }
}
