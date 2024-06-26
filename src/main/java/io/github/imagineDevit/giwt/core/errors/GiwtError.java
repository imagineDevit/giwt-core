package io.github.imagineDevit.giwt.core.errors;

/**
 * Giwt error abstract class
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public sealed abstract class GiwtError extends RuntimeException
        permits DuplicateTestNameException, ExpectationError, ParameterSourceException, ParameterizedTestMethodException, ResultValueError, TestClassException, TestMethodException {
    public GiwtError(String message) {
        super(message);
    }
}
