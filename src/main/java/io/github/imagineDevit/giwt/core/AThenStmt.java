package io.github.imagineDevit.giwt.core;

import io.github.imagineDevit.giwt.core.statements.functions.ThenFn;

public class AThenStmt<T, R, STATE extends ATestCaseState<T>, RESULT extends ATestCaseResult<R>, TC extends ATestCase<T, R, STATE, RESULT>> {
    protected final TC testCase;

    public AThenStmt(TC testCase) {
        this.testCase = testCase;
    }

    public AThenStmt<T, R, STATE, RESULT, TC> and(String message, ThenFn<R, RESULT> fn) {
        testCase.andThen(message, fn);
        return this;
    }
}
