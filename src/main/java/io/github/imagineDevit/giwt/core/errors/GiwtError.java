package io.github.imagineDevit.giwt.core.errors;

public sealed abstract class GiwtError extends RuntimeException permits DuplicateTestNameException, TestCaseArgMissingException {
    public GiwtError(String message) {
        super(message);
    }
}
