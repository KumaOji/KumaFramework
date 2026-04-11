package com.kuma.boot.data.elasticsearch.es.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Inherited
public @interface EsField {
   FieldType type() default FieldType.TEXT;

   AnalyzerType analyzer() default AnalyzerType.IK_SMART;

   AnalyzerType searchAnalyzer() default AnalyzerType.IK_MAX_WORD;
}
