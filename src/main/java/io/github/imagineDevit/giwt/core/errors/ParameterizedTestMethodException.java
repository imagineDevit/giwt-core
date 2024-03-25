package io.github.imagineDevit.giwt.core.errors;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

import static io.github.imagineDevit.giwt.core.utils.Messages.*;
import static io.github.imagineDevit.giwt.core.utils.TextUtils.bold;

public final class ParameterizedTestMethodException extends GiwtError {

    private static final Function<Integer, String> number = num -> bold(num.toString());
    private static final String testCase = italicPurple.apply("? extends ATestCase<>");
    private final Reasons reason;

    public ParameterizedTestMethodException(String methodName, Reasons reason) {
        super(getReasonMessage(reason, methodName, null, null));
        this.reason = reason;
    }

    public ParameterizedTestMethodException(String methodName, Type[] paramTypes, Type[] methodArgTypes, Reasons reason) {
        super(getReasonMessage(reason, methodName, paramTypes, methodArgTypes));
        this.reason = reason;
    }

    private static String getReasonMessage(Reasons reason, String methodName, Type[] paramTypes, Type[] methodArgTypes) {
        return switch (reason) {
            case DO_NOT_RETURN_VOID -> TEST_METHOD_SHOULD_RETURN_VOID.formatted(methodName);
            case HAS_EMPTY_PARAM_SOURCE -> PARAMETERIZED_TEST_SOURCE_EMPTY.formatted(methodName);
            case DO_NOT_HAVE_MORE_THAN_ONE_ARG -> PARAMETERIZED_TEST_MORE_THAN_ONE_ARGS.formatted(methodName);
            case HAS_BAD_FIRST_ARG_TYPE -> PARAMETERIZED_TEST_FIRST_ARG.formatted(methodName, testCase);
            case HAS_BAD_ARGS_NUMBER ->
                    PARAMETERIZED_TEST_BAD_ARGS_NUMBER.formatted(methodName, number.apply(paramTypes.length + 1), number.apply(methodArgTypes.length + 1));
            case HAS_BAD_ARGS_TYPES ->
                    PARAMETERIZED_TEST_BAD_ARG_TYPES.formatted(methodName, bold(Arrays.toString(paramTypes)), bold(Arrays.toString(methodArgTypes)));
        };
    }

    public Reasons getReason() {
        return reason;
    }

    public enum Reasons {
        DO_NOT_RETURN_VOID,
        HAS_EMPTY_PARAM_SOURCE,
        DO_NOT_HAVE_MORE_THAN_ONE_ARG,
        HAS_BAD_FIRST_ARG_TYPE,
        HAS_BAD_ARGS_NUMBER,
        HAS_BAD_ARGS_TYPES,
    }
}
