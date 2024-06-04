package io.github.imagineDevit.giwt.core.expectations;


import io.github.imagineDevit.giwt.core.errors.ExpectationError;
import org.junit.jupiter.api.Test;

import static io.github.imagineDevit.giwt.core.expectations.ExpectedToBe.*;
import static org.junit.jupiter.api.Assertions.*;

class ExpectedToBeTest {

    @Test
    void testNull() {
        try {
            null_().verify(null);
        } catch (Exception e) {
            fail("Expected value to be null but got <null>");
        }
    }

    @Test
    void testNull2() {
        var ex = assertThrows(
                ExpectationError.class,
                () -> null_().verify("Hello")
        );

        assertEquals("Expected <null> but got <Hello>", ex.getMessage());
    }


    @Test
    void testNotNull() {
        try {
            notNull().verify("Hello");
        } catch (Exception e) {
            fail("Expected not null value but got <null>");
        }
    }

    @Test
    void testNotNull2() {
        var ex = assertThrows(
                ExpectationError.class,
                () -> notNull().verify(null)
        );

        assertEquals("Expected not null value but got <null>", ex.getMessage());
    }


    @Test
    void testEqualTo() {

        try {
            equalTo("Hello").verify("Hello");
        } catch (Exception e) {
            fail("Expected value to be <Hello> but got <Hello>");
        }
    }

    @Test
    void testEqualTo2() {
        var ex = assertThrows(
                ExpectationError.class,
                () -> equalTo("World").verify("Hello")
        );

        assertEquals("Expected value to be <World> but got <Hello>", ex.getMessage());

    }

    @Test
    void testNotEqualTo() {
        try {
            notEqualTo("World").verify("Hello");
        } catch (Exception e) {
            fail("Expected value to be different from <Hello> but got <Hello>");
        }

    }

    @Test
    void testNotEqualTo2() {
        var ex = assertThrows(
                ExpectationError.class,
                () -> notEqualTo("Hello").verify("Hello")
        );

        assertEquals("Expected value to be different from <Hello> but got <Hello>", ex.getMessage());

    }

    @Test
    void testBetween() {
        try {
            between(1, 10).verify(5);
        } catch (Exception e) {
            fail("Expected value to be between <1> and <10> but got <5>");
        }
    }

    @Test
    void testBetween2() {
        var ex = assertThrows(
                ExpectationError.class,
                () -> between(1, 10).verify(0)
        );

        assertEquals("Expected value to be between <1> and <10> but got <0>", ex.getMessage());
    }

    @Test
    void testGreaterThan() {

        try {
            greaterThan(1).verify(5);
        } catch (Exception e) {
            fail("Expected value to be greater than <1> but got <5>");
        }
    }

    @Test
    void testGreaterThan2() {

        var ex = assertThrows(
                ExpectationError.class,
                () -> greaterThan(1).verify(0)
        );

        assertEquals("Expected value to be greater than <1> but got <0>", ex.getMessage());
    }

    @Test
    void testLessThan() {
        try {
            lessThan(10).verify(5);
        } catch (Exception e) {
            fail("Expected value to be less than <10> but got <5>");
        }

    }

    @Test
    void testLessThan2() {

        var ex = assertThrows(
                ExpectationError.class,
                () -> lessThan(0).verify(5)
        );

        assertEquals("Expected value to be less than <0> but got <5>", ex.getMessage());

    }

    @Test
    void testSameAs() {
        try {
            sameAs("Hello").verify("Hello");
        } catch (Exception e) {
            fail("Expected value to be the same as <Hello> but got <Hello>");
        }
    }

}