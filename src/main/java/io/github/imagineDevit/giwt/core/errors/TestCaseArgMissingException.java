package io.github.imagineDevit.giwt.core.errors;

import io.github.imagineDevit.giwt.core.ATestCase;
import io.github.imagineDevit.giwt.core.utils.TextUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception thrown when an argument of type {@code <? extends ATestCase>}is missing for a test method
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 * @see ATestCase
 */
public final class TestCaseArgMissingException extends GiwtError {

    public TestCaseArgMissingException(List<String> methodNames) {
        super(errorMessage(methodNames));
    }

    public TestCaseArgMissingException(String methodName) {
        super(errorMessage(methodName));
    }

    private static String errorMessage(String methodName) {
        return "Missing argument of type %s for test method %s.".formatted(TextUtils.purple("TestCase"), methodName);
    }

    private static String errorMessage(List<String> names){
        if (names.size() == 1) return errorMessage(names.get(0));

        String testMethods = names.stream()
                .map("'%s'"::formatted)
                .map(TextUtils::green)
                .collect(Collectors.joining(", "));

        return "Missing argument of type %s for following test methods: [ %s ].".formatted(TextUtils.purple("TestCase"), testMethods);
    }
}
