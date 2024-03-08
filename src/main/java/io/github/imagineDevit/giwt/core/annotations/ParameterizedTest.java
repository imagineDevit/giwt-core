package io.github.imagineDevit.giwt.core.annotations;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;

/**
 * A custom annotation used to mark a test method as a parameterized test.
 * The test case name and parameter source name can be specified using this annotation.
 *
 * <pre>Example usage:</pre>
 * <pre>{@code
 * @ParameterizedTest(
 *    name = "(1 * 2) + {0} should be equal to {1}",
 *    source = "getParams"
 * )
 * void test2(TestCase<Integer, Integer> testCase, Integer number, Integer expectedResult) {
 *         testCase
 *                 .given("state is 1", () -> 1)
 *                 .and("state is multiplied by 2", state -> state.map(i -> i * 2))
 *                 .when("%d is added to the state".formatted(number), i -> i + number)
 *                 .then("the result should be %d".formatted(expectedResult), result ->
 *                         result
 *                                 .shouldBe()
 *                                 .notNull()
 *                                 .equalTo(expectedResult)
 *                 );
 *     }
 *
 * }</pre>
 *
 * @author Henri Joel SEDJAME
 * @see ParameterSource
 * @since 0.0.1
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Testable
@Documented
public @interface ParameterizedTest {

    /**
     * @return the test case name
     */
    String name();

    /**
     * @return the parameter source name
     */
    String source();
}
