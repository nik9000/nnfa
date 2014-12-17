package com.github.nik9000.nnfa.heap;

import java.nio.charset.Charset;

import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.TestUtil;
import org.junit.Test;

public class NfaTest extends LuceneTestCase {
	@Test
	public void oneCharacter() {
		exactTestCase("a");
	}

	@Test
	public void twoCharacter() {
		exactTestCase("ab");
	}

	@Test
	public void unicodeCharacters() {
		exactTestCase(TestUtil.randomRealisticUnicodeString(random()));
	}

	private void exactTestCase(String str) {
		assertTrue(String.format("NFA for %s should accept it.", str), exactString(str).accepts(str, true));		
	}
	private Nfa exactString(String str) {
		Nfa nfa = new Nfa();
		State accept = new State().accepts(true);
		byte[] bytes = str.getBytes(Charset.forName("utf-8"));
		State from = nfa.initial();
		for (int i = 0; i < bytes.length; i++) {
			State to = i == bytes.length - 1 ? accept : new State();
			from.standardTransitions().add(new Transition(bytes[i], bytes[i], to));
			from = to;
		}
		return nfa;
	}
}
