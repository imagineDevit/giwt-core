package io.github.imagineDevit.giwt.core;

import io.github.imagineDevit.giwt.core.statements.functions.ThenFn;

@SuppressWarnings("unused")
public class AWhenStmt<T, R, STATE extends ATestCaseState<T>, RESULT extends ATestCaseResult<R>, TC extends ATestCase<T, R, STATE, RESULT>> {
    protected final TC testCase;

    public AWhenStmt(TC testCase) {
        this.testCase = testCase;
    }

    public AThenStmt<T, R, STATE, RESULT, TC> then(String message, ThenFn<R, RESULT> fn) {
        return testCase.then(message, fn);
    }

}
