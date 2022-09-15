package com.bobocode.bibernate.annotation;

import com.bobocode.bibernate.session.Session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies class as an entity. Class that considered to be entity and not marked with this annotation
 * will not be allowed to work with {@link Session}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
}
