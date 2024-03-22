package io.github.imagineDevit.giwt.core.descriptors;

import io.github.imagineDevit.giwt.core.utils.GiwtPredicates;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.PackageSource;

/**
 * A descriptor for a <strong>giwt</strong> package class
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class GiwtPackageTestDescriptor extends AbstractTestDescriptor {

    private final String packageName;

    public GiwtPackageTestDescriptor(UniqueId uniqueId, String packageName) {
        super(
                uniqueId.append("package", packageName),
                packageName,
                PackageSource.from(packageName)
        );
        this.packageName = packageName;

        addAllChildren();
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }


    private void addAllChildren() {
        ReflectionUtils.findAllClassesInPackage(packageName, GiwtPredicates.isTestClass(), (name) -> true)
                .forEach(clazz -> addChild(new GiwtClassTestDescriptor(ReflectionUtils.newInstance(clazz), getUniqueId())));
    }
}
