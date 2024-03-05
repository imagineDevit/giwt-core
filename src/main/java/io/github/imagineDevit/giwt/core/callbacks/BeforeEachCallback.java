package io.github.imagineDevit.giwt.core.callbacks;

/**
 * This functional interface represents a callback that is executed before each test method
 */
@FunctionalInterface
public non-sealed interface BeforeEachCallback extends Callback {
    void beforeEach();
}
