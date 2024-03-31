package io.github.imagineDevit.giwt.core.lib;

import io.github.imagineDevit.giwt.core.ATestCase;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.report.TestCaseReport;

public class TestCase<T, R> extends ATestCase<T, R, TestCaseState<T>, TestCaseResult<R>> {

    protected TestCase(String name, TestCaseReport.TestReport report, TestParameters.Parameter parameters) {
        super(name, report, parameters);
    }

    @Override
    protected void run() {

    }
}
