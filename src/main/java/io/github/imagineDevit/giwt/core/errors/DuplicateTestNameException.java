package io.github.imagineDevit.giwt.core.errors;

import io.github.imagineDevit.giwt.core.utils.TextUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception thrown when a duplicated test names are found
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public final class DuplicateTestNameException extends GiwtError {

    public DuplicateTestNameException(List<String> names) {
        super(errorMessage(names));
    }

    private static String errorMessage(List<String> names) {
        String testNames = names.stream()
                .map(TextUtils::green)
                .map("'%s'"::formatted)
                .collect(Collectors.joining(", "));
        return "Following test names : [ %s ] are duplicated.".formatted(testNames);
    }
}
