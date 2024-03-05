package io.github.imagineDevit.giwt.core;


import io.github.imagineDevit.giwt.core.utils.SelectorUtils;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public abstract class GiwtTestEngine implements TestEngine {

    private final Class<? extends TestCase<?, ?>> testCaseClass;

    public static final String ENGINE_ID = "giwt-test-engine";

    protected GiwtTestEngine(Class<? extends TestCase<?, ?>> testCaseClass) {
        this.testCaseClass = testCaseClass;
    }

    @Override
    public String getId() {
        return ENGINE_ID;
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest engineDiscoveryRequest, UniqueId uniqueId) {

        EngineDescriptor root = new EngineDescriptor(uniqueId, "GiwtUnitTestEngine");

        engineDiscoveryRequest.getSelectorsByType(ClasspathRootSelector.class)
                .forEach(selector -> SelectorUtils.appendTestInRoot(selector, root));

        engineDiscoveryRequest.getSelectorsByType(PackageSelector.class)
                .forEach(selector -> SelectorUtils.appendTestInPackage(selector.getPackageName(), root));

        engineDiscoveryRequest.getSelectorsByType(ClassSelector.class)
                .forEach(selector -> SelectorUtils.appendTestInClass(selector.getJavaClass(), root));

        engineDiscoveryRequest.getSelectorsByType(MethodSelector.class)
                .forEach(methodSelector -> SelectorUtils.appendTestInMethod(methodSelector.getJavaMethod(), root));

        return root;
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        TestDescriptor root = executionRequest.getRootTestDescriptor();
        new GiwtTestExecutor(this.testCaseClass).execute(executionRequest, root);
    }

}
