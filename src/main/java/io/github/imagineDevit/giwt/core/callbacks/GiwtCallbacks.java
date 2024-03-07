package io.github.imagineDevit.giwt.core.callbacks;


/**
 * A class that aggregates all callbacks present in a test class.
 * @param beforeAllCallback
 * @param afterAllCallback
 * @param beforeEachCallback
 * @param afterEachCallback
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public record GiwtCallbacks(
        BeforeAllCallback beforeAllCallback,
        AfterAllCallback afterAllCallback,
        BeforeEachCallback beforeEachCallback,
        AfterEachCallback afterEachCallback
) {}
