package io.github.imagineDevit.giwt.core.expectations;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.github.imagineDevit.giwt.core.expectations.ExpectedToHave.anItemEqualTo;
import static io.github.imagineDevit.giwt.core.expectations.ExpectedToHave.size;
import static org.junit.jupiter.api.Assertions.*;


class ExpectedToHaveTest {

    @Test
    void testSize() {
        try {
            size(3).verify("abc");
        } catch (Exception e) {
            fail("Expected result to have size <3> but got <3>");
        }

    }

    @Test
    void testSize2() {
        try {
            size(3).verify(List.of(1, "2", true));
        } catch (Exception e) {
            fail("Expected result to have size <3> but got <3>");
        }
    }


    @Test
    void testSize3() {
        try {
            size(3).verify(Map.of("1", 1, "2", "2", "3", true));
        } catch (Exception e) {
            fail("Expected result to have size <3> but got <3>");
        }
    }

    @Test
    void testSize4() {
        try {
            size(3).verify(new Object[]{1, "2", true});
        } catch (Exception e) {
            fail("Expected result to have size <3> but got <3>");
        }
    }


    @Test
    void testSize5() {

        var ex = assertThrows(
                IllegalStateException.class,
                () -> size(3).verify(new Object())
        );

        assertEquals("Result value has no size", ex.getMessage());

    }

    @Test
    void testSize6() {
        var ex = assertThrows(
                AssertionError.class,
                () -> size(4).verify("abc")
        );

        assertEquals("Expected result to have size <4> but got <3>", ex.getMessage());

    }


    @Test
    void testAnItemEqualTo() {
        try {
            anItemEqualTo("2").verify(List.of(1, "2", true));
        } catch (Exception e) {
            fail("Expected result to contain <2> but it does not");
        }

    }

    @Test
    void testAnItemEqualTo2() {

        try {
            anItemEqualTo(1).verify(Map.of("1", 1, "2", "2", "3", true));
        } catch (Exception e) {
            fail("Expected result to contain <1> but it does not");
        }
    }

    @Test
    void testAnItemEqualTo3() {
        try {
            anItemEqualTo(true).verify(new Object[]{1, "2", true});
        } catch (Exception e) {
            fail("Expected result to contain <true> but it does not");
        }
    }

    @Test
    void testAnItemEqualTo4() {
        try {
            anItemEqualTo("a").verify("abc");
        } catch (Exception e) {
            fail("Expected result to contain <a> but it does not");
        }
    }

    @Test
    void testAnItemEqualTo5() {

        var ex = assertThrows(
                IllegalStateException.class,
                () -> anItemEqualTo(3).verify(new Object())
        );

        assertEquals("Result value is not a collection, an array, a map or a string", ex.getMessage());
    }

    @Test
    void testAnItemEqualTo6() {

        var ex = assertThrows(
                AssertionError.class,
                () -> anItemEqualTo("d").verify("abc")
        );

        assertEquals("Expected result to contain <d> but it does not", ex.getMessage());

    }

    @Test
    void testAnItemEqualTo7() {

        var ex = assertThrows(
                AssertionError.class,
                () -> anItemEqualTo(false).verify(new Object[]{1, "2", true})
        );

        assertEquals("Expected result to contain <false> but it does not", ex.getMessage());
    }

    @Test
    void testAnItemEqualTo8() {
        var ex = assertThrows(
                AssertionError.class,
                () -> anItemEqualTo("1").verify(Map.of("1", 1, "2", "2", "3", true))
        );

        assertEquals("Expected result to contain <1> but it does not", ex.getMessage());
    }

    @Test
    void testAnItemEqualTo9() {
        var ex = assertThrows(
                AssertionError.class,
                () -> anItemEqualTo(3).verify(List.of(1, "2", true))
        );

        assertEquals("Expected result to contain <3> but it does not", ex.getMessage());
    }
}