package io.github.imagineDevit.giwt.core.callbacks;

/**
 * An interface that represents a callback method to be executed after all tests have run.
 */
@FunctionalInterface
public non-sealed interface AfterAllCallback extends Callback {
    void afterAll();
}
