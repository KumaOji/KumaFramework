package com.kuma.boot.client.forest.auth;

import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.RequestAttributes;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@MethodLifeCycle(MyAuthLifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MyAuth {
   String username();

   String password();
}
