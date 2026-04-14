package com.kuma.boot.translation.translationextension.service;

/**
 * 未接入业务字典数据源时的占位实现，保证容器中存在 {@link DictTranslationService}，避免仅引入翻译扩展时启动失败。
 * <p>
 * 业务侧提供自定义 {@link DictTranslationService} Bean（例如对接字典表、远程接口）即可覆盖本实现。
 * </p>
 */
public final class NoOpDictTranslationService implements DictTranslationService {

    @Override
    public void initDictTranslationCache() {
        // 无字典数据源时不预加载
    }
}
