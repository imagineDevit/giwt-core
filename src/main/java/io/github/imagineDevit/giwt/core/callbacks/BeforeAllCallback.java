package io.github.imagineDevit.giwt.core.callbacks;

/**
 * A functional interface that represents a callback to be executed before all test cases in a test suite.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@FunctionalInterface
public non-sealed interface BeforeAllCallback extends Callback {
    void beforeAll();
}
