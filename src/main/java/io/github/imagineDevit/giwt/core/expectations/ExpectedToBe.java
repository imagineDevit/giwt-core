package io.github.imagineDevit.giwt.core.expectations;

import io.github.imagineDevit.giwt.core.errors.ExpectationError;
import io.github.imagineDevit.giwt.core.utils.Utils;

import java.util.Objects;

/**
 * This interface provides a set of static methods to create different types of expectations.
 * Each expectation is a record that implements the ExpectedToBe interface and overrides the verify method.
 * The verify method is used to check if a value meets the expectation.
 * If the value does not meet the expectation, an AssertionError is thrown.
 *
 * @param <T> the type of the value to be checked
 */
@SuppressWarnings({"unused"})
public sealed interface ExpectedToBe<T> extends Expectation.OnValue<T> {


    /**
     * Creates an expectation that a value should be null.
     *
     * @param <T> the type of the value to be checked
     * @return a Null expectation
     */
    static <T> Null<T> null_() {
        return new Null<>();
    }

    /**
     * Creates an expectation that a value should not be null.
     *
     * @param <T> the type of the value to be checked
     * @return a NotNull expectation
     */
    static <T> NotNull<T> notNull() {
        return new NotNull<>();
    }

    /**
     * Creates an expectation that a value should be equal to the expected value.
     *
     * @param <T>      the type of the value to be checked
     * @param expected the expected value
     * @return an EqualTo expectation
     */
    static <T> EqualTo<T> equalTo(T expected) {
        return new EqualTo<>(expected);
    }

    /**
     * Creates an expectation that a value should not be equal to the expected value.
     *
     * @param <T>      the type of the value to be checked
     * @param expected the expected value
     * @return a NotEqualTo expectation
     */
    static <T> NotEqualTo<T> notEqualTo(T expected) {
        return new NotEqualTo<>(expected);
    }

    /**
     * Creates an expectation that a value should be between the min and max values.
     *
     * @param <T> the type of the value to be checked
     * @param min the minimum value
     * @param max the maximum value
     * @return a Between expectation
     */
    static <T> Between<T> between(T min, T max) {
        return new Between<>(min, max);
    }

    /**
     * Creates an expectation that a value should be greater than the min value.
     *
     * @param <T> the type of the value to be checked
     * @param min the minimum value
     * @return a GreaterThan expectation
     */
    static <T> GreaterThan<T> greaterThan(T min) {
        return new GreaterThan<>(min);
    }

    /**
     * Creates an expectation that a value should be lesser than the max value.
     *
     * @param <T> the type of the value to be checked
     * @param max the maximum value
     * @return a LesserThan expectation
     */
    static <T> LessThan<T> lessThan(T max) {
        return new LessThan<>(max);
    }

    static <T> SameAs<T> sameAs(T expected) {
        return new SameAs<>(expected);
    }

    static <S> S requireNotNull(S value, String message) {
        if (value == null) {
            throw new ExpectationError(message, "!= null", "null");
        }
        return value;
    }

    /**
     * This record represents an expectation that a value should be null.
     *
     * @param <T> the type of the value to be checked
     */
    record Null<T>() implements ExpectedToBe<T> {
        @Override
        public Name name() {
            return new Name.Value("Expected to be null");
        }

        @Override
        public void verify(T value) {
            if (value != null) {
                throw new ExpectationError(
                        "Expected <null> but got <%s>".formatted(value.toString()),
                        "null",
                        value.toString()
                );
            }
        }
    }

    /**
     * This record represents an expectation that a value should not be null.
     *
     * @param <T> the type of the value to be checked
     */
    record NotNull<T>() implements ExpectedToBe<T> {

        @Override
        public Name name() {
            return new Name.Value("Expected to be not null");
        }

        @Override
        public void verify(T value) {
            if (value == null) {
                throw new ExpectationError(
                        "Expected not null value but got <null>",
                        "!= null",
                        "null"
                );
            }
        }
    }

    /**
     * This record represents an expectation that a value should be equal to the expected value.
     *
     * @param <T>      the type of the value to be checked
     * @param expected the expected value
     */
    record EqualTo<T>(T expected) implements ExpectedToBe<T> {

