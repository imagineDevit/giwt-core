package io.github.imagineDevit.giwt.core.utils;

import java.util.function.Function;

import static io.github.imagineDevit.giwt.core.utils.TextUtils.italic;
import static io.github.imagineDevit.giwt.core.utils.TextUtils.purple;

public class Messages {

    public static final Function<String, String> italicPurple = text -> italic(purple(text));

    public static final String NO_SUPPORT_FOR_ABSTRACT_CLASSES = "No support for abstract class %s";
    public static final String NO_SUPPORT_FOR_PRIVATE_CLASSES = "No support for private class %s";
    public static final String NO_SUPPORT_FOR_STATIC_METHODS = "No support for static method %s";
    public static final String NO_SUPPORT_FOR_PRIVATE_METHODS = "No support for private method %s";
    public static final String NO_SUPPORT_FOR_ABSTRACT_METHODS = "No support for abstract method %s";
    public static final String PARAM_SOURCE_METHOD_SHOULD_RETURN = "Method annotated with %s should return object of type %s".formatted(italicPurple.apply("@ParameterSource"), italicPurple.apply("TestParameters<?>"));
    public static final String PARAM_SOURCE_METHOD_PUBLIC = italicPurple.apply("ParameterSource") + " method (%s) should be public";
    public static final String TEST_METHOD_ARG_TYPE = "Test method %s should have one argument of type %s ";
    public static final String TEST_METHOD_BAD_ARG_TYPE = "Test method %s argument type should be %s";
    public static final String TEST_METHOD_SHOULD_RETURN_VOID = "Test method %s should return void";
    public static final String PARAMETERIZED_TEST_SOURCE_EMPTY = "Parameterized test %s source should not be empty";
    public static final String PARAMETERIZED_TEST_MORE_THAN_ONE_ARGS = "Parameterized method test %s should have more than one argument";
    public static final String PARAMETERIZED_TEST_FIRST_ARG = "The first argument of the parameterized test method %s should be of type %s";
    public static final String PARAMETERIZED_TEST_BAD_ARGS_NUMBER = "Parameterized test method %s expected to have <%s>  but got <%s> arguments";
    public static final String PARAM_SOURCE_NOT_FOUND = "No parameter source found with name %s";
    public static final String MULTIPLE_SOURCE_FOUND = "Multiple parameter sources found with same name %s";
    public static final String PARAMETERIZED_TEST_BAD_ARG_TYPES = "Parameterized test method %s expected to have arguments of types : %s  but got %s";
}
