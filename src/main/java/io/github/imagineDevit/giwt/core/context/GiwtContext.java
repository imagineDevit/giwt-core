package io.github.imagineDevit.giwt.core.context;

import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.callbacks.GiwtCallbacks;

import java.lang.reflect.Method;
import java.util.*;

public record GiwtContext(Map<Class<?>, ClassCtx> items) {

    public GiwtContext {
        items = Objects.requireNonNullElse(items, new HashMap<>());
    }

    public ClassCtx get(Object testInstance) {
        var testClass = testInstance.getClass();
        if (items.containsKey(testClass)) {
            return items.get(testClass);
        }
        add(testInstance);
        return get(testInstance);
    }

    public TestConfiguration getConfiguration(Object testInstance) {
        return get(testInstance).configuration();
    }

    public GiwtCallbacks getCallbacks(Object testInstance) {
        return get(testInstance).callbacks();
    }

    public List<? extends TestParameters.Parameter> getParameters(Object testInstance, Method method) {

        ClassCtx ctx = get(testInstance);

        Map<String, List<? extends TestParameters.Parameter>> parameters = ctx.parameters();

        String name = method.getName();

        if (parameters.containsKey(name)) return parameters.get(name);

        addParameters(testInstance, method, ContextUtils.getParameters(method, ctx.configuration()));

        return getParameters(testInstance, method);
    }

    public Method getParameterSource(Method method) {
        return ContextUtils.getParameterSource(method, getConfiguration(method.getDeclaringClass()).orElse(null));
    }

    private Optional<ClassCtx> get(Class<?> testClass) {
        if (items.containsKey(testClass)) {
            return Optional.ofNullable(items.get(testClass));
        }
        return Optional.empty();
    }

    public void add(Object testInstance) {
        var testClass = testInstance.getClass();
        items.putIfAbsent(
                testClass,
                new ClassCtx(ContextUtils.getConfiguration(testClass), ContextUtils.getCallbacks(testInstance), new HashMap<>())
        );
    }

    public void remove(Object testInstance) {
        items.remove(testInstance.getClass());
    }

    private void addParameters(Object testInstance, Method method, List<? extends TestParameters.Parameter> ps) {
        get(testInstance).parameters.putIfAbsent(method.getName(), ps);
    }

    public Optional<TestConfiguration> getConfiguration(Class<?> testClass) {
        return get(testClass).map(ClassCtx::configuration);
    }

    public record ClassCtx(TestConfiguration configuration, GiwtCallbacks callbacks,
                           Map<String, List<? extends TestParameters.Parameter>> parameters) {
        public ClassCtx {
            parameters = Objects.requireNonNullElse(parameters, new HashMap<>());
        }
    }

}
