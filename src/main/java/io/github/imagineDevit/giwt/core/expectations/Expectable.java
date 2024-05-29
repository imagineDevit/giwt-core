package io.github.imagineDevit.giwt.core.expectations;

import io.github.imagineDevit.giwt.core.ATestCaseResult;

/**
 * This interface provides methods for asserting the state of a result value.
 * It provides several methods for different types of assertions: shouldFail, shouldBe, shouldHave, shouldMatch.
 * Each method takes one or more expectations as arguments and verifies them against the result value.
 *
 * @param <T> the type of the result value
 * @author Henri Joel SEDJAME
 * @version 0.1.2
 * @see ATestCaseResult.ResultValue
 */
@SuppressWarnings({"unused"})
public interface Expectable<T> {

    /**
     * Verifies that the result value should fail the provided expectation.
     *
     * @param expectation the expectation to be checked
     * @return an ExpectationChain.OnFailure instance
     */
    ExpectationChain.OnFailure<ExpectedToFail> shouldFail(ExpectedToFail expectation);

    /**
     * Verifies that the result value should be the provided expectation.
     *
     * @param expectation the expectation to be checked
     * @return an ExpectationChain.OnValue instance
     */
    ExpectationChain.OnValue<T, ExpectedToBe<T>> shouldBe(ExpectedToBe<T> expectation);

    /**
     * Verifies that the result value should have the provided expectation.
     *
     * @param expectation the expectation to be checked
     * @return an ExpectationChain.OnValue instance
     */
    ExpectationChain.OnValue<T, ExpectedToHave<T>> shouldHave(ExpectedToHave<T> expectation);

    /**
     * Verifies that the result value should match the provided expectation.
     *
     * @param expectation the expectation to be checked
     * @return an ExpectationChain.OnValue instance
     */
    ExpectationChain.OnValue<T, ExpectedToMatch<T>> shouldMatch(ExpectedToMatch<T> expectation);

    /**
     * Returns the result value if it is present.
     *
     * @return the result value
     */
    T resultValue();

    /**
     * Returns the result error if it is present.
     *
     * @return the result error
     */
    Throwable resultError();
}