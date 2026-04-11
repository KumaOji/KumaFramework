package com.kuma.boot.data.elasticsearch.kcloud.annotation;

public @interface Filter {
   String name();

   Option[] options();
}
