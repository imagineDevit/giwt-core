package io.github.imagineDevit.giwt.core.annotations;

import io.github.imagineDevit.giwt.core.callbacks.Callback;

import java.lang.annotation.*;

/**
 * Annotation that specifies the extensions classes for a particular class.
 * The annotated class can be configured with an instance of the specified extensions classes.
 *
 * @see io.github.imagineDevit.giwt.core.callbacks.BeforeAllCallback
 * @see io.github.imagineDevit.giwt.core.callbacks.BeforeEachCallback
 * @see io.github.imagineDevit.giwt.core.callbacks.AfterEachCallback
 * @see io.github.imagineDevit.giwt.core.callbacks.AfterAllCallback
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface ExtendWith {
    /**
     * @return array of extensions classes
     */
    Class<? extends Callback>[] value();
}
