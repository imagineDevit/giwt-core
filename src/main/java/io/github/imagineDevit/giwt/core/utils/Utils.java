package io.github.imagineDevit.giwt.core.utils;


import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.ParameterizedTest;
import io.github.imagineDevit.giwt.core.annotations.Test;
import io.github.imagineDevit.giwt.core.errors.DuplicateTestNameException;
import io.github.imagineDevit.giwt.core.statements.StmtMsg;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class contains utility methods that are used to run test callbacks, to get test parameters and other utility methods.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class Utils {

    public static final String DASH = "-".repeat(50);

    public static String getTestName(String name, Method method) {
        if (name.isEmpty()) return method.getName();
        else return name;
    }

    public static String reportTestCase(String name, List<StmtMsg> givenMsgs, List<StmtMsg> whenMsgs, List<StmtMsg> thenMsgs, TestParameters.Parameter parameters) {
        var n = name;

        if (parameters != null) n = parameters.formatName(n);

        var title = TextUtils.bold("▶️" + TextUtils.italic(TextUtils.purple(n)));

        var givenMsg = givenMsgs.stream().map(StmtMsg::value).collect(Collectors.joining("\n"));
        var whenMsg = whenMsgs.stream().map(StmtMsg::value).collect(Collectors.joining("\n"));
        var thenMsg = thenMsgs.stream().map(StmtMsg::value).collect(Collectors.joining("\n"));

        var sb = new StringBuilder();

        sb.append(
                """
                        %s
                        %s
                        %s
                        """.formatted(DASH, title, DASH)
        );

        if (!givenMsg.isEmpty()) {
            sb.append("""
                    %s
                    """.formatted(givenMsg));
        }

        if (!whenMsg.isEmpty()) {
            sb.append("""
                    %s
                    """.formatted(whenMsg));
        }

        if (!thenMsg.isEmpty()) {
            sb.append("""
                    %s
                    """.formatted(thenMsg));
        }

        sb.append("""
                %s
                """.formatted(DASH));

        return sb.toString();
    }

    public static <S> S runIfOpen(boolean closed, Supplier<S> fn, Runnable close) {
        if (closed) {
            throw new IllegalStateException("""
                                        \s
                     Test case is already closed.
                     A test case can only be run once.
                    \s""");
        }
        close.run();
        return fn.get();
    }

    @SuppressWarnings("unchecked")
    public static <T, E extends Throwable> Comparable<T> asComparableOrThrow(T value, Supplier<E> eSupplier) throws E {
        if (value instanceof Comparable<?> c) {
            return (Comparable<T>) c;
        } else {
            throw eSupplier.get();
        }
    }

    public static void checkTestNamesDuplication(Class<?> testClass) {
        var methods = ReflectionUtils.findMethods(testClass, GiwtPredicates.isMethodTest().or(GiwtPredicates.isParameterizedMethodTest()));

        var duplicatedTestNames = findDuplicatedTestNames(methods);

        if (!duplicatedTestNames.isEmpty()) {
            throw new DuplicateTestNameException(duplicatedTestNames);
        }
    }

    private static List<String> findDuplicatedTestNames(List<Method> methods) {
        var testNames = methods.stream()
                .map(m ->
                        Optional.ofNullable(m.getAnnotation(Test.class))
                                .map(Test::value)
                                .orElseGet(() -> m.getAnnotation(ParameterizedTest.class).name())
                ).toList();

        return testNames.stream()
                .distinct()
                .filter(s -> !s.isEmpty())
                .filter(s -> testNames.stream().filter(tn -> tn.equals(s)).count() > 1)
                .toList();
    }

}
