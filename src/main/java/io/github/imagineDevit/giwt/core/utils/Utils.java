package io.github.imagineDevit.giwt.core.utils;


import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.ParameterizedTest;
import io.github.imagineDevit.giwt.core.annotations.Test;
import io.github.imagineDevit.giwt.core.context.GiwtContext;
import io.github.imagineDevit.giwt.core.errors.DuplicateTestNameException;
import io.github.imagineDevit.giwt.core.statements.StmtMsg;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static String getTestName(Method method) {
        return Optional.ofNullable(method.getAnnotation(Test.class))
                .map(Test::value)
                .filter(s -> !s.isEmpty())
                .or(() -> Optional.ofNullable(method.getAnnotation(ParameterizedTest.class))
                        .map(ParameterizedTest::name)
                        .filter(s -> !s.isEmpty()))
                .orElse(method.getName());
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

    @SuppressWarnings("unchecked")
    public static <T, E extends Throwable> Comparable<T> asComparableOrThrow(T value, Supplier<E> eSupplier) throws E {
        if (value instanceof Comparable<?> c) {
            return (Comparable<T>) c;
        } else {
            throw eSupplier.get();
        }
    }

    public static void checkTestNamesDuplication(Object testInstance) {
        var methods = GiwtTestEngine.CONTEXT.get(testInstance).testMethods().stream()
                .map(GiwtContext.TestMethod::method)
                .toList();

        var duplicatedTestNames = findDuplicatedTestNames(methods);

        if (!duplicatedTestNames.isEmpty()) {
            throw new DuplicateTestNameException(duplicatedTestNames);
        }
    }

    private static List<String> findDuplicatedTestNames(List<Method> methods) {
        return methods.stream().map(Utils::getTestName)
                .reduce(new HashMap<String, Integer>(), (map, name) -> {
                    map.put(name, map.getOrDefault(name, 0) + 1);
                    return map;
                }, (m1, m2) -> {
                    m1.putAll(m2);
                    return m1;
                }).entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();
    }

}
