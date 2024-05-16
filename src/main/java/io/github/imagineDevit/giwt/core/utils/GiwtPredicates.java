package io.github.imagineDevit.giwt.core.utils;

import io.github.imagineDevit.giwt.core.ATestCase;
import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.ParameterSource;
import io.github.imagineDevit.giwt.core.annotations.ParameterizedTest;
import io.github.imagineDevit.giwt.core.annotations.Test;
import io.github.imagineDevit.giwt.core.errors.ParameterSourceException;
import io.github.imagineDevit.giwt.core.errors.ParameterizedTestMethodException;
import io.github.imagineDevit.giwt.core.errors.TestClassException;
import io.github.imagineDevit.giwt.core.errors.TestMethodException;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.github.imagineDevit.giwt.core.errors.TestClassException.Reasons.IS_ABSTRACT;
import static io.github.imagineDevit.giwt.core.errors.TestClassException.Reasons.IS_PRIVATE;
import static io.github.imagineDevit.giwt.core.utils.Matchers.MatchCase.matchCase;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.FailureArg.ExceptionArg;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.failure;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.success;
import static io.github.imagineDevit.giwt.core.utils.Matchers.match;
import static io.github.imagineDevit.giwt.core.utils.TextUtils.*;
import static org.junit.platform.commons.util.ReflectionUtils.*;

