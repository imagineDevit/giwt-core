package io.github.imagineDevit.giwt.core.errors;

/**
 * An error that occurs when trying to get the value or the exception stored in a {@link io.github.imagineDevit.giwt.core.ATestCaseResult.ResultValue}
 * @see io.github.imagineDevit.giwt.core.ATestCaseResult.ResultValue
 * @author Henri Joel SEDJAME
 * @since 0.1.0
 */
public sealed abstract class ResultValueError extends GiwtError {

    public static final String EXPECTED_VALUE_FAILED = "Expected value failed";
    public static final String EXPECTED_ERROR_FAILED = "Expected error failed";

    public ResultValueError(String message) {
        super(message);
    }

    public static final class ExpectedValueFailed extends ResultValueError {
        public ExpectedValueFailed() {
            super(EXPECTED_VALUE_FAILED);
        }
    }


    public static final class ExpectedErrorFailed extends ResultValueError {
        public ExpectedErrorFailed() {
            super(EXPECTED_ERROR_FAILED);
        }
    }
}
