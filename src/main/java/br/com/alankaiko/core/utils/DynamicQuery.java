package br.com.alankaiko.core.utils;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DynamicQuery {
    String[] field();

    Class<?> target() default void.class;

    JoinType joinType() default JoinType.INNER;

    public static enum JoinType {
        INNER,
        LEFT,
        RIGHT,
        COLUMN;

        JoinType() {
        }
    }
}
