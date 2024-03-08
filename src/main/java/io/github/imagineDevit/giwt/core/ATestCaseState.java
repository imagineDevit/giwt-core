package io.github.imagineDevit.giwt.core;

import java.util.function.Consumer;

public abstract class ATestCaseState<T> {

    protected final T value;

    protected ATestCaseState(T value) {
        this.value = value;
    }


    protected void consumeValue(Consumer<T> consumer) {
        consumer.accept(value);
    }

}
