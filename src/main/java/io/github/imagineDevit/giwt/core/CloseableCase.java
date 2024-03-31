package io.github.imagineDevit.giwt.core;

import java.util.function.Supplier;

/**
 * Closeable case
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class CloseableCase {
    private boolean closed = false;

    private void close() {
        closed = true;
    }

    protected <S> S runIfOpen(Supplier<S> fn) {
        if (closed) {
            throw new IllegalStateException("""
                                        \s
                     Test case is already closed.
                     A test case can only be run once.
                    \s""");
        }
        close();
        return fn.get();
    }
}
