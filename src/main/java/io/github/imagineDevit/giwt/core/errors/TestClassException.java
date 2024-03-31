package io.github.imagineDevit.giwt.core.errors;

import static io.github.imagineDevit.giwt.core.utils.Messages.NO_SUPPORT_FOR_ABSTRACT_CLASSES;
import static io.github.imagineDevit.giwt.core.utils.Messages.NO_SUPPORT_FOR_PRIVATE_CLASSES;

public final class TestClassException extends GiwtError {

    private final Reasons reason;

    public TestClassException(String className, Reasons reason) {
        super(getReasonMessage(reason, className));
        this.reason = reason;
    }

    private static String getReasonMessage(Reasons reason, String className) {
        return switch (reason) {
            case IS_ABSTRACT -> NO_SUPPORT_FOR_ABSTRACT_CLASSES.formatted(className);
            case IS_PRIVATE -> NO_SUPPORT_FOR_PRIVATE_CLASSES.formatted(className);
        };
    }

    public Reasons getReason() {
        return reason;
    }

    public enum Reasons {
        IS_ABSTRACT,
        IS_PRIVATE
    }
}
