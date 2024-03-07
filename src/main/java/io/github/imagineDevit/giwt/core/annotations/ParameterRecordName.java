package io.github.imagineDevit.giwt.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the name of a record generated for a method .
 *
 * @see GiwtProxyable
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface ParameterRecordName {

    String value();
}
