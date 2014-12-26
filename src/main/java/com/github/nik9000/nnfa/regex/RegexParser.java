package com.github.nik9000.nnfa.regex;

import com.github.nik9000.nnfa.heap.Nfa;
import com.github.nik9000.nnfa.heap.NfaFactory;

public class RegexParser {
    /**
     * Syntax flag, enables intersection (<tt>&amp;</tt>).
     */
    public static final int INTERSECTION = 0x0001;

    /**
     * Syntax flag, enables complement (<tt>~</tt>).
     */
    public static final int COMPLEMENT = 0x0002;

    /**
     * Syntax flag, enables empty language (<tt>#</tt>).
     */
    public static final int EMPTY = 0x0004;

    /**
     * Syntax flag, enables anystring (<tt>@</tt>).
     */
    public static final int ANYSTRING = 0x0008;

    /**
     * Syntax flag, enables named automata (<tt>&lt;</tt>identifier<tt>&gt;</tt>
     * ).
     */
    public static final int AUTOMATON = 0x0010;

    /**
     * Syntax flag, enables numerical intervals (
     * <tt>&lt;<i>n</i>-<i>m</i>&gt;</tt>).
     */
    public static final int INTERVAL = 0x0020;

    /**
     * Syntax flag, enables all optional regexp syntax.
     */
    public static final int ALL = 0xffff;

    /**
     * Syntax flag, enables no optional regexp syntax.
     */
    public static final int NONE = 0x0000;

    public Nfa parse(NfaFactory factory, String expression, int flags) {
        if (expression.length() == 0) {
            return factory.empty();
        }
        Parse parse = new Parse(factory, expression, flags);
        Nfa nfa = parse.parseUnionExp();
        if (parse.pos < expression.length()) {
            throw new IllegalArgumentException("end-of-string expected at position " + parse.pos);
        }
        return nfa;
    }

    /**
     * Parse a regexp. The internals of this class started with Lucene's RegExp
     * class but have been pretty heavily hacked on.
     */
    private static class Parse {
        final NfaFactory factory;
        final String expression;
        final int flags;
        int pos = 0;

        public Parse(NfaFactory factory, String expression, int flags) {
            this.factory = factory;
            this.expression = expression;
            this.flags = flags;
        }

        private boolean peek(String s) {
            return more() && s.indexOf(expression.codePointAt(pos)) != -1;
        }

        private boolean match(int c) {
            if (pos >= expression.length())
                return false;
            if (expression.codePointAt(pos) == c) {
                pos += Character.charCount(c);
                return true;
            }
            return false;
        }

        private boolean more() {
            return pos < expression.length();
        }

        private int next() throws IllegalArgumentException {
            if (!more()) {
                throw new IllegalArgumentException("unexpected end-of-string");
            }
            int ch = expression.codePointAt(pos);
            pos += Character.charCount(ch);
            return ch;
        }

        private boolean check(int flag) {
            return (flags & flag) != 0;
        }

        Nfa parseUnionExp() {
            Nfa nfa = parseIntersectionExp();
            if (match('|')) {
                nfa.union(parseUnionExp());
            }
            return nfa;
        }

        Nfa parseIntersectionExp() {
            Nfa nfa = parseConcatExp();
            if (check(INTERSECTION) && match('&')) {
                nfa.intersect(parseIntersectionExp());
            }
            return nfa;
        }

        Nfa parseConcatExp() {
            Nfa nfa = parseRepeatExp();
            if (more() && !peek(")|") && (!check(INTERSECTION) || !peek("&")))
                nfa.concat(parseConcatExp(), false);
            return nfa;
        }

        Nfa parseRepeatExp() {
            Nfa nfa = parseComplementExp();
            while (peek("?*+{")) {
                if (match('?')) {
                    nfa.optional();
                    continue;
                }
                if (match('*')) {
                    nfa.makeRepeat(0, -1);
                    continue;
                }
                if (match('+')) {
                    nfa.makeRepeat(1, -1);
                    continue;
                }
                if (match('{')) {
                    int min = parsePositiveInteger();
                    if (min < 0) {
                        throw new IllegalArgumentException("integer expected at position " + pos);
                    }
                    int max = -1;
                    if (match(',')) {
                        max = parsePositiveInteger();
                    } else {
                        max = min;
                    }
                    if (!match('}')) {
                        throw new IllegalArgumentException("expected '}' at position " + pos);
                    }
                    nfa.makeRepeat(min, max);
                    continue;
                }
                throw new IllegalArgumentException("unexpected " + expression.codePointAt(pos) + " at " + pos);
            }
            return nfa;
        }

