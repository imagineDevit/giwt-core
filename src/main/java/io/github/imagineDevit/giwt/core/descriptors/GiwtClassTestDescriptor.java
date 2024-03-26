package io.github.imagineDevit.giwt.core.descriptors;

import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.callbacks.GiwtCallbacks;
import io.github.imagineDevit.giwt.core.report.TestCaseReport;
import io.github.imagineDevit.giwt.core.utils.Utils;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

import java.util.function.Consumer;

/**
 * A descriptor for a <strong>giwt</strong> test class
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class GiwtClassTestDescriptor extends AbstractTestDescriptor {

    private final Class<?> testClass;

    private final GiwtCallbacks callbacks;

    private final TestConfiguration configuration;

    public GiwtClassTestDescriptor(Class<?> testClass, UniqueId uniqueId) {
        super(
                uniqueId.append("class", testClass.getSimpleName()),
                testClass.getSimpleName(),
                ClassSource.from(testClass)
        );

        this.testClass = testClass;

        this.callbacks = GiwtTestEngine.CONTEXT.getCallbacks(this.testClass);

        this.configuration = GiwtTestEngine.CONTEXT.getConfiguration(this.testClass).orElse(null);

        addAllChildren();
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    public TestCaseReport.ClassReport createReport() {
        return new TestCaseReport.ClassReport(this.testClass.getName());
    }

    public Object getTestInstance() {
        return GiwtTestEngine.CONTEXT.getInstanceOf(this.testClass);
    }

    public void execute(Runnable before, Consumer<GiwtClassTestDescriptor> consumer, Runnable after) {
        before.run();
        this.callbacks.beforeAllCallback().beforeAll();
        consumer.accept(this);
        this.callbacks.afterAllCallback().afterAll();
        GiwtTestEngine.CONTEXT.remove(this.testClass);
        after.run();
    }

    public boolean shouldBeReported() {
        return this.configuration != null && !this.configuration.excludeFromReport().contains(this.testClass);
    }

    private void addAllChildren() {
        GiwtTestEngine.CONTEXT.get(this.testClass)
                .testMethods()
                .forEach(testMethod -> {
                            var method = testMethod.method();
                            if (testMethod.isParameterized()) {
                                addChild(
                                        new GiwtParameterizedMethodTestDescriptor(method, getUniqueId(), GiwtTestEngine.CONTEXT.getParameters(this.testClass, method))
                                );
                            } else {
                                addChild(new GiwtMethodTestDescriptor(Utils.getTestName(method), method, getUniqueId(), null));
                            }
                        }
                );
    }
}
