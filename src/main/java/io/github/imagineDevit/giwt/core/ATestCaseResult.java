package io.github.imagineDevit.giwt.core;


import java.util.Optional;

@SuppressWarnings("unused")
public abstract class ATestCaseResult<T> {

    protected final ResultValue value;

    protected ATestCaseResult(T value) {
        this.value = new ResultValue.Ok<>(value);
    }


    protected ATestCaseResult(Exception e) {
        this.value = new ResultValue.Err<>(e);
    }

    public static sealed class ResultValue {

        @SuppressWarnings("unchecked")
        public <T> Optional<Ok<T>> ok() {
            return this instanceof Ok ? Optional.of((Ok<T>) this) : Optional.empty();
        }

        @SuppressWarnings("unchecked")
        public <E extends Exception> Optional<Err<E>> err() {
            return this instanceof Err ? Optional.of((Err<E>) this) : Optional.empty();
        }

        @SuppressWarnings("unchecked")
        public <T> T get() {
            if (this instanceof Ok) {
                return ((Ok<T>) this).getValue();
            }
            throw new RuntimeException("TestCaseResult is not Ok");
        }

        public static final class Ok<T> extends ResultValue {
            private final T value;

            public Ok(T value) {
                this.value = value;
            }

            public T getValue() {
                return value;
            }
        }


        public static final class Err<E extends Exception> extends ResultValue {
            private final E error;

            public Err(E error) {
                this.error = error;
            }

            public E getError() {
                return error;
            }
        }
    }

}
