package io.github.imagineDevit.giwt.core.context;

import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.*;
import io.github.imagineDevit.giwt.core.callbacks.*;
import io.github.imagineDevit.giwt.core.errors.MultipleParamSourcesFoundException;
import io.github.imagineDevit.giwt.core.errors.NoParamSourceFoundException;
import io.github.imagineDevit.giwt.core.utils.GiwtPredicates;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ContextUtils {

    static TestConfiguration getConfiguration(Class<?> testClass) {
        return Optional.ofNullable(testClass.getAnnotation(ConfigureWith.class))
                .map(ConfigureWith::value)
                .map(ReflectionUtils::newInstance)
                .orElse(null);
    }

    static GiwtCallbacks getCallbacks(Object testInstance) {

        return new GiwtCallbacks(
                () ->
                        runCallbacks(
                                getBeforeAllMethods(testInstance),
                                m -> Optional.ofNullable(m.getAnnotation(BeforeAll.class)).map(BeforeAll::order).orElse(0)
                        ),
                () ->
                        runCallbacks(
                                getAfterAllMethods(testInstance),
                                m -> Optional.ofNullable(m.getAnnotation(AfterAll.class)).map(AfterAll::order).orElse(0)
                        ),
                () ->
                        runCallbacks(
                                getBeforeEachMethods(testInstance),
                                m -> Optional.ofNullable(m.getAnnotation(BeforeEach.class)).map(BeforeEach::order).orElse(0)
                        ),
                () ->
                        runCallbacks(
                                getAfterEachMethods(testInstance),
                                m -> Optional.ofNullable(m.getAnnotation(AfterEach.class)).map(AfterEach::order).orElse(0)
                        )

        );
    }

    @SuppressWarnings("unchecked")
    static List<? extends TestParameters.Parameter> getParameters(Method method, TestConfiguration configuration) {
        var methodSource = getParameterSource(method, configuration);

        var instance = ReflectionUtils.newInstance(methodSource.getDeclaringClass());

        var testParameters = (TestParameters<TestParameters.Parameter>) ReflectionUtils.invokeMethod(methodSource, instance);

        return testParameters.getParameters();
    }

    static Method getParameterSource(Method method, TestConfiguration configuration) {
        ParameterizedTest annotation = method.getAnnotation(ParameterizedTest.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Method is not annotated with @ParameterizedTest");
        }

        var testClass = method.getDeclaringClass();

        var parameterSource = annotation.source();

        var methods = Optional.of(getParameterSourcesMethods(testClass, parameterSource))
                .filter(list -> !list.isEmpty())
                .orElseGet(() -> {
                    if (configuration != null) {
                        return getParameterSourcesMethods(configuration.getClass(), parameterSource);
                    }
                    return Collections.emptyList();
                });

        return switch (methods.size()) {
            case 0 -> throw new NoParamSourceFoundException(parameterSource);
            case 1 -> methods.get(0);
            default -> throw new MultipleParamSourcesFoundException(parameterSource);
        };
    }

    private static List<Method> getParameterSourcesMethods(Class<?> clazz, String parameterSource) {

        Predicate<Method> hasName = method ->
                Optional.of(method.getAnnotation(ParameterSource.class).value())
                        .filter(source -> !source.isEmpty())
                        .orElse(method.getName())
                        .equals(parameterSource);

        return ReflectionUtils.findMethods(clazz, (Method m) -> GiwtPredicates.isParameterSource().test(m) && hasName.test(m));
    }

    private static void runCallbacks(Map<Object, List<Method>> methods, Function<Method, Integer> order) {
        methods
                .forEach((instance, ms) ->
                        ms.stream().sorted(Comparator.comparing(order))
                                .forEach(m -> ReflectionUtils.invokeMethod(m, instance))
                );
    }

    private static Map<Object, List<Method>> getBeforeAllMethods(Object testInstance) {
        return getCallbackMethods(testInstance, BeforeAll.class, BeforeAllCallback.class, Callback.Methods.BEFORE_ALL);
    }

    private static Map<Object, List<Method>> getAfterAllMethods(Object testInstance) {
        return getCallbackMethods(testInstance, AfterAll.class, AfterAllCallback.class, Callback.Methods.AFTER_ALL);
    }

    private static Map<Object, List<Method>> getBeforeEachMethods(Object testInstance) {
        return getCallbackMethods(testInstance, BeforeEach.class, BeforeEachCallback.class, Callback.Methods.BEFORE_EACH);
    }

    private static Map<Object, List<Method>> getAfterEachMethods(Object testInstance) {
        return getCallbackMethods(testInstance, AfterEach.class, AfterEachCallback.class, Callback.Methods.AFTER_EACH);
    }

    private static Map<Object, List<Method>> getCallbackMethods(Object testInstance, Class<? extends Annotation> annotationClazz, Class<? extends Callback> callbackClazz, String callbackMethod) {
        var map = new HashMap<Object, List<Method>>();
        var testClass = testInstance.getClass();

        map.put(testInstance, (ReflectionUtils.findMethods(testClass, m -> AnnotationSupport.isAnnotated(m, annotationClazz))));

        Optional.ofNullable(testClass.getAnnotation(ExtendWith.class))
                .map(ExtendWith::value)
                .map(Arrays::asList)
                .orElse(new ArrayList<>())
                .forEach(clazz -> {
                    if (callbackClazz.isAssignableFrom(clazz)) {
                        ReflectionUtils.findMethod(clazz, callbackMethod)
                                .ifPresent(method ->
                                        map.put(ReflectionUtils.newInstance(clazz), Collections.singletonList(method))
                                );
                    }
                });

        return map;
    }

}
