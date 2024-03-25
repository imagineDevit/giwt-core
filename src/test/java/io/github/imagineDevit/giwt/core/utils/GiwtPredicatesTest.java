package io.github.imagineDevit.giwt.core.utils;

import io.github.imagineDevit.giwt.core.TestConfiguration;
import io.github.imagineDevit.giwt.core.TestParameters;
import io.github.imagineDevit.giwt.core.annotations.ParameterSource;
import io.github.imagineDevit.giwt.core.annotations.ParameterizedTest;
import io.github.imagineDevit.giwt.core.errors.ParameterSourceException;
import io.github.imagineDevit.giwt.core.errors.ParameterizedTestMethodException;
import io.github.imagineDevit.giwt.core.errors.TestClassException;
import io.github.imagineDevit.giwt.core.errors.TestMethodException;
import io.github.imagineDevit.giwt.core.lib.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
class GiwtPredicatesTest {

    @Test
    void shouldHaveTestMethods() {

        class TestClass1 {
            @io.github.imagineDevit.giwt.core.annotations.Test
            void test1() {}
        }

        class TestClass2{
            @io.github.imagineDevit.giwt.core.annotations.ParameterizedTest(
                    name = "Test 1",
                    source = "params"
            )
            void test1() {}
        }

        assertTrue(GiwtPredicates.hasTestMethods().test(TestClass1.class));
        assertTrue(GiwtPredicates.hasTestMethods().test(TestClass2.class));
    }

    @Test
    void shouldNotHaveTestMethods() {

        class JustAClass {
            void test1() {}
            void test2(TestCase<String, String> testCase) {}
        }

        assertFalse(GiwtPredicates.hasTestMethods().test(JustAClass.class));
    }
    
    @Test
    void shouldBeTestClass() {
        class TestClass1 {
            @io.github.imagineDevit.giwt.core.annotations.Test
            void test1(TestCase<Object,Object> tc) {}
        }

        class TestClass2{
            @io.github.imagineDevit.giwt.core.annotations.ParameterizedTest(
                    name = "Test 1",
                    source = "params"
            )
            void test1(TestCase<Object,Object> tc, String param) {}

            @ParameterSource
            TestParameters<TestParameters.Parameter.P1<String>> params() {
                return TestParameters.of(
                        TestParameters.Parameter.P1.of("A"),
                        TestParameters.Parameter.P1.of("B")
                );
            }
        }

        assertTrue(GiwtPredicates.isTestClass().test(TestClass1.class));
        assertTrue(GiwtPredicates.isTestClass().test(TestClass2.class));
    }

    @Test
    void shouldNotBeTestClass() {
        class JustAClass {
            void test1() {}
        }


        assertFalse(GiwtPredicates.isTestClass().test(JustAClass.class));

        var ex1 = assertThrows(TestClassException.class,
                () -> GiwtPredicates.isTestClass().test(AbstractTestClass.class));

        assertEquals(TestClassException.Reasons.IS_ABSTRACT, ex1.getReason());

        var ex2 = assertThrows(TestClassException.class,
                () -> GiwtPredicates.isTestClass().test(PrivateTestClass.class));

        assertEquals(TestClassException.Reasons.IS_PRIVATE, ex2.getReason());
    }

    @Test
    void shouldBeMethodTest() throws NoSuchMethodException {

        class TestClass1 {
            @io.github.imagineDevit.giwt.core.annotations.Test
            void test1(TestCase<Object, Object> tc) {}
        }

        assertTrue(GiwtPredicates.isMethodTest().test(TestClass1.class.getDeclaredMethod("test1", TestCase.class)));
    }

