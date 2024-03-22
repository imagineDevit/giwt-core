package io.github.imagineDevit.giwt.core.utils;

import io.github.imagineDevit.giwt.core.ATestCase;
import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.ParameterSource;
import io.github.imagineDevit.giwt.core.annotations.ParameterizedTest;
import io.github.imagineDevit.giwt.core.annotations.Test;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static io.github.imagineDevit.giwt.core.utils.Matchers.MatchCase.matchCase;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.failure;
import static io.github.imagineDevit.giwt.core.utils.Matchers.Result.success;
import static io.github.imagineDevit.giwt.core.utils.Matchers.match;
import static org.junit.platform.commons.util.ReflectionUtils.*;

/**
 * This class contains predicates that are used to filter test classes and methods.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class GiwtPredicates {

    public static Predicate<Class<?>> hasTestMethods() {
        return clazz -> match(
                matchCase(
                        () -> isAbstract(clazz),
                        () -> failure("No support for abstract classes " + clazz.getSimpleName())
                ),
                matchCase(
                        () -> isPrivate(clazz),
                        () -> failure("No support for private classes " + clazz.getSimpleName())
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
                        () -> failure("No support for abstract classes " + clazz.getSimpleName())
                ),
                matchCase(
                        () -> isPrivate(clazz),
                        () -> failure("No support for private classes " + clazz.getSimpleName())
                ),
                matchCase(
                        () -> !findMethods(clazz, m -> isTestMethod(m) || isParameterizedTestMethod(m)).isEmpty(),
                        () -> success(Boolean.TRUE)
                )
        ).orElse(false);
    }

    public static Predicate<Method> isMethodTest() {
        return method -> match(
                matchCase(
                        () -> isStatic(method),
                        () -> failure("No support for static methods " + method.getName())
                ),
                matchCase(
                        () -> isPrivate(method),
                        () -> failure("No support for private methods " + method.getName())
                ),
                matchCase(
                        () -> isAbstract(method),
                        () -> failure("No support for abstract methods " + method.getName())
                ),
                matchCase(() -> isTestMethod(method), () -> success(Boolean.TRUE))
        ).orElse(false);
    }

    public static Predicate<Method> isParameterizedMethodTest() {
        return method -> match(
                matchCase(
                        () -> isStatic(method),
                        () -> failure("No support for static methods " + method.getName())
                ),
                matchCase(
                        () -> isPrivate(method),
                        () -> failure("No support for private methods " + method.getName())
                ),
                matchCase(
                        () -> isAbstract(method),
                        () -> failure("No support for abstract methods " + method.getName())
                ),
                matchCase(() -> isParameterizedTestMethod(method), () -> success(Boolean.TRUE))
        ).orElse(false);
    }

    public static Predicate<Method> isParameterSource() {
        return method -> match(
                matchCase(
                        () -> !method.getReturnType().isAssignableFrom(TestParameters.class),
                        () -> failure("Method annotated with %s should return object of type %s".formatted(TextUtils.blue("@ParameterSource"), TextUtils.green("TestParameters<?>")))
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
                        () -> failure("Test method (%s) should have one argument of type %s".formatted(TextUtils.blue(method.getName()), TextUtils.purple("TestCase")))),
                matchCase(
                        () -> !ATestCase.class.isAssignableFrom(method.getParameterTypes()[0]),
                        () -> failure("Missing argument of type %s for test method %s.".formatted(TextUtils.purple("TestCase"), TextUtils.blue(method.getName())))
                ),
                matchCase(
                        () -> !method.getReturnType().equals(Void.TYPE),
                        () -> failure("Test method should return void")),
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

        final String methodName = TextUtils.blue(method.getName());

        return match(
                matchCase(
                        () -> method.getAnnotation(ParameterizedTest.class).source().isEmpty(),
                        () -> failure("%s source should not be empty".formatted(TextUtils.blue("@ParameterizedTest"))
                        )
                ),
                matchCase(
                        () -> method.getParameterCount() <= 1,
                        () -> failure("Parameterized method test %s should have more than one argument".formatted(methodName))
                ),
                matchCase(
                        () -> !ATestCase.class.isAssignableFrom(allArgs[0]),
                        () -> failure("The first argument of the test method (%s) should be of type %s.".formatted(methodName, TextUtils.purple("TestCase")))
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
                                ? failure(() -> "Test method %s expected to have <%d>  but got <%d> arguments".formatted(methodName, pTypes.get().length + 1, methodParamTypes.get().length + 1))
                                : failure(() -> "No parameter source found with name %s".formatted(TextUtils.blue(method.getAnnotation(ParameterizedTest.class).source())))
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
                        () -> failure(() -> "Test method %s expected to have arguments of types : %s  but got %s".formatted(methodName, TextUtils.bold(Arrays.toString(pTypes.get())), TextUtils.bold(Arrays.toString(methodParamTypes.get())))
                        )
                ),
                matchCase(
                        () -> !method.getReturnType().equals(Void.TYPE),
                        () -> failure("Test method should return void")
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
