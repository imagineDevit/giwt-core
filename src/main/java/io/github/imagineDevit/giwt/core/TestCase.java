package io.github.imagineDevit.giwt.core;


import io.github.imagineDevit.giwt.core.report.TestCaseReport;
import io.github.imagineDevit.giwt.core.utils.TextUtils;
import io.github.imagineDevit.giwt.core.utils.Utils;
import org.junit.platform.commons.util.ReflectionUtils;

public abstract class TestCase<T,R> extends CloseableCase {

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

    // region Fields

    /**
     * Test case name
     */
    protected final String name;

    /**
     * Test case report
     */
    private final TestCaseReport.TestReport report;

    private final TestParameters.Parameter parameters;

    /**
     * Constructor
     *
     * @param name  the testCase name
     * @param report the test report
     * @param parameters the test parameters
     */
    private TestCase(String name, TestCaseReport.TestReport report, TestParameters.Parameter parameters) {
        this.name = name;
        this.report = report;
        this.parameters = parameters;
    }

    /**
     * Create a test case
     *
     * @param name test case name
     * @param report test case report
     * @param parameters test case parameters
     * @return the test case
     */
    static  <T, R, C extends TestCase<T, R>> C create(String name, TestCaseReport.TestReport report, TestParameters.Parameter parameters, Class<C> clazz) {
        return ReflectionUtils.newInstance(clazz, name, report, parameters);
    }

    abstract void run();

    /**
     * Returns the name of the test case with its relevant parameters formatted.
     * If the test case has parameters, it will format the name using the parameters.
     * If not, it will return the name as is.
     * Note: This method is protected, which means it can only be accessed by classes within the same package or subclasses.
     *
     * @return the formatted name of the test case.
     */
    protected String getName(){
        if (parameters != null) return parameters.formatName(name);
        else return name;
    }

}
