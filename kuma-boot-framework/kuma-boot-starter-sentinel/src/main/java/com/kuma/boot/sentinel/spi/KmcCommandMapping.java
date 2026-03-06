package com.kuma.boot.sentinel.spi;

import com.alibaba.csp.sentinel.command.annotation.CommandMapping;

import java.lang.annotation.Annotation;

//由 CommandHandlerProvider 类触发
public class KmcCommandMapping implements CommandMapping {
    @Override
    public String name() {
        return "";
    }

    @Override
    public String desc() {
        return "";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
