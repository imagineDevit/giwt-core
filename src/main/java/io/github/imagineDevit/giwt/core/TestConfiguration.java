package io.github.imagineDevit.giwt.core;

import io.github.imagineDevit.giwt.core.annotations.ConfigureWith;

import java.util.Set;


/**
 * Test configuration class
 *
 * @author Henri Joel SEDJAME
 * @see ConfigureWith
 * @since 0.0.1
 */
public interface TestConfiguration {
    default Set<Class<?>> excludeFromReport() {
        return Set.of();
    }
}
