package io.github.imagineDevit.giwt.core.lib;

import io.github.imagineDevit.giwt.core.ATestCaseResult;

@SuppressWarnings("unused")
public class TestCaseResult<R> extends ATestCaseResult<R> {
    protected TestCaseResult(R value) {
        super(value);
    }

    protected TestCaseResult(Exception e) {
        super(e);
    }
}
