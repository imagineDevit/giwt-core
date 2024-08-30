package io.github.imagineDevit.giwt.core.expectations;


import io.github.imagineDevit.giwt.core.errors.ExpectationError;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This interface defines the expectations for a value to match a certain condition.
 * It is a sealed interface, meaning it can only be implemented by classes in the same module.
 * It provides several static methods to create specific matchings: matching, one, all, none.
 *
 * @param <T> the type of the value to be checked
 */
@SuppressWarnings({"unused"})
public sealed interface ExpectedToMatch<T> extends Expectation.OnValue<T> {

    /**
     * Creates a matching expectation for a value.
     *
     * @param description the description of the matching
     * @param predicate   the condition to be checked
     * @param <T>         the type of the value to be checked
     * @return a Matching expectation
     */
    static <T> Matching<T> matching(String description, Predicate<T> predicate) {
        return new Matching<>(description, predicate);
    }

    /**
     * Creates a one expectation for a value.
     *
     * @param description the matching description
     * @param predicate   the condition to be checked
     * @param <T>         the type of the value to be checked
     * @return a One expectation
     */
    static <T> One<T> one(String description, Predicate<T> predicate) {
        return new One<>(matching(description, predicate));
    }

    /**
     * Creates an all expectation for a value.
     *
     * @param matchings the matchings to be checked
     * @param <T>       the type of the value to be checked
     * @return an All expectation
     */
    static <T> All<T> all(Map<String, Predicate<T>> matchings) {
        return new All<>(matchings.entrySet().stream()
                .map(e -> matching(e.getKey(), e.getValue()))
                .toList());
    }

    /**
     * Creates a none expectation for a value.
     *
     * @param matchings the matchings to be checked
     * @param <T>       the type of the value to be checked
     * @return a None expectation
     */
    static <T> None<T> none(Map<String, Predicate<T>> matchings) {
        return new None<>(matchings.entrySet().stream()
                .map(e -> matching(e.getKey(), e.getValue()))
                .toList());
    }

    /**
     * This record defines a matching expectation for a value.
     *
     * @param <T> the type of the value to be checked
     */
    record Matching<T>(String description, Predicate<T> predicate) {
        public void shouldTest(T value) {
            if (!predicate.test(value)) {
                throw new ExpectationError(
                        "Matching <%s> failed".formatted(description),
                        description,
                        "not (" + description + ")"
                );
            }
        }

        private Matching<T> not() {
            return new Matching<>(description, predicate.negate());
        }
    }

    /**
     * This record defines a one expectation for a value.
     *
     * @param <T> the type of the value to be checked
     */
    record One<T>(Matching<T> matching) implements ExpectedToMatch<T> {

        @Override
        public Name name() {
            if (Objects.requireNonNullElse(matching.description, "").isBlank())
                return new Name.Value("Expected to match one condition");
            return new Name.Value(matching.description);
        }

        @Override
        public void verify(T value) {
            matching.shouldTest(value);
        }
    }

    /**
     * This record defines an all expectation for a value.
     *
     * @param <T> the type of the value to be checked
     */
    record All<T>(List<Matching<T>> matchings) implements ExpectedToMatch<T> {

        @Override
        public Name name() {
            return new Name.None();
        }

        @Override
        public void verify(T value) {
            matchings.forEach(matching -> new One<T>(matching).doVerify(value));
        }
    }

    /**
     * This record defines a none expectation for a value.
     *
     * @param <T> the type of the value to be checked
     */
    record None<T>(List<Matching<T>> matchings) implements ExpectedToMatch<T> {
        @Override
        public Name name() {
            return new Name.None();
        }

        @Override
        public void verify(T value) {
            matchings.forEach(matching -> new One<T>(matching.not()).doVerify(value));
        }
    }
}