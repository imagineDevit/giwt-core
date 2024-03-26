package io.github.imagineDevit.giwt.core.context;

import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.callbacks.GiwtCallbacks;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

public record GiwtContext(Map<Class<?>, ClassCtx<?>> items) {

    public GiwtContext {
        items = Objects.requireNonNullElse(items, new HashMap<>());
    }

    public ClassCtx<?> get(Class<?> testClass) {
        if (items.containsKey(testClass)) {
            return items.get(testClass);
        }
        add(testClass);
        return get(testClass);
    }

    public Object getInstanceOf(Class<?> testClass) {
        return get(testClass).instance();
    }

    public Optional<TestConfiguration> getConfiguration(Class<?> testClass) {
        return Optional.ofNullable(get(testClass).configuration());
    }

    public GiwtCallbacks getCallbacks(Class<?> testClass) {
        return get(testClass).callbacks();
    }

    public List<? extends TestParameters.Parameter> getParameters(Class<?> testClass, Method method) {

        ClassCtx<?> ctx = get(testClass);

        Map<String, List<? extends TestParameters.Parameter>> parameters = ctx.parameters();

        String name = method.getName();

        if (parameters.containsKey(name)) return parameters.get(name);

        addParameters(testClass, method, ContextUtils.getParameters(method, ctx.configuration()));

        return getParameters(testClass, method);
    }

    public Method getParameterSource(Method method) {
        return ContextUtils.getParameterSource(method, getConfiguration(method.getDeclaringClass()).orElse(null));
    }

    public void add(Class<?> testClass) {
        var testInstance = ReflectionUtils.newInstance(testClass);
        items.putIfAbsent(
                testClass,
                new ClassCtx<>(testInstance, ContextUtils.getConfiguration(testClass), ContextUtils.getCallbacks(testInstance), new HashMap<>(), new ArrayList<>())
        );
    }

    public void add(Object testInstance) {
        items.putIfAbsent(
                testInstance.getClass(),
                new ClassCtx<>(testInstance, ContextUtils.getConfiguration(testInstance.getClass()), ContextUtils.getCallbacks(testInstance), new HashMap<>(), new ArrayList<>())
        );
    }

    public void remove(Class<?> testClass) {
        items.remove(testClass);
    }

    public void addTestMethod(Class<?> testClass, List<Method> methods, boolean isParameterized) {
        get(testClass).testMethods().addAll(methods.stream().map(m -> new ClassCtx.TestMethod(isParameterized, m)).toList());
    }

    private void addParameters(Class<?> testClass, Method method, List<? extends TestParameters.Parameter> ps) {
        get(testClass).parameters().putIfAbsent(method.getName(), ps);
    }

}