        /**
         * Parse a positive integer or return -1 if there isn't one at pos.
         */
        private int parsePositiveInteger() {
            int start = pos;
            while (peek("0123456789")) {
                next();
            }
            if (start == pos) {
                return -1;
            }
            return Integer.parseInt(expression.substring(start, pos));
        }

        Nfa parseComplementExp() {
            if (check(COMPLEMENT) && match('~')) {
                Nfa nfa = parseComplementExp();
                nfa.complement();
                return nfa;
            }
            return parseCharacterClassExp();
        }

        Nfa parseCharacterClassExp() {
            if (match('[')) {
                boolean negate = false;
                if (match('^')) {
                    negate = true;
                }
                Nfa nfa = parseCharClasses();
                if (negate) {
                    // TODO handle non-1 byte characters here
                    nfa.complement();
                    Nfa chars = nfa;
                    nfa = factory.anyChar();
                    nfa.intersect(chars);
                }
                if (!match(']')) {
                    throw new IllegalArgumentException("expected ']' at position " + pos);
                }
                return nfa;
            }
            return parseSimpleExp();
        }

        Nfa parseCharClasses() {
            Nfa nfa = parseCharClass();
            while (more() && !peek("]")) {
                nfa.union(parseCharClass());
            }
            return nfa;
        }

        Nfa parseCharClass() {
            int c = parseCharExp();
            if (match('-')) {
                return factory.characterRange(c, parseCharExp());
            }
            return factory.character(c);
        }

        Nfa parseSimpleExp() {
            if (match('.')) {
                return factory.anyChar();
            }
            if (check(EMPTY) && match('#')) {
                return factory.nothing();
            }
            if (check(ANYSTRING) && match('@')) {
                return factory.anyString();
            }
            if (match('"')) {
                int start = pos;
                while (more() && !peek("\"")) {
                    next();
                }
                if (!match('"')) {
                    throw new IllegalArgumentException("expected '\"' at position " + pos);
                }
                return factory.string(expression.substring(start, pos - 1));
            }
            if (match('(')) {
                if (match(')')) {
                    return factory.empty();
                }
                Nfa nfa = parseUnionExp();
                if (!match(')')) {
                    throw new IllegalArgumentException("expected ')' at position " + pos);
                }
                return nfa;
            }
            if ((check(AUTOMATON) || check(INTERVAL)) && match('<')) {
                int start = pos;
                while (more() && !peek(">")) {
                    next();
                }
                if (!match('>')) {
                    throw new IllegalArgumentException("expected '>' at position " + pos);
                }
                String s = expression.substring(start, pos - 1);
                int i = s.indexOf('-');
                if (i == -1) {
                    if (!check(AUTOMATON)) {
                        throw new IllegalArgumentException("interval syntax error at position "
                                + (pos - 1));
                    }
                    throw new UnsupportedOperationException("Automaton option net yet supported");
                }
                if (!check(INTERVAL)) {
                    throw new IllegalArgumentException("illegal identifier at position "
                            + (pos - 1));
                }
                try {
                    if (i == 0 || i == s.length() - 1 || i != s.lastIndexOf('-')) {
                        throw new IllegalArgumentException("interval syntax error at position "
                                + (pos - 1));
                    }
                    String smin = s.substring(0, i);
                    String smax = s.substring(i + 1, s.length());
                    int imin = Integer.parseInt(smin);
                    int imax = Integer.parseInt(smax);
                    int digits;
                    if (smin.length() == smax.length()) {
                        digits = smin.length();
                    } else {
                        digits = 0;
                    }
                    if (imin > imax) {
                        int t = imin;
                        imin = imax;
                        imax = t;
                    }
                    return factory.interval(imin, imax, digits);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("interval syntax error at position "
                            + (pos - 1));
                }
            }
            return factory.character(parseCharExp());
        }

        final int parseCharExp() throws IllegalArgumentException {
            match('\\');
            return next();
        }
    }
}
