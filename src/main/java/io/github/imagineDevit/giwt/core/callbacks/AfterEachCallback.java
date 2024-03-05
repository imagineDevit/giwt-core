package io.github.imagineDevit.giwt.core.callbacks;

/**
 * Represents a callback function to be executed after each test.
 */
@FunctionalInterface
public non-sealed interface AfterEachCallback extends Callback {
    void afterEach();
}
