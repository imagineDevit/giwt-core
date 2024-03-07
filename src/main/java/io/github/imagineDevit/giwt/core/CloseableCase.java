package io.github.imagineDevit.giwt.core;

import io.github.imagineDevit.giwt.core.utils.Utils;

import java.util.function.Supplier;

/**
 * Closeable case
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public abstract class CloseableCase {
    private boolean closed = false;

    private void close() {
        closed = true;
    }

    protected <S> S runIfOpen(Supplier<S> fn) {
        return Utils.runIfOpen(this.closed, fn, this::close);
    }
}
