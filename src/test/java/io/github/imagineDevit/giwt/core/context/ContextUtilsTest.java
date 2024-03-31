package io.github.imagineDevit.giwt.core.context;

import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.*;
import io.github.imagineDevit.giwt.core.callbacks.BeforeAllCallback;
import io.github.imagineDevit.giwt.core.callbacks.GiwtCallbacks;
import org.junit.jupiter.api.Test;

import static io.github.imagineDevit.giwt.core.TestParameters.Parameter.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"unused"})
class ContextUtilsTest {

    static class TestConf extends TestConfiguration {}

    static class TestExtension implements BeforeAllCallback {
        @Override
        public void beforeAll() {
            System.out.println("Before all callback");
        }
    }

    public static class ParamTestClass {

        @ParameterizedTest(
                name = "test",
                source = "getParams"
        )
        void test() {}

        @ParameterSource
        TestParameters<P1<String>> getParams() {
            return TestParameters.of(P1.of("A"), P1.of("B"));
        }
    }

    @Test
    void getConfiguration() {

        @ConfigureWith(TestConf.class)
        class TestClass1 {}

        class TestClass2 {}

        assertNotNull(ContextUtils.getConfiguration(TestClass1.class));
        assertNull(ContextUtils.getConfiguration(TestClass2.class));

    }

    @Test
    void getCallbacks() {
        @ExtendWith({TestExtension.class})
        class TestClass {

            @AfterEach
            void afterEach() {
                System.out.println("After each callback");
            }
        }

        GiwtCallbacks callbacks = ContextUtils.getCallbacks(new TestClass());
        assertNotNull(callbacks);
        assertNotNull(callbacks.beforeAllCallback());
        assertNotNull(callbacks.afterEachCallback());
        assertNotNull(callbacks.beforeEachCallback());
        assertNotNull(callbacks.afterAllCallback());
    }

    @Test
    void getParameters() throws NoSuchMethodException {
        var parameters = ContextUtils.getParameters(ParamTestClass.class.getDeclaredMethod("test"), null);

        assertEquals(2, parameters.size());
        assertInstanceOf(P1.class, parameters.get(0));

    }

    @Test
    void getParameterSource() throws NoSuchMethodException {
        var mSource = ContextUtils.getParameterSource(ParamTestClass.class.getDeclaredMethod("test"), null);
        assertNotNull(mSource);
    }
}