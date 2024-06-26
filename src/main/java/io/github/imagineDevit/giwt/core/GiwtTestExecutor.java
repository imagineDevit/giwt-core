package io.github.imagineDevit.giwt.core;

import io.github.imagineDevit.giwt.core.descriptors.GiwtClassTestDescriptor;
import io.github.imagineDevit.giwt.core.descriptors.GiwtMethodTestDescriptor;
import io.github.imagineDevit.giwt.core.descriptors.GiwtParameterizedMethodTestDescriptor;
import io.github.imagineDevit.giwt.core.report.ReportProcessor;
import io.github.imagineDevit.giwt.core.report.TestCaseReport;
import io.github.imagineDevit.giwt.core.utils.EnvVars;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.util.Arrays;
import java.util.Optional;


/**
 * Giwt test executor
 *
 * @param <TC> the test case type
 * @author Henri Joel SEDJAME
 * @see GiwtTestEngine
 * @since 0.0.1
 */
@SuppressWarnings("rawtypes")
public abstract class GiwtTestExecutor<TC extends ATestCase> {

    private static Integer NB = null;

    private TestCaseReport report;

    private boolean allCallbacksRan = false;

    private Boolean withReport;

    private Object testInstance;

    public abstract void run(TC testCase);

    public abstract TC createTestCase(String name, TestCaseReport.TestReport report, TestParameters.Parameter parameter);

    public void execute(ExecutionRequest request, TestDescriptor root) {

        if (withReport == null) {
            withReport = Boolean.valueOf(System.getenv(EnvVars.GENERATE_REPORT));
        }

        if (NB == null) {
            NB = root.getChildren().size();
        }

        if (root instanceof EngineDescriptor) {
            initReport();
            executeForEngineDescriptor(request, root);
        }

        if (root instanceof GiwtClassTestDescriptor ctd) {
            testInstance = ctd.getTestInstance();
            ctd.execute(
                    () -> allCallbacksRan = true,
                    d -> executeForClassDescriptor(request, d),
                    () -> allCallbacksRan = false
            );
        }

        if (root instanceof GiwtParameterizedMethodTestDescriptor) {
            executeContainer(request, root);
        }

        if (root instanceof GiwtMethodTestDescriptor mtd) {
            if (testInstance == null) testInstance = mtd.getTestInstance();
            mtd.execute(d -> executeForMethodDescriptor(request, d), allCallbacksRan);
        }

    }

    private void initReport() {
        if (report == null && withReport) {
            report = new TestCaseReport();
        }
    }

    private void executeForMethodDescriptor(ExecutionRequest request, GiwtMethodTestDescriptor md) {
        String className = md.getTestMethod().getDeclaringClass().getName();

        Optional<TestCaseReport.ClassReport> classReport = getReport().map(
                tc -> tc.getClassReport(className).orElseGet(() -> {
                    TestCaseReport.ClassReport cr = new TestCaseReport.ClassReport(className);
                    tc.addClassReport(cr);
                    return cr;
                })
        );

        TestCaseReport.TestReport testReport = executeTest(request, md);
        classReport.ifPresent(cr -> cr.addTestReport(testReport));
    }

    private void executeForClassDescriptor(ExecutionRequest request, GiwtClassTestDescriptor r) {
        TestCaseReport.ClassReport classReport = r.createReport();
        if (r.shouldBeReported()) {
            getReport().ifPresent(tc -> tc.addClassReport(classReport));
        }
        executeContainer(request, r);
    }

    private void executeForEngineDescriptor(ExecutionRequest request, TestDescriptor root) {
        executeContainer(request, root);
        getReport()
                .ifPresent(tc -> {
                    if (NB != null && tc.getClassReports().size() == NB) {
                        try {
                            new ReportProcessor().process(tc);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            NB = null;
                        }
                    }
                });
    }

    private TestCaseReport.TestReport executeTest(ExecutionRequest request, GiwtMethodTestDescriptor root) {

        TestCaseReport.TestReport report = new TestCaseReport.TestReport();

        TC testCase = root.getTestCase(report,
                (n) -> (r, p) -> this.createTestCase(n, r, p),
                ATestCase::getName);

        EngineExecutionListener listener = request.getEngineExecutionListener();

        listener.executionStarted(root);

        return root.shouldBeSkipped()
                .map(reason -> {
                    listener.executionSkipped(root, reason);
                    return report;
                })
                .orElseGet(() -> {
                    try {


                        if (root.getParams() != null) {
                            root.getParams().executeTest(testInstance, root.getTestMethod(), testCase);
                        } else {
                            ReflectionUtils.invokeMethod(root.getTestMethod(), testInstance, testCase);
                        }

                        this.run(testCase);

                        report.setStatus(TestCaseReport.TestReport.Status.SUCCESS);

                        System.out.println(ATestCase.Result.SUCCESS.message(null));

                        listener.executionFinished(root, TestExecutionResult.successful());

                    } catch (Throwable e) {

                        report.setStatus(TestCaseReport.TestReport.Status.FAILURE);

                        report.addTrace(e.getClass().getName());

                        Arrays.stream(e.getStackTrace()).forEach(element -> report.addTrace("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at %s".formatted(element.toString())));

                        if (e.getCause() != null) {
                            System.out.println(ATestCase.Result.FAILURE.message(e.getCause().getMessage()));
                            report.setFailureReason(e.getCause().getMessage());
                            report.addTrace("Caused by: %s : %s".formatted(e.getCause().getClass().getName(), e.getCause().getMessage()));
                            Arrays.stream(e.getCause().getStackTrace()).forEach(element -> report.addTrace("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at %s".formatted(element.toString())));
                        } else {
                            System.out.println(ATestCase.Result.FAILURE.message(e.getMessage()));
                            report.setFailureReason(e.getMessage());
                        }

                        listener.executionFinished(root, TestExecutionResult.failed(e));
                    }

                    return report;
                });
    }

    private void executeContainer(ExecutionRequest request, TestDescriptor root) {
        EngineExecutionListener listener = request.getEngineExecutionListener();

        listener.executionStarted(root);

        root.getChildren().forEach(child -> execute(request, child));

        listener.executionFinished(root, TestExecutionResult.successful());
    }

    private Optional<TestCaseReport> getReport() {
        return Optional.ofNullable(report);
    }

}