    @Test
    void shouldNotBeMethodTest() throws NoSuchMethodException {

        class JustAClass {
            void test1(TestCase<Object, Object> tc) {}
        }

        assertFalse(GiwtPredicates.isMethodTest().test(JustAClass.class.getDeclaredMethod("test1", TestCase.class)));

        class TestClassWithWrongMethod {
            @io.github.imagineDevit.giwt.core.annotations.Test
            void test1() {}

            @io.github.imagineDevit.giwt.core.annotations.Test
            void test2(Object o) {}

            @io.github.imagineDevit.giwt.core.annotations.Test
            Object test3(TestCase<Integer, Integer> testCase) {
                return null;
            }

            @io.github.imagineDevit.giwt.core.annotations.Test
            static void test4(TestCase<Integer, Integer> testCase) {}

            @io.github.imagineDevit.giwt.core.annotations.Test
            private void test5(TestCase<Integer, Integer> testCase) {}

        }

        var ex1 =assertThrows(TestMethodException.class,
                () -> GiwtPredicates.isMethodTest().test(TestClassWithWrongMethod.class.getDeclaredMethod("test1")));

        assertEquals(TestMethodException.Reasons.DO_NOT_HAVE_EXACTLY_ONE_ARG, ex1.getReason());

        var ex2 =assertThrows(TestMethodException.class,
                () -> GiwtPredicates.isMethodTest().test(TestClassWithWrongMethod.class.getDeclaredMethod("test2", Object.class)));

        assertEquals(TestMethodException.Reasons.HAS_BAD_ARG_TYPE, ex2.getReason());

        var ex3 = assertThrows(TestMethodException.class,
                () -> GiwtPredicates.isMethodTest().test(TestClassWithWrongMethod.class.getDeclaredMethod("test3", TestCase.class)));

        assertEquals(TestMethodException.Reasons.DO_NOT_RETURN_VOID, ex3.getReason());

        var ex4 = assertThrows(TestMethodException.class,
                () -> GiwtPredicates.isMethodTest().test(TestClassWithWrongMethod.class.getDeclaredMethod("test4", TestCase.class)));

        assertEquals(TestMethodException.Reasons.IS_STATIC, ex4.getReason());

        var ex5 = assertThrows(TestMethodException.class,
                () -> GiwtPredicates.isMethodTest().test(TestClassWithWrongMethod.class.getDeclaredMethod("test5", TestCase.class)));

        assertEquals(TestMethodException.Reasons.IS_PRIVATE, ex5.getReason());

    }

