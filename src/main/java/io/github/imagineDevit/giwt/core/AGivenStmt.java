package io.github.imagineDevit.giwt.core;

import io.github.imagineDevit.giwt.core.statements.functions.GivenFFn;
import io.github.imagineDevit.giwt.core.statements.functions.WhenCFn;
import io.github.imagineDevit.giwt.core.statements.functions.WhenFFn;
import io.github.imagineDevit.giwt.core.statements.functions.WhenRFn;

public class AGivenStmt<T, R, STATE extends ATestCaseState<T>, RESULT extends ATestCaseResult<R>, TC extends ATestCase<T, R, STATE, RESULT>> {

    protected final TC testCase;

    public AGivenStmt(TC testCase) {
        this.testCase = testCase;
    }

    public AGivenStmt<T, R, STATE, RESULT, TC> and(String message, GivenFFn<T, STATE> fn) {
        testCase.andGiven(message, fn);
        return this;
    }

    public AWhenStmt<T, R, STATE, RESULT, TC> when(String message, WhenFFn<T, R> fn) {
        return testCase.when(message, fn);
    }

    public AWhenStmt<T, R, STATE, RESULT, TC> when(String message, WhenCFn<T> fn) {
        return testCase.whenc(message, fn);
    }

    public AWhenStmt<T, R, STATE, RESULT, TC> when(String message, WhenRFn fn) {
        return testCase.whenr(message, fn);
    }


}
