package io.github.imagineDevit.giwt.core.annotations;

import io.github.imagineDevit.giwt.core.TestConfiguration;

import java.lang.annotation.*;

/**
 * Annotation that specifies the test configuration class for a particular class.
 * The annotated class can be configured with an instance of the specified test configuration class.
 *
 * @author Henri Joel SEDJAME
 * @see TestConfiguration
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ConfigureWith {
    /**
     * @return the test configuration class
     */
    Class<? extends TestConfiguration> value();
}
