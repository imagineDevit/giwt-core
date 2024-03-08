package io.github.imagineDevit.giwt.core;


import io.github.imagineDevit.giwt.core.report.TestCaseReport;
import io.github.imagineDevit.giwt.core.statements.StmtMsg;
import io.github.imagineDevit.giwt.core.statements.functions.*;
import io.github.imagineDevit.giwt.core.utils.TextUtils;
import io.github.imagineDevit.giwt.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Test case abstract class.
 *
 * @param <T>
 * @param <R>
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@SuppressWarnings("unchecked, unused")
public abstract class ATestCase<T, R, STATE extends ATestCaseState<T>, RESULT extends ATestCaseResult<R>> extends CloseableCase {

    protected final String name;

    // region Fields
    protected final TestCaseReport.TestReport report;
    protected final TestParameters.Parameter parameters;
    protected final List<GivenFFn<T, STATE>> andGivenFns = new ArrayList<>();
    protected final List<Object> whenFns = new ArrayList<>();
    protected final List<ThenFn<R, RESULT>> thenFns = new ArrayList<>();
    protected final List<StmtMsg> givenMsgs = new ArrayList<>();
    protected final List<StmtMsg> whenMsgs = new ArrayList<>();
    protected final List<StmtMsg> thenMsgs = new ArrayList<>();
    protected STATE state;
    protected RESULT result;
    protected GivenSFn<T> givenFn = null;
    protected GivenRFn givenRFn = null;
    protected WhenSFn<R> whenFn = null;
    protected WhenRFn whenRFn = null;
    protected WhenCFn<T> whenCFn = null;

    /**
     * Constructor
     *
     * @param name       the testCase name
     * @param report     the test report
     * @param parameters the test parameters
     */
    protected ATestCase(String name, TestCaseReport.TestReport report, TestParameters.Parameter parameters) {
        this.name = name;
        this.report = report;
        this.parameters = parameters;
    }

    // endregion

