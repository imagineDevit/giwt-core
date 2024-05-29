package io.github.imagineDevit.giwt.core.expectations;


import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.imagineDevit.giwt.core.expectations.ExpectedToMatch.matching;
import static io.github.imagineDevit.giwt.core.expectations.ExpectedToMatch.one;
import static org.junit.jupiter.api.Assertions.*;

class ExpectedToMatchTest {

    @Test
    void testOne() {

        try {
            one(matching("expected to be <a>", s -> s.equals("a"))).verify("a");
        } catch (Exception e) {
            fail("Expected value to be <a> but got <a>");
        }

    }

    @Test
    void testOne2() {

        var ex = assertThrows(
                AssertionError.class,
                () -> one(matching("expected to be <a>", s -> s.equals("a"))).verify("b")
        );

        assertEquals("Matching <expected to be <a>> failed", ex.getMessage());

    }


    @Test
    void testAll() {

        try {
            new ExpectedToMatch.All<String>(List.of(
                    matching("expected to be <a>", s -> s.equals("a")),
                    matching("expected to have length 1", s -> s.length() == 1)
            )).verify("a");
        } catch (Exception e) {
            fail("Expected value to be <a> and have length 1 but got <a>");
        }
    }

    @Test
    void testAll2() {
        String desc1 = "expected to be <a>";
        String desc2 = "expected to have length 1";

        var ex = assertThrows(
                AssertionError.class,
                () -> new ExpectedToMatch.All<String>(List.of(
                        matching(desc1, s -> s.equals("a")),
                        matching(desc2, s -> s.length() == 1)
                )).verify("b")
        );

        assertEquals("Matching <expected to be <a>> failed", ex.getMessage());

    }

    @Test
    void testAll3() {
        String desc1 = "expected to be <a>";
        String desc2 = "expected to have length 1";

        var ex = assertThrows(
                AssertionError.class,
                () -> new ExpectedToMatch.All<String>(List.of(
                        matching(desc2, s -> s.length() == 1),
                        matching(desc1, s -> s.equals("a"))
                )).verify("b")
        );

        assertEquals("Matching <expected to be <a>> failed", ex.getMessage());

    }


    @Test
    void testNone() {

        try {
            new ExpectedToMatch.None<String>(List.of(
                    matching("expected to be <a>", s -> s.equals("a")),
                    matching("expected to have length 1", s -> s.length() == 1)
            )).verify("ab");
        } catch (Exception e) {
            fail("Expected value to not be <a> and have length 1 but got <ab>");
        }

    }

    @Test
    void testNone2() {
        String desc1 = "expected to be <ab>";
        String desc2 = "expected to have length 1";

        var ex = assertThrows(
                AssertionError.class,
                () -> new ExpectedToMatch.None<String>(List.of(
                        matching(desc1, s -> s.equals("ab")),
                        matching(desc2, s -> s.length() == 1)
                )).verify("ab")
        );

        assertEquals("Matching <expected to be <ab>> failed", ex.getMessage());
    }

    @Test
    void testNone3() {
        String desc1 = "expected to be <a>";
        String desc2 = "expected to have length 2";

        var ex = assertThrows(
                AssertionError.class,
                () -> new ExpectedToMatch.None<String>(List.of(
                        matching(desc1, s -> s.equals("a")),
                        matching(desc2, s -> s.length() == 2)
                )).verify("ab")
        );

        assertEquals("Matching <expected to have length 2> failed", ex.getMessage());

    }
}