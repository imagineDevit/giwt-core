package io.github.imagineDevit.giwt.core.errors;

import io.github.imagineDevit.giwt.core.utils.TextUtils;

/**
 * Exception thrown when multiple parameter sources are found
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public final class MultipleParamSourcesFoundException extends GiwtError {

    public MultipleParamSourcesFoundException(String source) {
        super(errorMessage(source));
    }

    private static String errorMessage(String source) {
        return "Multiple parameter sources found with same name found (%s)".formatted(TextUtils.red(source));
    }
}