        @Override
        public Name name() {
            return new Name.Value("Expected to be equal to <" + expected + ">");
        }

        @Override
        public void verify(T value) {
            if (!Objects.equals(value, expected)) {
                throw new ExpectationError(
                        "Expected value to be <" + expected + "> but got <" + value + ">",
                        expected.toString(),
                        value.toString()
                );
            }
        }
    }

    /**
     * This record represents an expectation that a value should not be equal to the expected value.
     *
     * @param <T>      the type of the value to be checked
     * @param expected the expected value
     */
    record NotEqualTo<T>(T expected) implements ExpectedToBe<T> {

        @Override
        public Name name() {
            return new Name.Value("Expected to be not equal to <" + expected + ">");
        }

        @Override
        public void verify(T value) {
            if (Objects.equals(value, expected)) {
                throw new ExpectationError(
                        "Expected value to be different from <" + expected + "> but got <" + value + ">",
                        "!=" + expected,
                        value.toString()
                );
            }
        }
    }

    /**
     * This record represents an expectation that a value should be between the min and max values.
     *
     * @param <T> the type of the value to be checked
     * @param min the minimum value
     * @param max the maximum value
     */
    record Between<T>(T min, T max) implements ExpectedToBe<T> {

        @Override
        public Name name() {
            return new Name.Value("Expected to be between <" + min + "> and <" + max + ">");
        }

        @Override
        public void verify(T value) {
            var c = Utils.asComparableOrThrow(
                    requireNotNull(value, "Expected value to be between <" + min + "> and <" + max + "> but got <null>"),
                    () -> new IllegalStateException("Value is not comparable")
            );

            if (c.compareTo(min) < 0 || c.compareTo(max) > 0) {
                throw new ExpectationError(
                        "Expected value to be between <" + min + "> and <" + max + "> but got <" + value + ">",
                        min + " < " + value + " < " + max,
                        value.toString()
                );
            }
        }
    }

    /**
     * This record represents an expectation that a value should be greater than the min value.
     *
     * @param <T> the type of the value to be checked
     * @param min the minimum value
     */
    record GreaterThan<T>(T min) implements ExpectedToBe<T> {

        @Override
        public Name name() {
            return new Name.Value("Expected to be greater than <" + min + ">");
        }

        @Override
        public void verify(T value) {
            var c = Utils.asComparableOrThrow(
                    requireNotNull(value, "Expected to be greater than <" + min + "> but got <null>"),
                    () -> new IllegalStateException("Value is not comparable")
            );

            if (c.compareTo(min) <= 0) {
                throw new ExpectationError(
                        "Expected value to be greater than <" + min + "> but got <" + value + ">",
                        "> " + min,
                        value.toString()
                );
            }
        }
    }

    /**
     * This record represents an expectation that a value should be lesser than the max value.
     *
     * @param <T> the type of the value to be checked
     * @param max the maximum value
     */
    record LessThan<T>(T max) implements ExpectedToBe<T> {
        @Override
        public Name name() {
            return new Name.Value("Expected to be less than <" + max + ">");
        }

        @Override
        public void verify(T value) {
            var c = Utils.asComparableOrThrow(
                    requireNotNull(value, "Expected to be less than <" + max + "> but got <null>"),
                    () -> new IllegalStateException("Value is not comparable")
            );

            if (c.compareTo(max) >= 0) {
                throw new ExpectationError(
                        "Expected value to be less than <" + max + "> but got <" + value + ">",
                        "< " + max,
                        value.toString()
                );
            }
        }
    }

    record SameAs<T>(T expected) implements ExpectedToBe<T> {
        @Override
        public Name name() {
            return new Name.Value("Expected to be the same as <" + expected + ">");
        }

        @Override
        public void verify(T value) {
            if (value != expected) {
                throw new ExpectationError(
                        "Expected value to be the same as <" + expected + "> but got <" + value + ">",
                        expected.toString(),
                        value.toString()
                );
            }
        }
    }
}
