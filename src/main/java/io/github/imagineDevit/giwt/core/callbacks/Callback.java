package io.github.imagineDevit.giwt.core.callbacks;

/**
 * Callback interface
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public sealed interface Callback permits AfterAllCallback, AfterEachCallback, BeforeAllCallback, BeforeEachCallback {

    class Methods {
        public static final String BEFORE_EACH = "beforeEach";
        public static final String BEFORE_ALL = "beforeAll";
        public static final String AFTER_EACH = "afterEach";
        public static final String AFTER_ALL = "afterAll";
    }
}
