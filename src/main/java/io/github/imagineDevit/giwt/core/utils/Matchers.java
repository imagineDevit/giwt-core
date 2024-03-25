package io.github.imagineDevit.giwt.core.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class contains a utility method that allows to match a value against a list of cases and return the result of the
 * first matching case.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public class Matchers {

    @SafeVarargs
    public static <T> Optional<T> match(MatchCase<T>... cases) {

        Map<Boolean, List<MatchCase<T>>> partitions = Arrays.stream(cases)
                .collect(Collectors.partitioningBy(c -> c.result.get().isSuccess()));

        return partitions.get(true)
                .stream()
                .filter(MatchCase::matched)
                .findFirst()
                .map(success -> {
                            partitions.get(false)
                                    .stream()
                                    .filter(MatchCase::matched)
                                    .findFirst()
                                    .ifPresent(matchCase -> {
                                        throw matchCase.error().orElse(new IllegalStateException("An error occurred"));
                                    });
                            return ((Result.Success<T>) success.result.get()).getValue();
                        }
                );

    }

    public static sealed class Result<T> {

        public static <T> Result<T> success(T value) {
            return new Success<>(() -> value);
        }

        public static <T> Result<T> success(Supplier<T> value) {
            return new Success<>(value);
        }

        public static <T> Result<T> failure(String message) {
            return new Failure.WithMessage<>(() -> message);
        }

        public static <T> Result<T> failure(FailureArg arg) {
            if (arg instanceof FailureArg.StringArg stringArg) {
                return new Failure.WithMessage<>(stringArg::getMessage);
            } else if (arg instanceof FailureArg.ExceptionArg exceptionArg) {
                return new Failure.WithException<>(exceptionArg::getException);
            }
            throw new IllegalStateException("Unknown failure argument");
        }

        public boolean isSuccess() {
            return this instanceof Success;
        }

        public static sealed class FailureArg {

            public static final class StringArg extends FailureArg {

                private final Supplier<String> message;

                public StringArg(Supplier<String> message) {
                    this.message = message;
                }

                public String getMessage() {
                    return message.get();
                }
            }

            public static final class ExceptionArg extends FailureArg {

                private final Supplier<RuntimeException> exception;

                public ExceptionArg(Supplier<RuntimeException> exception) {
                    this.exception = exception;
                }

                public RuntimeException getException() {
                    return exception.get();
                }
            }
        }

        public static final class Success<T> extends Result<T> {

            private final Supplier<T> value;

            public Success(Supplier<T> value) {
                this.value = value;
            }

            public T getValue() {
                return value.get();
            }
        }

        public static abstract sealed class Failure<T> extends Result<T> {

            abstract RuntimeException getError();

            public static final class WithMessage<T> extends Failure<T> {

                private final Supplier<String> message;

                public WithMessage(Supplier<String> message) {
                    this.message = message;
                }

                public String getMessage() {
                    return message.get();
                }

                @Override
                RuntimeException getError() {
                    return new IllegalStateException(getMessage());
                }
            }

            public static final class WithException<T> extends Failure<T> {

                private final Supplier<RuntimeException> exception;

                public WithException(Supplier<RuntimeException> exception) {
                    this.exception = exception;
                }

                public RuntimeException getException() {
                    return exception.get();
                }

                @Override
                RuntimeException getError() {
                    return getException();
                }
            }
        }
    }

    public static class MatchCase<T> {
        private final Supplier<Boolean> predicate;
        private final Supplier<Result<T>> result;

        public MatchCase(Supplier<Boolean> predicate, Supplier<Result<T>> result) {
            this.predicate = predicate;
            this.result = result;
        }

        public static <T> MatchCase<T> matchCase(Supplier<Boolean> predicate, Supplier<Result<T>> result) {
            return new MatchCase<>(predicate, result);
        }


        public boolean matched() {
            return predicate.get();
        }

        public Optional<RuntimeException> error() {
            return result.get().isSuccess() ? Optional.empty() : Optional.of(((Result.Failure<T>) (result.get())).getError());
        }
    }
}
