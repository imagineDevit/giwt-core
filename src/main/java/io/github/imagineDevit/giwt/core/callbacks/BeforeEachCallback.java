package io.github.imagineDevit.giwt.core.callbacks;

/**
 * This functional interface represents a callback that is executed before each test method
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@FunctionalInterface
public non-sealed interface BeforeEachCallback extends Callback {
    void beforeEach();
}
