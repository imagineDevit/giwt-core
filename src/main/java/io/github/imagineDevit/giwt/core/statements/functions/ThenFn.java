package io.github.imagineDevit.giwt.core.statements.functions;


import io.github.imagineDevit.giwt.core.ATestCaseResult;

import java.util.function.Consumer;

public interface ThenFn<R, TCR extends ATestCaseResult<R>> extends Consumer<TCR> {
}
