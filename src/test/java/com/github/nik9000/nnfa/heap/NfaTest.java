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
		assertThat(nfa, not(accepts("").endAnchored(random())));
		assertThat(nfa, not(accepts(randomRealisticUnicodeString(random())).endAnchored(random())));
	}

	@Test
	public void justAcceptStateMatchesEverything() {
		Nfa nfa = new Nfa();
		nfa.initial().accepts(true);
		assertThat(nfa, accepts("").endAnchored(random()));
		assertThat(nfa, accepts(randomRealisticUnicodeString(random())));
	}

	@Test
	public void justEpsilonTransitionToAcceptStateMatchesEverything() {
		Nfa nfa = new Nfa();
		State accept = new State().accepts(true);
		nfa.initial().epsilonTransitions().add(accept);
		assertThat(nfa, accepts("").endAnchored(random()));
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
		Nfa nfa = exactString("candle");
		nfa.initial().combine(exactString("light").initial());
		assertThat(nfa, accepts("candle").endAnchored(random()));
		assertThat(nfa, accepts("light").endAnchored(random()));
		assertThat(nfa, not(accepts("lemmon").endAnchored(random())));
	}

	private void exactStringTestCase(String str) {
		Nfa nfa = exactString(str);
		assertThat(nfa, accepts(str));
		assertThat(nfa, accepts(str).endAnchored());
		String strWithSuffix = str + randomRealisticUnicodeString(random(), 1, 20);
		assertThat(nfa, accepts(strWithSuffix));
		assertThat(nfa, not(accepts(strWithSuffix).endAnchored()));
	}

	private Nfa exactString(String str) {
		Nfa nfa = new Nfa();
		byte[] bytes = str.getBytes(Charset.forName("utf-8"));
		State from = nfa.initial();
		State to = nfa.initial();
		for (int i = 0; i < bytes.length; i++) {
			to = new State();
			from.standardTransitions().add(new Transition(bytes[i], bytes[i], to));
			from = to;
		}
		to.accepts(true);
		return nfa;
	}

	public AcceptsMatcher accepts(String str) {
		return new AcceptsMatcher(str);
	}
}
