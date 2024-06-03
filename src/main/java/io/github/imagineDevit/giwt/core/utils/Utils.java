package io.github.imagineDevit.giwt.core.utils;


import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.ParameterizedTest;
import io.github.imagineDevit.giwt.core.annotations.Test;
import io.github.imagineDevit.giwt.core.context.ClassCtx;
import io.github.imagineDevit.giwt.core.errors.DuplicateTestNameException;
import io.github.imagineDevit.giwt.core.statements.StmtMsg;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.github.imagineDevit.giwt.core.utils.TextUtils.*;

/**
 * This class contains utility methods that are used to run test callbacks, to get test parameters and other utility methods.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class Utils {

    public static final int MAX_LENGTH = 100;

    public static final String DASH = bold("-".repeat(MAX_LENGTH));


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
        var n = name.trim();

        if (parameters != null) n = parameters.formatName(n);

        var title = purple(n);

        var givenMsg = givenMsgs.stream().map(StmtMsg::value).collect(Collectors.joining("\n"));
        var whenMsg = whenMsgs.stream().map(StmtMsg::value).collect(Collectors.joining("\n"));
        var thenMsg = thenMsgs.stream().map(StmtMsg::value).collect(Collectors.joining("\n"));

        var sb = new StringBuilder();

        var lineLength = DASH.length() - (title.toLowerCase().length() + 5);
        var part1 = bold("🎬[ ");
        var part2 = bold(" ]" + "-".repeat(lineLength));

        sb.append("""
                %s%s%s
                """.formatted(part1, title, part2));


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

        return sb.toString();
    }


    /**
     * @return a string representation of the title of the list of expectations section.
     */
    public static String listExpectations() {
        return """
                
                  ----------------------
                  %s
                  ----------------------
                """.formatted(bold(italic(yellow(" List of expectations"))));
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
        var methods = GiwtTestEngine.CONTEXT.get(testClass).testMethods().stream()
                .map(ClassCtx.TestMethod::method)
                .toList();

        var duplicatedTestNames = findDuplicatedTestNames(methods);

        if (!duplicatedTestNames.isEmpty()) {
            throw new DuplicateTestNameException(duplicatedTestNames);
        }
    }

    public static List<String> splitStr(String str, int len) {
        var list = new ArrayList<String>();

        if (str.length() <= len) {
            list.add(str);
            return list;
        }

        var words = str.split(" ");

        var sb = new StringBuilder();

        for (var word : words) {
            if (sb.length() + word.length() + 1 <= len) {
                sb.append(word).append(" ");
            } else {
                list.add(sb.toString());
                sb = new StringBuilder();
                sb.append(" ".repeat(13));
                sb.append(word).append(" ");
            }
        }

        list.add(sb.toString());
        return list;
    }

    public static String formatReason(String reason) {
        return String.join("\n", splitStr(reason, (MAX_LENGTH - 15)));
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