/**
 * This class contains predicates that are used to filter test classes and methods.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class GiwtPredicates {

    private static final Function<Class<?>, String> className = clazz -> bold(green(clazz.getSimpleName()));
    private static final Function<Method, String> methodName = method -> bold(blue(method.getName()));

    public static Predicate<Class<?>> hasTestMethods() {
        return clazz -> match(
                matchCase(
                        () -> isAbstract(clazz),
                        () -> failure(new ExceptionArg(() -> new TestClassException(className.apply(clazz), IS_ABSTRACT)))
                ),
                matchCase(
                        () -> isPrivate(clazz),
                        () -> failure(new ExceptionArg(() -> new TestClassException(className.apply(clazz), IS_PRIVATE)))
                ),
                matchCase(
                        () -> !findMethods(clazz, m -> AnnotationSupport.isAnnotated(m, Test.class)
                                || AnnotationSupport.isAnnotated(m, ParameterizedTest.class)).isEmpty(),
                        () -> success(Boolean.TRUE)
                )
        ).orElse(false);
    }

    public static Predicate<Class<?>> isTestClass() {
        return clazz -> match(
                matchCase(
                        () -> isAbstract(clazz),
                        () -> failure(new ExceptionArg(() -> new TestClassException(className.apply(clazz), IS_ABSTRACT)))
                ),
                matchCase(
                        () -> isPrivate(clazz),
                        () -> failure(new ExceptionArg(() -> new TestClassException(className.apply(clazz), IS_PRIVATE)))
                ),
                matchCase(
                        () -> {
                            var testMethods = findMethods(clazz, GiwtPredicates::isTestMethod);
                            GiwtTestEngine.CONTEXT.addTestMethod(clazz, testMethods, false);
                            var parameterizedTestMethods = findMethods(clazz, GiwtPredicates::isParameterizedTestMethod);
                            GiwtTestEngine.CONTEXT.addTestMethod(clazz, parameterizedTestMethods, true);
                            return !testMethods.isEmpty() || !parameterizedTestMethods.isEmpty();
                        },
                        () -> success(Boolean.TRUE)
                )
        ).orElse(false);
    }

    public static Predicate<Method> isMethodTest() {
        return method -> match(
                matchCase(
                        () -> isStatic(method),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.IS_STATIC)))
                ),
                matchCase(
                        () -> isPrivate(method),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.IS_PRIVATE)))
                ),
                matchCase(
                        () -> isAbstract(method),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.IS_ABSTRACT)))
                ),
                matchCase(() -> isTestMethod(method), () -> success(Boolean.TRUE))
        ).orElse(false);
    }

    public static Predicate<Method> isParameterizedMethodTest() {
        return method -> match(
                matchCase(
                        () -> isStatic(method),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.IS_STATIC)))
                ),
                matchCase(
                        () -> isPrivate(method),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.IS_PRIVATE)))
                ),
                matchCase(
                        () -> isAbstract(method),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.IS_ABSTRACT)))
                ),
                matchCase(() -> isParameterizedTestMethod(method), () -> success(Boolean.TRUE))
        ).orElse(false);
    }

    public static Predicate<Method> isParameterSource(boolean shouldBePublic) {
        return method -> match(
                matchCase(
                        () -> !method.getReturnType().isAssignableFrom(TestParameters.class),
                        () -> failure(new ExceptionArg(() -> new ParameterSourceException(methodName.apply(method), ParameterSourceException.Reasons.DO_NOT_RETURN_VOID)))
                ),
                matchCase(
                        () -> shouldBePublic && !isPublic(method),
                        () -> failure(new ExceptionArg(() -> new ParameterSourceException(methodName.apply(method), ParameterSourceException.Reasons.IS_NOT_PUBLIC)))
                ),
                matchCase(
                        () -> AnnotationSupport.isAnnotated(method, ParameterSource.class),
                        () -> success(Boolean.TRUE)
                )
        ).orElse(false);
    }

    private static boolean isTestMethod(Method method) {
        return match(
                matchCase(
                        () -> method.getParameterCount() != 1,
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.DO_NOT_HAVE_EXACTLY_ONE_ARG)))
                ),
                matchCase(
                        () -> !ATestCase.class.isAssignableFrom(method.getParameterTypes()[0]),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.HAS_BAD_ARG_TYPE)))
                ),
                matchCase(
                        () -> !method.getReturnType().equals(Void.TYPE),
                        () -> failure(new ExceptionArg(() -> new TestMethodException(methodName.apply(method), TestMethodException.Reasons.DO_NOT_RETURN_VOID)))
                ),
                matchCase(
                        () -> AnnotationSupport.isAnnotated(method, Test.class),
                        () -> success(Boolean.TRUE))
        ).orElse(false);
    }

    private static boolean isParameterizedTestMethod(Method method) {

        final Class<?>[] allArgs = method.getParameterTypes();
        final AtomicReference<Class<?>[]> methodParamTypes = new AtomicReference<>();
        final AtomicReference<Type[]> pTypes = new AtomicReference<>();
        final AtomicBoolean methodSourceFound = new AtomicBoolean(false);

        return match(
                matchCase(
                        () -> method.getAnnotation(ParameterizedTest.class).source().isEmpty(),
                        () -> failure(new ExceptionArg(() -> new ParameterizedTestMethodException(methodName.apply(method), ParameterizedTestMethodException.Reasons.HAS_EMPTY_PARAM_SOURCE))
                        )
                ),
                matchCase(
                        () -> method.getParameterCount() <= 1,
                        () -> failure(new ExceptionArg(() -> new ParameterizedTestMethodException(methodName.apply(method), ParameterizedTestMethodException.Reasons.DO_NOT_HAVE_MORE_THAN_ONE_ARG)))
                ),
                matchCase(
                        () -> !ATestCase.class.isAssignableFrom(allArgs[0]),
                        () -> failure(new ExceptionArg(() -> new ParameterizedTestMethodException(methodName.apply(method), ParameterizedTestMethodException.Reasons.HAS_BAD_FIRST_ARG_TYPE)))
                ),
                matchCase(
                        () -> {

                            var sourceMethod = GiwtTestEngine.CONTEXT.getParameterSource(method);

                            methodSourceFound.set(true);

                            Type[] pt = (
                                    (ParameterizedType) (
                                            (ParameterizedType) sourceMethod.getGenericReturnType()
                                    ).getActualTypeArguments()[0]
                            ).getActualTypeArguments();

                            pTypes.set(pt);

                            methodParamTypes.set(Arrays.copyOfRange(allArgs, 1, allArgs.length));

                            return !methodSourceFound.get() || pt.length != methodParamTypes.get().length;
                        },
                        () -> methodSourceFound.get()
                                ? failure(new ExceptionArg(() -> new ParameterizedTestMethodException(methodName.apply(method), pTypes.get(), methodParamTypes.get(), ParameterizedTestMethodException.Reasons.HAS_BAD_ARGS_NUMBER)))
                                : failure(new ExceptionArg(() -> new ParameterSourceException(method.getAnnotation(ParameterizedTest.class).source(), ParameterSourceException.Reasons.NOT_FOUND)))
                ),
                matchCase(
                        () -> {
                            var mpt = methodParamTypes.get();
                            var pt = pTypes.get();
                            for (int i = 0; i < mpt.length; i++) {
                                String typeName = mpt[i].getTypeName();
                                String expectedType = pt[i].getTypeName();
                                if (!areTypesEqual(typeName, expectedType)) return true;
                            }
                            return false;
                        },
                        () -> failure(new ExceptionArg(() -> new ParameterizedTestMethodException(methodName.apply(method), pTypes.get(), methodParamTypes.get(), ParameterizedTestMethodException.Reasons.HAS_BAD_ARGS_TYPES)))
                ),
                matchCase(
                        () -> !method.getReturnType().equals(Void.TYPE),
                        () -> failure(new ExceptionArg(() -> new ParameterizedTestMethodException(methodName.apply(method), ParameterizedTestMethodException.Reasons.DO_NOT_RETURN_VOID)))
                ),
                matchCase(
                        () -> AnnotationSupport.isAnnotated(method, ParameterizedTest.class),
                        () -> success(Boolean.TRUE)
                )
        ).orElse(false);
    }

    private static boolean areTypesEqual(String type1, String type2) {
        List<String> types1 = typeNames().getOrDefault(type1, List.of(type1));
        List<String> types2 = typeNames().getOrDefault(type2, List.of(type2));
        return types1.stream().anyMatch(t1 -> types2.stream().anyMatch(t1::equals));
    }

    private static Map<String, List<String>> typeNames() {
        return Map.ofEntries(
                new ImmutablePair<>("int", List.of("java.lang.Integer", "java.lang.Number")),
                new ImmutablePair<>("kotlin.Int", List.of("java.lang.Integer", "java.lang.Number")),
                new ImmutablePair<>("long", List.of("java.lang.Long", "java.lang.Number")),
                new ImmutablePair<>("kotlin.Long", List.of("java.lang.Long", "java.lang.Number")),
                new ImmutablePair<>("short", List.of("java.lang.Short", "java.lang.Number")),
                new ImmutablePair<>("kotlin.Short", List.of("java.lang.Short", "java.lang.Number")),
                new ImmutablePair<>("float", List.of("java.lang.Float", "java.lang.Number")),
                new ImmutablePair<>("kotlin.Float", List.of("java.lang.Float", "java.lang.Number")),
                new ImmutablePair<>("double", List.of("java.lang.Double", "java.lang.Number")),
                new ImmutablePair<>("kotlin.Double", List.of("java.lang.Double", "java.lang.Number")),
                new ImmutablePair<>("byte", List.of("java.lang.Byte", "java.lang.Number")),
                new ImmutablePair<>("kotlin.Byte", List.of("java.lang.Byte", "java.lang.Number")),
                new ImmutablePair<>("boolean", List.of("java.lang.Boolean")),
                new ImmutablePair<>("kotlin.Boolean", List.of("java.lang.Boolean")),
                new ImmutablePair<>("char", List.of("java.lang.Character")),
                new ImmutablePair<>("kotlin.Char", List.of("java.lang.Character")),
                new ImmutablePair<>("String", List.of("java.lang.String")),
                new ImmutablePair<>("kotlin.String", List.of("java.lang.String"))
        );
    }
}
