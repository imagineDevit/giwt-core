package io.github.imagineDevit.giwt.core.errors;

import static io.github.imagineDevit.giwt.core.utils.Messages.*;

public final class TestMethodException extends GiwtError {

    private static final String testCase = italicPurple.apply("? extends ATestCase<>");
    private final Reasons reason;

    public TestMethodException(String methodName, Reasons reason) {
        super(getReasonMessage(reason, methodName));
        this.reason = reason;
    }

    private static String getReasonMessage(Reasons reason, String methodName) {
        return switch (reason) {
            case IS_STATIC -> NO_SUPPORT_FOR_STATIC_METHODS.formatted(methodName);
            case IS_ABSTRACT -> NO_SUPPORT_FOR_ABSTRACT_METHODS.formatted(methodName);
            case IS_PRIVATE -> NO_SUPPORT_FOR_PRIVATE_METHODS.formatted(methodName);
            case DO_NOT_HAVE_EXACTLY_ONE_ARG -> TEST_METHOD_ARG_TYPE.formatted(methodName, testCase);
            case HAS_BAD_ARG_TYPE -> TEST_METHOD_BAD_ARG_TYPE.formatted(methodName, testCase);
            case DO_NOT_RETURN_VOID -> TEST_METHOD_SHOULD_RETURN_VOID.formatted(methodName);
        };
    }

    public Reasons getReason() {
        return reason;
    }

    public enum Reasons {
        IS_STATIC,
        IS_ABSTRACT,
        IS_PRIVATE,
        DO_NOT_HAVE_EXACTLY_ONE_ARG,
        HAS_BAD_ARG_TYPE,
        DO_NOT_RETURN_VOID
    }
}
