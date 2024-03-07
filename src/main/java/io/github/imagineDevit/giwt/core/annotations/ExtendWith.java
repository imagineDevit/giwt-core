package io.github.imagineDevit.giwt.core.annotations;

import io.github.imagineDevit.giwt.core.callbacks.*;

import java.lang.annotation.*;

/**
 * Annotation that specifies the extensions classes for a particular class.
 * The annotated class can be configured with an instance of the specified extensions classes.
 *
 * @see BeforeAllCallback
 * @see BeforeEachCallback
 * @see AfterEachCallback
 * @see AfterAllCallback
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
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
