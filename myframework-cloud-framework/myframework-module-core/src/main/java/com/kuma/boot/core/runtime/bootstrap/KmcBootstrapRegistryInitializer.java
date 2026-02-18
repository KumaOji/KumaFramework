package com.kuma.boot.core.runtime.bootstrap;

import org.springframework.boot.bootstrap.BootstrapRegistry;
import org.springframework.boot.bootstrap.BootstrapRegistryInitializer;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLocationResolver;

/**
 * KmcBootstrapRegistryInitializer
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class KmcBootstrapRegistryInitializer implements BootstrapRegistryInitializer {

    @Override
    public void initialize( BootstrapRegistry registry ) {
//		// 1. 注册 Token 提供者
//		registry.register(VaultTokenSupplier.class, ctx -> new StaticTokenSupplier("token"));
//
//		// 2. 注册 ConfigData 解析器
//		registry.register(ConfigDataLocationResolver.class, ctx ->
//			new VaultConfigDataLocationResolver(ctx.get(VaultTokenSupplier.class))
//		);
//
//		// 3. 注册 ConfigData 加载器
//		registry.register(ConfigDataLoader.class, ctx ->
//			new VaultConfigDataLoader(ctx.get(VaultTokenSupplier.class))
//		);
    }
}
