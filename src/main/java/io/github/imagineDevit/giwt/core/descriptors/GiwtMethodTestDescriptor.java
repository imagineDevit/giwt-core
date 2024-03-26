package io.github.imagineDevit.giwt.core.descriptors;

import io.github.imagineDevit.giwt.core.ATestCase;
import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.Skipped;
import io.github.imagineDevit.giwt.core.callbacks.GiwtCallbacks;
import io.github.imagineDevit.giwt.core.report.TestCaseReport;
import io.github.imagineDevit.giwt.core.report.TestCaseReport.TestReport;
import io.github.imagineDevit.giwt.core.utils.Utils;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A descriptor for a <strong>giwt</strong> test method
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class GiwtMethodTestDescriptor extends AbstractTestDescriptor {

    private final Method testMethod;

    private final TestParameters.Parameter params;

    private final GiwtCallbacks callbacks;

    public GiwtMethodTestDescriptor(String name, Method testMethod, UniqueId uniqueId, TestParameters.Parameter params) {

        super(
                uniqueId.append("method", name),
                name,
                MethodSource.from(testMethod)
        );

        this.testMethod = testMethod;
        this.params = params;

        this.callbacks = GiwtTestEngine.CONTEXT.getCallbacks(this.testMethod.getDeclaringClass());

    }

    @Override
    public Type getType() {
        return Type.TEST;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public TestParameters.Parameter getParams() {
        return params;
    }

    public <TC extends ATestCase<?, ?, ?, ?>> TC getTestCase(TestCaseReport.TestReport report, Function<String, BiFunction<TestReport, TestParameters.Parameter, TC>> createTestCase, Function<TC, String> getName) {
        String name;
        if (params == null) {
            name = Utils.getTestName(this.testMethod);
        } else {
            name = Utils.getTestName(this.testMethod);
        }

        report.setStatus(TestReport.Status.SKIPPED);

        TC tc = createTestCase.apply(name).apply(report, params);

        report.setName(getName.apply(tc));

        return tc;
    }

    public Optional<String> shouldBeSkipped() {
        return AnnotationSupport.findAnnotation(this.testMethod, Skipped.class)
                .or(() -> AnnotationSupport.findAnnotation(this.testMethod.getDeclaringClass(), Skipped.class))
                .map(Skipped::reason);
    }

    public Object getTestInstance() {
        return GiwtTestEngine.CONTEXT.getInstanceOf(this.testMethod.getDeclaringClass());
    }

    public void execute(Consumer<GiwtMethodTestDescriptor> consumer, boolean allCallacksRan) {
        if (!allCallacksRan) this.callbacks.beforeAllCallback().beforeAll();
        this.callbacks.beforeEachCallback().beforeEach();
        consumer.accept(this);
        this.callbacks.afterEachCallback().afterEach();
        if (!allCallacksRan) this.callbacks.afterAllCallback().afterAll();
    }
}
