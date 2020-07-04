package com.keeplive.hln.network.annotation;

import com.keeplive.hln.network.NetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetworkAnnotation {
    NetType netType() default NetType.AUTO;
}
