package io.github.imagineDevit.giwt.core.utils;

import io.github.imagineDevit.giwt.core.GiwtTestEngine;
import io.github.imagineDevit.giwt.core.errors.DuplicateTestNameException;
import io.github.imagineDevit.giwt.core.lib.TestCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unused")
class UtilsTest {

    @Test
    void getTestName() throws NoSuchMethodException {
        class TestClass {

            @io.github.imagineDevit.giwt.core.annotations.Test("First test")
            void test1(TestCase<Integer, Integer> testCase) {

            }

            @io.github.imagineDevit.giwt.core.annotations.Test
            void test2(TestCase<Integer, Integer> testCase) {

            }
        }

        assertEquals("First test", Utils.getTestName(TestClass.class.getDeclaredMethod("test1", TestCase.class)));
        assertEquals("test2", Utils.getTestName(TestClass.class.getDeclaredMethod("test2", TestCase.class)));
    }

    @Test
    void asComparableOrThrow() throws Exception {
        record Q(Integer value) implements Comparable<Q> {
            @Override
            public int compareTo(Q o) {
                return value.compareTo(o.value);
            }
        }
        record P() {}
        var q = new Q(1);

        assertEquals(q, Utils.asComparableOrThrow(q, () -> new Exception("Error")));

        assertThrows(Exception.class, () -> Utils.asComparableOrThrow(new P(), () -> new Exception("Error")));
    }

    @Test
    void checkTestNamesDuplication() throws NoSuchMethodException {

        class TestClass {

            @io.github.imagineDevit.giwt.core.annotations.Test("First test")
            void test1(TestCase<Integer, Integer> testCase) {

            }

            @io.github.imagineDevit.giwt.core.annotations.Test
            void test2(TestCase<Integer, Integer> testCase) {

            }
        }

        class TestClass2 {
            @io.github.imagineDevit.giwt.core.annotations.Test
            void test2 () {
            }

            @io.github.imagineDevit.giwt.core.annotations.Test("test2")
            void test2p () {
            }

        }

        TestClass testInstance = new TestClass();
        TestClass2 testInstance2 = new TestClass2();
        GiwtTestEngine.CONTEXT.add(testInstance);
        GiwtTestEngine.CONTEXT.add(testInstance2);

        GiwtTestEngine.CONTEXT.addTestMethod(TestClass.class, Arrays.asList(TestClass.class.getDeclaredMethods()), false);
        GiwtTestEngine.CONTEXT.addTestMethod(TestClass2.class, List.of(TestClass2.class.getDeclaredMethod("test2")), false);
        GiwtTestEngine.CONTEXT.addTestMethod(TestClass2.class, List.of(TestClass2.class.getDeclaredMethod("test2p")), false);

        Utils.checkTestNamesDuplication(testInstance);

        assertThrows(DuplicateTestNameException.class, () -> Utils.checkTestNamesDuplication(testInstance2));
    }
}