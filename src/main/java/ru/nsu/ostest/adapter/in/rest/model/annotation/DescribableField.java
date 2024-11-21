package ru.nsu.ostest.adapter.in.rest.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DescribableField {
    boolean includeInFilter() default false;

    boolean includeInDescriptor() default false;
}