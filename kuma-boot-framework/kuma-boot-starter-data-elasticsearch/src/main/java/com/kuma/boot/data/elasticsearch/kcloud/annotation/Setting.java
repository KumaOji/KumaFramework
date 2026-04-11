package com.kuma.boot.data.elasticsearch.kcloud.annotation;

public @interface Setting {
   short shards() default 1;

   short replicas() default 1;

   String refreshInterval() default "1s";
}
