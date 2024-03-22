package io.github.imagineDevit.giwt.core.errors;

import io.github.imagineDevit.giwt.core.utils.TextUtils;

/**
 * Exception thrown when a parameter source is not found
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public final class NoParamSourceFoundException extends GiwtError {

    public NoParamSourceFoundException(String source) {
        super(errorMessage(source));
    }

    private static String errorMessage(String source) {
        return "No parameter source with name %s found".formatted(TextUtils.red(source));
    }
}
