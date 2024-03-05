package io.github.imagineDevit.giwt.core.callbacks;

public record GiwtCallbacks(
        BeforeAllCallback beforeAllCallback,
        AfterAllCallback afterAllCallback,
        BeforeEachCallback beforeEachCallback,
        AfterEachCallback afterEachCallback
) {}
