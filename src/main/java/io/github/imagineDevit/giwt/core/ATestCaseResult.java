package io.github.imagineDevit.giwt.core;


import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public abstract class ATestCaseResult<T> {

    protected final ResultValue value;

    protected ATestCaseResult(T value) {
        this.value = new ResultValue.Ok<>(value);
    }

    protected ATestCaseResult(Throwable e) {
        this.value = new ResultValue.Err<>(e);
    }

    public static sealed class ResultValue {

        @SuppressWarnings("unchecked")
        public <T> Optional<Ok<T>> ok() {
            return this instanceof Ok ? Optional.of((Ok<T>) this) : Optional.empty();
        }

        @SuppressWarnings("unchecked")
        public <E extends Throwable> Optional<Err<E>> err() {
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

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Ok<?> ok = (Ok<?>) o;
                return Objects.equals(value, ok.value);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(value);
            }
        }

        public static final class Err<E extends Throwable> extends ResultValue {
            private final E error;

            public Err(E error) {
                this.error = error;
            }

            public E getError() {
                return error;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Err<?> err = (Err<?>) o;
                return Objects.equals(error, err.error);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(error);
            }
        }
    }

}
