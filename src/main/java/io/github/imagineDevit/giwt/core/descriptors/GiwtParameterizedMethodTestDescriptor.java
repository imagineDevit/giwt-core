package io.github.imagineDevit.giwt.core.descriptors;

import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.ParameterizedTest;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A descriptor for a <strong>giwt</strong> parameterized test method
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class GiwtParameterizedMethodTestDescriptor extends AbstractTestDescriptor {

    public GiwtParameterizedMethodTestDescriptor(Method testMethod, UniqueId uniqueId, List<? extends TestParameters.Parameter> parameters) {

        super(
                uniqueId.append("method", testMethod.getName()),
                testMethod.getName(),
                MethodSource.from(testMethod)
        );

        addAllChildren(testMethod, parameters);
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    private void addAllChildren(Method testMethod, List<? extends TestParameters.Parameter> parameters) {
        parameters.forEach(param -> {
            String name = param.formatName(testMethod.getAnnotation(ParameterizedTest.class).name());
            addChild(new GiwtMethodTestDescriptor(name, testMethod, getUniqueId(), param));
        });
    }
}
