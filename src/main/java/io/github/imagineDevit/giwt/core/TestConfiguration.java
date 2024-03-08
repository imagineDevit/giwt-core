package io.github.imagineDevit.giwt.core;

import io.github.imagineDevit.giwt.core.annotations.ConfigureWith;

import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * Test configuration class
 *
 * @author Henri Joel SEDJAME
 * @see ConfigureWith
 * @since 0.0.1
 */
public interface TestConfiguration {

    Map<String, TestParameters<?>> parameterSources();

    default Set<Class<?>> excludeFromReport() {
        return Set.of();
    }

    default Optional<TestParameters<?>> getParameters(String name) {
        return Optional.ofNullable(parameterSources().get(name));
    }
}
