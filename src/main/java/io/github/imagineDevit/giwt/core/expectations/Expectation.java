package io.github.imagineDevit.giwt.core.expectations;

import io.github.imagineDevit.giwt.core.errors.ResultValueError;

import java.util.List;
import java.util.function.Consumer;

import static io.github.imagineDevit.giwt.core.utils.TextUtils.*;

/**
 * This interface represents an expectation, which is a condition that a value must satisfy.
 * An expectation can either succeed or fail.
 * It provides a method to verify the expectation against a value.
 *
 * @param <T> the type of the value to be checked
 * @author Henri Joel SEDJAME
 * @version 0.1.5
 */
@SuppressWarnings("unused")
public sealed interface Expectation<T> {

    String PLAY = "▸";
    String PASSED = " ✔";
    String FAILED = " ✘";

    /**
     * get the name of the expectation.
     *
     * @return a [Name] instance
     */
    Name name();

    /**
     * Verifies the expectation against a value.
     *
     * @param value the value to be checked
     */
    void verify(T value);

    default void doVerify(T value) {
        Name name = name();
        Consumer<Consumer<String>> doPrint = (Consumer<String> c) -> {
            if (name instanceof Name.Value v) {
                c.accept(v.name());
            }
        };
        try {
            doPrint.accept(s -> System.out.printf("     %s %s", yellow(PLAY), italic(s)));
            verify(value);
            doPrint.accept(s -> System.out.println(green(PASSED)));
        } catch (Throwable e) {
            doPrint.accept(s -> System.out.printf("""
                    %s
                    
                    """, red(FAILED)));
            throw e;
        }
    }

    sealed interface Name {

        record Value(String name) implements Name {
            public Value {
                if (name == null || name.isBlank()) {
                    throw new IllegalArgumentException("Name cannot be null or blank");
                }
            }
        }

        final class None implements Name {
        }


    }

    /**
     * This interface represents a failed expectation.
     * It extends the Expectation interface with the type parameter set to Exception.
     * It can only be implemented by the ExpectedToFail class.
     */
    sealed interface OnFailure extends Expectation<Throwable> permits ExpectedToFail {
    }

    /**
     * This interface represents a successful expectation.
     * It extends the Expectation interface with a generic type parameter.
     * It can be implemented by the ExpectedToBe, ExpectedToHave, and ExpectedToMatch classes.
     *
     * @param <T> the type of the value to be checked
     */
    sealed interface OnValue<T> extends Expectation<T> permits ExpectedToBe, ExpectedToHave, ExpectedToMatch {
    }

    /**
     * This interface represents a should expectation that can be Fail or Succeed.
     *
     * @param <R> the type of the value on which the expectation is checked
     */
    sealed interface Should<R> extends Expectation<Void> {

        /**
         * Verifies the expectation.
         */
        void verify();

        /**
         * Verifies the expectation and returns the value.
         *
         * @return the value
         */
        R verifyAndGet();


        /**
         * This class represents an expectation on a failure.
         *
         * @param <T>  the type of the value on which the expectation is checked
         * @param <EX> the type of the expectable
         */
        final class ShouldFail<T, EX extends Expectable<T>> implements Should<Throwable> {

            private final EX expectable;
            private final List<OnFailure> expectations;
            private Throwable error;

            public ShouldFail(EX expectable, List<OnFailure> expectations) {
                this.expectable = expectable;
                this.expectations = expectations;
            }

            @Override
            public Name name() {
                return new Name.None();
            }

            @Override
            public void verify(Void v) {
                try {
                    error = expectable.resultError();
                    expectations.forEach(e -> e.doVerify(error));
                } catch (ResultValueError.ExpectedErrorFailed e) {
                    System.out.printf("""
                                 %s %s %s
                            
                            """, yellow(PLAY), italic("Expected to fail"), red(FAILED));
                    try {
                        T value = expectable.resultValue();
                        throw new AssertionError("Expected to fail but got a result <%s>".formatted(value));
                    } catch (Exception ex) {
                        throw new AssertionError("Expected to fail but got a result");
                    }
                }
            }

            public void verify() {
                doVerify(null);
            }

            public Throwable verifyAndGet() {
                verify();
                return error;
            }
        }

        /**
         * This class represents an expectation on a success.
         *
         * @param <T>  the type of the value on which the expectation is checked
         * @param <EX> the type of the expectable
         */
        final class ShouldSucceed<T, EX extends Expectable<T>> implements Should<T> {

            private final EX expectable;
            private final List<OnValue<T>> expectations;
            private T value;

            public ShouldSucceed(EX expectable, List<OnValue<T>> expectations) {
                this.expectable = expectable;
                this.expectations = expectations;
            }

            @Override
            public Name name() {
                return new Name.None();
            }

            @Override
            public void verify(Void v) {
                try {
                    value = expectable.resultValue();
                    expectations.forEach(e -> e.doVerify(value));
                } catch (ResultValueError.ExpectedValueFailed e) {
                    System.out.printf("""
                                 %s %s %s
                            
                            """, yellow(PLAY), italic("Expected to succeed"), red(FAILED));
                    try {
                        Throwable error = expectable.resultError();
                        throw new AssertionError("Expected to succeed but got an error <%s('%s')>".formatted(error.getClass().getName(), error.getMessage()));
                    } catch (Exception ex) {
                        throw new AssertionError("Expected to succeed but got an error");
                    }
                }
            }

            @Override
            public void verify() {
                doVerify(null);
            }

            @Override
            public T verifyAndGet() {
                verify();
                return value;
            }
        }
    }


}