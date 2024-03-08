package io.github.imagineDevit.giwt.core.statements.functions;


import io.github.imagineDevit.giwt.core.ATestCaseState;

import java.util.function.UnaryOperator;


public interface GivenFFn<T, TCS extends ATestCaseState<T>> extends UnaryOperator<TCS> {
}
