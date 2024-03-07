package io.github.imagineDevit.giwt.core.annotations;

import java.lang.annotation.*;

/**
 * Annotation used to specify methods that should be executed after all test methods in a test class.
 * The annotated method must be public, optionally taking no arguments, and not throwing any exception.
 * The execution order of methods with this annotation can be controlled using the {@code order} parameter.
 *
 * <p>Example usage:
 * <pre>{@code
 * @AfterAll(order = 1)
 * public void cleanup1() {
 *     // Perform cleanup tasks
 * }
 *
 * @AfterAll(order = 2)
 * public static void cleanup2() {
 *     // Release resources
 * }
 * }</pre>
 *
 * <p>By default, methods with the {@code AfterAll} annotation have an execution order of {@code 0}.
 * Lower order values will be executed before higher order values.
 * If two methods have the same order value, the execution order is not defined and may vary between different test frameworks.
 *
 * <p>Note that the {@code AfterAll} annotation is a meta-annotation, and can be used to create custom annotations for more specific use cases.
 *
 * @see BeforeAll
 * @see AfterEach
 * @see BeforeEach
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AfterAll  {

    /**
     * @return the callback invocation order
     */
    int order() default 0;
}
