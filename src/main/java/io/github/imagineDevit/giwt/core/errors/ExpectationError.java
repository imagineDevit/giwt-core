package io.github.imagineDevit.giwt.core.errors;

import io.github.imagineDevit.giwt.core.utils.Utils;

import static io.github.imagineDevit.giwt.core.utils.TextUtils.*;

/**
 * An error that occurs when an expectation is not met
 * @author Henri Joel SEDJAME
 * @since 0.1.0
 */
public final class ExpectationError extends GiwtError {


    public ExpectationError(String message, String expected, String actual) {
        super(message);
        initCause(new AssertionError(message(message, expected, actual)));
    }


    public static String message(String message, String expected, String actual) {
        return """
                %s
               
                      %s : %s
                      %s :   %s
                """.formatted(red(italic(Utils.formatReason(message))), bold(italic("Expected")), yellow(expected), bold(italic("Actual")), yellow(actual));
    }
}
