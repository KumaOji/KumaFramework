package com.kuma.boot.data.elasticsearch.kcloud.annotation;

public @interface Analysis {
   Filter[] filters() default {};

   Analyzer[] analyzers() default {};
}
