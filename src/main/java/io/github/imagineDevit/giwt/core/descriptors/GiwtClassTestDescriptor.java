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

    private final Object testInstance;

    private final GiwtCallbacks callbacks;

    private final TestConfiguration configuration;

    public GiwtClassTestDescriptor(Object testInstance, UniqueId uniqueId) {
        super(
                uniqueId.append("class", testInstance.getClass().getSimpleName()),
                testInstance.getClass().getSimpleName(),
                ClassSource.from(testInstance.getClass())
        );

        this.testClass = testInstance.getClass();

        this.testInstance = testInstance;

        this.callbacks = GiwtTestEngine.CONTEXT.getCallbacks(this.testInstance);

        this.configuration = GiwtTestEngine.CONTEXT.getConfiguration(this.testInstance);

        addAllChildren();
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    public TestCaseReport.ClassReport createReport() {
        return new TestCaseReport.ClassReport(this.testClass.getName());
    }


    public void execute(Runnable before, Consumer<GiwtClassTestDescriptor> consumer, Runnable after) {
        before.run();
        this.callbacks.beforeAllCallback().beforeAll();
        consumer.accept(this);
        this.callbacks.afterAllCallback().afterAll();
        GiwtTestEngine.CONTEXT.remove(this.testInstance);
        after.run();
    }

    public boolean shouldBeReported() {
        return this.configuration != null && !this.configuration.excludeFromReport().contains(this.testClass);
    }

    private void addAllChildren() {
        GiwtTestEngine.CONTEXT.get(this.testInstance)
                .testMethods()
                .forEach(testMethod -> {
                            var method = testMethod.method();
                            if (testMethod.isParameterized()) {
                                addChild(
                                        new GiwtParameterizedMethodTestDescriptor(method, testInstance, getUniqueId(),
                                                GiwtTestEngine.CONTEXT.getParameters(testInstance, method))
                                );
                            } else {
                                addChild(new GiwtMethodTestDescriptor(
                                        Utils.getTestName(method),
                                        method,
                                        testInstance,
                                        getUniqueId(),
                                        null));
                            }
                        }
                );
    }
}
