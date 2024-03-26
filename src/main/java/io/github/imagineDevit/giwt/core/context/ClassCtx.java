package io.github.imagineDevit.giwt.core.context;

import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.callbacks.GiwtCallbacks;

import java.lang.reflect.Method;
import java.util.*;

public record ClassCtx<T>(
        T instance,
        TestConfiguration configuration,
        GiwtCallbacks callbacks,
        Map<String, List<? extends TestParameters.Parameter>> parameters,
        List<TestMethod> testMethods) {

    public ClassCtx {
        Objects.requireNonNull(instance);
        parameters = Objects.requireNonNullElse(parameters, new HashMap<>());
        testMethods = Objects.requireNonNullElse(testMethods, new ArrayList<>());
    }

    public record TestMethod(Boolean isParameterized, Method method) {
        public TestMethod {
            Objects.requireNonNull(isParameterized);
            Objects.requireNonNull(method);
        }
    }
}

