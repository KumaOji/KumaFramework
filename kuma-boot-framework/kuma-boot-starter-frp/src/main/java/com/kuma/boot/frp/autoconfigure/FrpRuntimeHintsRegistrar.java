package com.kuma.boot.frp.autoconfigure;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * 注册内置 frpc 二进制资源，供 GraalVM native image 编译时保留
 *
 * @author kuma
 */
public class FrpRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("frpc/*");
    }
}