    /**
     * Creates a new Given statement with the provided message and (supplier) function.
     *
     * @param message the description of the statement being given
     * @param fn      the function that executes the given statement
     * @return the Given statement object
     */
    public <TC extends ATestCase<T, R, STATE, RESULT>> AGivenStmt<T, R, STATE, RESULT, TC> given(String message, GivenSFn<T> fn) {
        return runIfOpen(() -> {
            this.givenMsgs.add(StmtMsg.given(message));
            this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.given(message));
            this.givenFn = fn;
            return new AGivenStmt<>((TC) this);
        });
    }

    public <TC extends ATestCase<T, R, STATE, RESULT>> AGivenStmt<T, R, STATE, RESULT, TC> given(String message, T t) {
        return runIfOpen(() -> {
            this.givenMsgs.add(StmtMsg.given(message));
            this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.given(message));
            this.state = this.stateOf(t);
            return new AGivenStmt<>((TC) this);
        });
    }

    /**
     * Creates a new Given statement with the provided message and (runnable) function.
     *
     * @param message the description of the statement being given
     * @param fn      the function that executes the given statement
     * @return the Given statement object
     */
    public <TC extends ATestCase<T, R, STATE, RESULT>> AGivenStmt<T, R, STATE, RESULT, TC> given(String message, GivenRFn fn) {
        return runIfOpen(() -> {
            this.givenMsgs.add(StmtMsg.given(message));
            this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.given(message));
            this.givenRFn = fn;
            return new AGivenStmt<>((TC) this);
        });
    }

    /**
     * Adds a When statement to the current test case with the provided message and a supplier function.
     *
     * @param message the description of the new When statement
     * @param fn      the function that executes the When statement
     * @return a new instance of WhenStmt that is associated with this test case
     */
    public <TC extends ATestCase<T, R, STATE, RESULT>> AWhenStmt<T, R, STATE, RESULT, TC> when(String message, WhenSFn<R> fn) {
        return runIfOpen(() -> {
            this.whenMsgs.add(StmtMsg.when(message));
            this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.when(message));
            this.whenFn = fn;
            return new AWhenStmt<>((TC) this);
        });
    }

    /**
     * Adds a When statement to the current test case with the provided message and a runnable function
     *
     * @param message the description of the new When statement
     * @param fn      the function that executes the When statement
     * @return a new instance of WhenStmt that is associated with this test case
     */
    public <TC extends ATestCase<T, R, STATE, RESULT>> AWhenStmt<T, R, STATE, RESULT, TC> when(String message, WhenRFn fn) {
        return runIfOpen(() -> {
            this.whenMsgs.add(StmtMsg.when(message));
            this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.when(message));
            this.whenRFn = fn;
            return new AWhenStmt<>((TC) this);
        });
    }

    /**
     * Adds another Given statement to the current test case with the provided message and function.
     *
     * @param message the description of the new Given statement
     * @param fn      the function that executes the given statement
     */
    protected void andGiven(String message, GivenFFn<T, STATE> fn) {
        this.givenMsgs.add(StmtMsg.and(message));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.and(message));
        this.andGivenFns.add(fn);
    }

    /**
     * Adds a When statement to the current test case with the provided message and a function.
     *
     * @param message the description of the new When statement
     * @param fn      the function that executes the When statement and returns a result of type R
     * @return a new instance of WhenStmt that is associated with this test case
     */
    protected <TC extends ATestCase<T, R, STATE, RESULT>> AWhenStmt<T, R, STATE, RESULT, TC> when(String message, WhenFFn<T, R> fn) {
        this.whenMsgs.add(StmtMsg.when(message));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.when(message));
        this.whenFns.add(fn);
        return new AWhenStmt<>((TC) this);
    }

    /**
     * Adds a When statement to the current test case with the provided message and a runnable function
     *
     * @param message the description of the new When statement
     * @param fn      the function that executes the When statement
     * @return a new instance of WhenStmt that is associated with this test case
     */
    protected <TC extends ATestCase<T, R, STATE, RESULT>> AWhenStmt<T, R, STATE, RESULT, TC> whenc(String message, WhenCFn<T> fn) {
        this.whenMsgs.add(StmtMsg.when(message));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.when(message));
        this.whenCFn = fn;
        return new AWhenStmt<>((TC) this);
    }

    /**
     * Adds a When statement to the current test case with the provided message and a function.
     *
     * @param message the description of the new When statement
     * @param fn      the function that executes the When statement and returns a result of type R
     * @return a new instance of WhenStmt that is associated with this test case
     */
    protected <TC extends ATestCase<T, R, STATE, RESULT>> AWhenStmt<T, R, STATE, RESULT, TC> whenr(String message, WhenRFn fn) {
        this.whenMsgs.add(StmtMsg.when(message));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.when(message));
        this.whenFns.add(fn);
        return new AWhenStmt<>((TC) this);
    }

    /**
     * Adds a Then statement to the current test case with the provided message and a consumer function.
     *
     * @param message the description of the new Then statement
     * @param fn      the function that executes the Then statement
     * @return a new instance of ThenStmt that is associated with this test case
     */
    protected <TC extends ATestCase<T, R, STATE, RESULT>> AThenStmt<T, R, STATE, RESULT, TC> then(String message, ThenFn<R, RESULT> fn) {
        this.thenMsgs.add(StmtMsg.then(message));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.then(message));
        this.thenFns.add(fn);
        return new AThenStmt<>((TC) this);
    }

    /**
     * Adds a then statement to the current test case with the provided message and a consumer function.
     * This method is used in conjunction with the initial ThenStmt method, allowing for multiple Then statements in a single test case.
     *
     * @param message the description of the new Then statement
     * @param fn      the function that executes the additional Then statement
     */
    protected void andThen(String message, ThenFn<R, RESULT> fn) {
        thenMsgs.add(StmtMsg.and(message));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.and(message));
        this.thenFns.add(fn);
    }

    @SuppressWarnings("unchecked")
    protected void run() {

        System.out.print(Utils.reportTestCase(name, givenMsgs, whenMsgs, thenMsgs, parameters));

        if (this.givenFn != null) {
            this.state = this.stateOf(this.givenFn.get());
        } else if (this.givenRFn != null) {
            this.givenRFn.run();
        }

        this.andGivenFns.forEach(f -> this.state = f.apply(this.state));

        try {
            if (this.whenFn != null) {
                this.result = this.resultOf(this.whenFn.get());
            } else if (this.whenRFn != null) {
                this.whenRFn.run();
            } else if (this.whenCFn != null) {
                this.state.consumeValue(this.whenCFn);
            } else {
                this.whenFns.forEach(fn -> {
                    if (fn instanceof WhenFFn<?, ?> gfn) {
                        this.result = this.stateToResult((WhenFFn<T, R>) gfn);
                    } else if (fn instanceof WhenRFn rfn) {
                        rfn.run();
                    }
                });
            }
        } catch (Exception e) {
            this.result = this.resultOfErr(e);
        }

        this.thenFns.forEach(fn -> fn.accept(this.result));
    }

    /**
     * Returns the name of the test case with its relevant parameters formatted.
     * If the test case has parameters, it will format the name using the parameters.
     * If not, it will return the name as is.
     * Note: This method is protected, which means it can only be accessed by classes within the same package or subclasses.
     *
     * @return the formatted name of the test case.
     */
    protected String getName() {
        if (parameters != null) return parameters.formatName(name);
        else return name;
    }

    protected abstract STATE stateOf(T value);

    protected abstract RESULT stateToResult(Function<T, R> mapper);

    protected abstract RESULT resultOf(R value);

    protected abstract RESULT resultOfErr(Exception e);

    /**
     * Test case result that can be either success or failure
     */
    public enum Result {
        SUCCESS("✅", TextUtils.green("Passed")),
        FAILURE("❌", TextUtils.red("Failed"));

        private final String s;
        private final String m;

        Result(String s, String m) {
            this.s = s;
            this.m = m;
        }

        public String message(String reason) {
            var r = (reason != null) ? " (reason: " + TextUtils.yellow(reason) + ")" : "";

            return """
                    %s
                    %s
                    """.formatted(s + m + r, Utils.DASH);
        }
    }
}
