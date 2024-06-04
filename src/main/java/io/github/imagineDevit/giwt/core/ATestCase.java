package io.github.imagineDevit.giwt.core;


import io.github.imagineDevit.giwt.core.report.TestCaseReport;
import io.github.imagineDevit.giwt.core.statements.StmtMsg;
import io.github.imagineDevit.giwt.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static io.github.imagineDevit.giwt.core.utils.TextUtils.*;


/**
 * Test case abstract class.
 *
 * @param <T>
 * @param <R>
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@SuppressWarnings({"unused"})
public abstract class ATestCase<T, R, STATE extends ATestCaseState<T>, RESULT extends ATestCaseResult<R>> extends CloseableCase {

    // region Fields
    protected final String name;
    protected final TestCaseReport.TestReport report;
    protected final TestParameters.Parameter parameters;
    protected final List<StmtMsg> givenMsgs = new ArrayList<>();
    protected final List<StmtMsg> whenMsgs = new ArrayList<>();
    protected final List<StmtMsg> thenMsgs = new ArrayList<>();
    protected STATE state;
    protected RESULT result;

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

    protected void addGivenMsg(String message) {
        this.givenMsgs.add(StmtMsg.given(message.trim()));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.given(message));
    }

    protected void addAndGivenMsg(String message) {
        this.givenMsgs.add(StmtMsg.and(message.trim()));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.and(message));
    }

    protected void addWhenMsg(String message) {
        this.whenMsgs.add(StmtMsg.when(message.trim()));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.when(message));
    }

    protected void addThenMsg(String message) {
        this.thenMsgs.add(StmtMsg.then(message.trim()));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.then(message));
    }

    protected void addAndThenMsg(String message) {
        this.thenMsgs.add(StmtMsg.and(message.trim()));
        this.report.addDescriptionItem(TestCaseReport.TestReport.DescriptionItem.and(message));
    }

    protected abstract void run();

    /**
     * Test case result that can be either success or failure
     */
    public enum Result {
        SUCCESS("✅", green(bold("PASSED"))),
        FAILURE("💥", red(bold("FAILED")));

        private final String s;
        private final String m;

        Result(String s, String m) {
            this.s = s;
            this.m = m;
        }

        public String message(String reason) {
            Supplier<String> rlabel = () -> """
                      👉%s : %s
                    """.formatted(bold("reason"), reason);

            return (reason == null) ? """
                    %s
                    %s
                    """.formatted(s + m, Utils.DASH) : """
                    %s
                    %s%s
                    """.formatted(s + m, rlabel.get(), Utils.DASH);
        }
    }

}
