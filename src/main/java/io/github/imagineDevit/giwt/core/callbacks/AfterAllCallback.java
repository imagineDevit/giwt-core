package io.github.imagineDevit.giwt.core.callbacks;

/**
 * An interface that represents a callback method to be executed after all tests have run.
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@FunctionalInterface
public non-sealed interface AfterAllCallback extends Callback {
    void afterAll();
}
