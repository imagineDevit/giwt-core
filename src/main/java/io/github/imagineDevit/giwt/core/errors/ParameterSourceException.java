package io.github.imagineDevit.giwt.core.errors;

import static io.github.imagineDevit.giwt.core.utils.Messages.*;

public final class ParameterSourceException extends GiwtError {

    private final Reasons reason;

    public ParameterSourceException(String methodOrSource, Reasons reason) {
        super(getReasonMessage(reason, methodOrSource));
        this.reason = reason;
    }

    private static String getReasonMessage(Reasons reason, String methodOrSource) {
        return switch (reason) {
            case DO_NOT_RETURN_VOID -> PARAM_SOURCE_METHOD_SHOULD_RETURN;
            case IS_NOT_PUBLIC -> PARAM_SOURCE_METHOD_PUBLIC.formatted(methodOrSource);
            case NOT_FOUND -> PARAM_SOURCE_NOT_FOUND.formatted(methodOrSource);
            case MULTIPLE_FOUND -> MULTIPLE_SOURCE_FOUND.formatted(methodOrSource);
        };
    }

    public Reasons getReason() {
        return reason;
    }

    public enum Reasons {
        DO_NOT_RETURN_VOID,
        IS_NOT_PUBLIC,
        NOT_FOUND,
        MULTIPLE_FOUND,
    }
}
