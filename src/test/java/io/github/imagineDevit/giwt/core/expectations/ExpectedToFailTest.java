package io.github.imagineDevit.giwt.core.expectations;

import io.github.imagineDevit.giwt.core.errors.ExpectationError;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static io.github.imagineDevit.giwt.core.expectations.ExpectedToFail.withMessage;
import static io.github.imagineDevit.giwt.core.expectations.ExpectedToFail.withType;
import static org.junit.jupiter.api.Assertions.*;


class ExpectedToFailTest {

    @Test
    void testWithType() {
        try {
            withType(IllegalStateException.class).verify(new IllegalStateException());
        } catch (Exception e) {
            fail("Expected error to be of type <java.lang.IllegalStateException> but got <java.lang.IllegalStateException>");
        }
    }

    @Test
    void testWithType2() {

        var ex = assertThrows(
                ExpectationError.class,
                () -> withType(IllegalStateException.class).verify(new NoSuchElementException())
        );

        assertEquals("Expected error to be of type <java.lang.IllegalStateException> but got <java.util.NoSuchElementException>", ex.getMessage());

    }

    @Test
    void testWithMessage() {
        try {
            withMessage("Illegal state").verify(new Exception("Illegal state"));
        } catch (Exception e) {
            fail("Expected error message to be <Illegal state> but got <Illegal state>");
        }
    }

    @Test
    void testWithMessage2() {

        var ex = assertThrows(
                ExpectationError.class,
                () -> withMessage("Null pointer").verify(new Exception("Illegal state"))
        );

        assertEquals("Expected error message to be <Null pointer> but got <Illegal state>", ex.getMessage());
    }

}