package io.github.imagineDevit.giwt.core.descriptors;

import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.annotations.Test;
import io.github.imagineDevit.giwt.core.callbacks.GiwtCallbacks;
import io.github.imagineDevit.giwt.core.utils.GiwtPredicates;
import io.github.imagineDevit.giwt.core.utils.Utils;
import org.junit.platform.commons.util.ReflectionUtils;
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

    public Class<?> getTestClass() {
        return testClass;
    }

    public void execute(Consumer<GiwtClassTestDescriptor> consumer) {
        this.callbacks.beforeAllCallback().beforeAll();
        consumer.accept(this);
        this.callbacks.afterAllCallback().afterAll();
    }

    public boolean shouldBeReported() {
        return this.configuration != null && !this.configuration.excludeFromReport().contains(this.testClass);
    }

    private void addAllChildren() {
        ReflectionUtils.findMethods(testClass, GiwtPredicates.isMethodTest())
                .forEach(method ->
                        addChild(new GiwtMethodTestDescriptor(
                                Utils.getTestName(method.getAnnotation(Test.class).value(), method),
                                method,
                                testInstance,
                                getUniqueId(),
                                null))
                );

        ReflectionUtils.findMethods(testClass, GiwtPredicates.isParameterizedMethodTest())
                .forEach(method ->
                        addChild(
                                new GiwtParameterizedMethodTestDescriptor(method, testInstance, getUniqueId(),
                                        GiwtTestEngine.CONTEXT.getParameters(testInstance, method))
                        )
                );
    }
}
