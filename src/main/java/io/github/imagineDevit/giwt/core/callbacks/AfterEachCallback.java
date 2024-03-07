package io.github.imagineDevit.giwt.core.callbacks;

/**
 * Represents a callback function to be executed after each test.
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@FunctionalInterface
public non-sealed interface AfterEachCallback extends Callback {
    void afterEach();
}
