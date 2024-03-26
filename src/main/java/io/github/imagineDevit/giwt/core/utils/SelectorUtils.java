package io.github.imagineDevit.giwt.core.utils;

import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.descriptors.GiwtClassTestDescriptor;
import io.github.imagineDevit.giwt.core.descriptors.GiwtMethodTestDescriptor;
import io.github.imagineDevit.giwt.core.descriptors.GiwtParameterizedMethodTestDescriptor;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class contains utility methods that are used to append tests to the root of the engine descriptor.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class SelectorUtils {

    public static void appendTestInRoot(ClasspathRootSelector selector, EngineDescriptor root, List<Predicate<String>> predicates) {
        ReflectionUtils
                .findAllClassesInClasspathRoot(selector.getClasspathRoot(), GiwtPredicates.hasTestMethods(), (name) -> {
                    if (predicates.isEmpty()) return true;
                    return predicates.stream().anyMatch(p -> p.test(name));
                })
                .forEach(testClass -> appendTestInClass(testClass, root));
    }

    public static void appendTestInClass(Class<?> testClass, EngineDescriptor root) {

        GiwtTestEngine.CONTEXT.add(testClass);

        if (GiwtPredicates.isTestClass().test(testClass)) {
            Utils.checkTestNamesDuplication(testClass);
            root.addChild(new GiwtClassTestDescriptor(testClass, root.getUniqueId()));
        } else {
            GiwtTestEngine.CONTEXT.remove(testClass);
        }
    }

    public static void appendTestInMethod(Method method, EngineDescriptor root) {
        Class<?> testClass = method.getDeclaringClass();

        GiwtTestEngine.CONTEXT.add(testClass);

        if (GiwtPredicates.isMethodTest().test(method)) {
            root.addChild(new GiwtMethodTestDescriptor(
                    Utils.getTestName(method),
                    method,
                    root.getUniqueId(),
                    null));

        } else if (GiwtPredicates.isParameterizedMethodTest().test(method)) {
            root.addChild(new GiwtParameterizedMethodTestDescriptor(method, root.getUniqueId(), GiwtTestEngine.CONTEXT.getParameters(testClass, method)));
        }
    }

}
