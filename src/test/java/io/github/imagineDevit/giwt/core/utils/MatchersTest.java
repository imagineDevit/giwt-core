package io.github.imagineDevit.giwt.core.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.imagineDevit.giwt.core.utils.Matchers.MatchCase.matchCase;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.FailureArg.ExceptionArg;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.FailureArg.StringArg;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.failure;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.success;
import static io.github.imagineDevit.giwt.core.utils.Matchers.match;
import static org.junit.jupiter.api.Assertions.*;

class MatchersTest {

    @Test
    @DisplayName("""
            WHEN match function is called with one success case that has a predicate that matches
            THEN the result should be non empty optional
            """
    )
    void testMatch() {

        match(matchCase(() -> true, () -> success("value")))
                .ifPresentOrElse(
                        value -> assertEquals("value", value),
                        () -> fail("Optional should not be empty")
                );
    }

    @Test
    @DisplayName("""
            WHEN match function is called with one failure case that has a predicate that matches
            THEN the result should be an empty optional
            """
    )
    void testMatch2() {
        match(matchCase(() -> true, () -> failure("value")))
                .ifPresent(value -> fail("Optional should be empty"));
    }

    @Test
    @DisplayName("""
            WHEN match function is called with:
              - one success case that has a predicate that always returns true
              - and a list of failures cases that have predicates that always return false
            THEN the result should be optional of value
            """
    )
    void testMatch3() {
        match(
                matchCase(() -> false, () -> failure(new StringArg(() -> "error1"))),
                matchCase(() -> false, () -> failure(new ExceptionArg(() -> new IllegalStateException("error2")))),
                matchCase(() -> false, () -> failure("error3")),
                matchCase(() -> true, () -> success("value"))
        ).ifPresentOrElse(
                value -> assertEquals("value", value),
                () -> fail("Optional should not be empty")
        );
    }

    @Test
    @DisplayName("""
            WHEN match function is called with:
              - one success case that has a predicate that always returns true
              - one failure case that has a predicate that always return true
              - and a list of failures cases that have predicates that always return false
            THEN the result should be a runtime exception
            """
    )
    void testMatch4() {
        var ex = assertThrows(RuntimeException.class, () -> match(
                matchCase(() -> false, () -> failure(new StringArg(() -> "error1"))),
                matchCase(() -> true, () -> failure(new ExceptionArg(() -> new RuntimeException("error2")))),
                matchCase(() -> false, () -> failure("error3")),
                matchCase(() -> true, () -> success("value"))
        ));

        var ex2 = assertThrows(IllegalStateException.class, () -> match(
                matchCase(() -> true, () -> failure(new StringArg(() -> "error1"))),
                matchCase(() -> false, () -> failure(new ExceptionArg(() -> new RuntimeException("error2")))),
                matchCase(() -> false, () -> failure("error3")),
                matchCase(() -> true, () -> success("value"))
        ));

        assertEquals("error2", ex.getMessage());
        assertEquals("error1", ex2.getMessage());
    }


}