    @Test
    void shouldBeParameterizedTest() throws NoSuchMethodException {
        class TestClass {
            @ParameterizedTest(
                    name = "Test 1",
                    source = "params"
            )
            void test1(TestCase<Integer, Integer> testCase, String param) {

            }

            @ParameterSource
            TestParameters<TestParameters.Parameter.P1<String>> params() {
                return TestParameters.of(
                        TestParameters.Parameter.P1.of("A"),
                        TestParameters.Parameter.P1.of("B")
                );
            }
        }

        assertTrue(GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test1", TestCase.class, String.class)));
    }

    @Test
    void shouldNotBeParameterizedTest() throws NoSuchMethodException {

        class TestClass {
            void test7(TestCase<Integer, Integer> testCase, String param){}

            @ParameterizedTest(name = "Test 8", source = "params")
            void test8(String param){}

            @ParameterizedTest(name = "Test 9", source = "params")
            void test9(Object o, String param){}

            @ParameterizedTest(name = "Test 10", source = "params")
            Object test10(TestCase<String, String> o, String param){return null;}

            @ParameterizedTest(name = "Test 11", source = "params")
            static void test11(TestCase<Integer, Integer> testCase, String param){}

            @ParameterizedTest(name = "Test 12", source = "params")
            private void test12(TestCase<Integer, Integer> testCase, String param){}

            @ParameterizedTest(name = "Test 13", source = "parameters")
            private void test13(TestCase<Integer, Integer> testCase, String param){}

            @ParameterizedTest(name = "Test 14", source = "p")
            void test14(TestCase<Integer, Integer> testCase, String param){}

            @ParameterizedTest(name = "Test 15", source = "param")
            void test15(TestCase<Integer, Integer> testCase, String param){}

            @ParameterSource
            public TestParameters<TestParameters.Parameter.P1<String>> params() {
                return null;
            }

            @ParameterSource
            public TestParameters<TestParameters.Parameter.P2<String, Boolean>> parameters() {
                return null;
            }

            @ParameterSource
            public TestParameters<TestParameters.Parameter.P1<String>> param() {
                return null;
            }

            @ParameterSource("param")
            public TestParameters<TestParameters.Parameter.P1<String>> param2() {
                return null;
            }
        }

        assertFalse(GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test7", TestCase.class, String.class)));


        var ex1 = assertThrows(ParameterizedTestMethodException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test8", String.class)));

        assertEquals(ParameterizedTestMethodException.Reasons.DO_NOT_HAVE_MORE_THAN_ONE_ARG, ex1.getReason());

        var ex2 = assertThrows(ParameterizedTestMethodException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test9", Object.class, String.class)));

        assertEquals(ParameterizedTestMethodException.Reasons.HAS_BAD_FIRST_ARG_TYPE, ex2.getReason());

        var ex3 = assertThrows(ParameterizedTestMethodException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test10", TestCase.class, String.class)));

        assertEquals(ParameterizedTestMethodException.Reasons.DO_NOT_RETURN_VOID, ex3.getReason());

        var ex4 = assertThrows(TestMethodException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test11", TestCase.class, String.class)));

        assertEquals(TestMethodException.Reasons.IS_STATIC, ex4.getReason());

        var ex5 = assertThrows(TestMethodException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test12", TestCase.class, String.class)));

        assertEquals(TestMethodException.Reasons.IS_PRIVATE, ex5.getReason());

        var ex6 = assertThrows(ParameterizedTestMethodException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test13", TestCase.class, String.class)));

        assertEquals(ParameterizedTestMethodException.Reasons.HAS_BAD_ARGS_NUMBER, ex6.getReason());

        var ex7 = assertThrows(ParameterSourceException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test14", TestCase.class, String.class)));

        assertEquals(ParameterSourceException.Reasons.NOT_FOUND, ex7.getReason());

        var ex8 = assertThrows(ParameterSourceException.class,
                () -> GiwtPredicates.isParameterizedMethodTest().test(TestClass.class.getDeclaredMethod("test15", TestCase.class, String.class)));

        assertEquals(ParameterSourceException.Reasons.MULTIPLE_FOUND, ex8.getReason());
    }

    @Test
    void shouldBeParameterSource() throws NoSuchMethodException {
        class TestClass {
            @ParameterSource
            TestParameters<TestParameters.Parameter.P1<String>> params() {
                return TestParameters.of(
                        TestParameters.Parameter.P1.of("A"),
                        TestParameters.Parameter.P1.of("B")
                );
            }
        }

        class TestConf implements TestConfiguration {
            @ParameterSource
            public TestParameters<TestParameters.Parameter.P1<String>> params() {
                return TestParameters.of(
                        TestParameters.Parameter.P1.of("A"),
                        TestParameters.Parameter.P1.of("B")
                );
            }
        }

        assertTrue(GiwtPredicates.isParameterSource(false).test(TestClass.class.getDeclaredMethod("params")));
        assertTrue(GiwtPredicates.isParameterSource(true).test(TestConf.class.getDeclaredMethod("params")));

    }

    @Test
    void shouldNotBeParameterSource() {

        class TestConf implements TestConfiguration {
            @ParameterSource
            TestParameters<TestParameters.Parameter.P1<String>> params2() {
                return TestParameters.of(
                        TestParameters.Parameter.P1.of("A"),
                        TestParameters.Parameter.P1.of("B")
                );
            }
        }


        var ex = assertThrows(ParameterSourceException.class,
                () -> GiwtPredicates.isParameterSource(true).test(TestConf.class.getDeclaredMethod("params2")));

        assertEquals(ParameterSourceException.Reasons.IS_NOT_PUBLIC, ex.getReason());
    }

    abstract  class AbstractTestClass {
        @io.github.imagineDevit.giwt.core.annotations.Test
        void test1(TestCase<Object,Object> tc) {}
    }

    private class PrivateTestClass {
        @io.github.imagineDevit.giwt.core.annotations.Test
        void test1(TestCase<Object,Object> tc) {}
    }
}