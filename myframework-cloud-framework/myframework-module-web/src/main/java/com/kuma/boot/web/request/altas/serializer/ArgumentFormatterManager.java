package com.kuma.boot.web.request.altas.serializer;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数格式化器管理器
 * 负责管理和选择不同的格式化器
 *
 * @author nemoob
 * @since 0.2.0
 */

public class ArgumentFormatterManager {

    private final Map<String, com.kuma.boot.web.request.altas.serializer.ArgumentFormatter> formatters = new ConcurrentHashMap<>();
    private final com.kuma.boot.web.request.altas.serializer.ArgumentFormatter defaultFormatter;
    private final String defaultFormatterName;

    public ArgumentFormatterManager(com.kuma.boot.web.request.altas.serializer.ArgumentFormatter defaultFormatter, String defaultFormatterName) {
        this.defaultFormatter = defaultFormatter;
        this.defaultFormatterName = defaultFormatterName;

        // 注册默认格式化器
        registerFormatter(defaultFormatterName, defaultFormatter);

        LogUtils.info("ArgumentFormatterManager initialized with default formatter: {}", defaultFormatterName);
    }

    /**
     * 注册格式化器
     *
     * @param name 格式化器名称
     * @param formatter 格式化器实例
     */
    public void registerFormatter(String name, com.kuma.boot.web.request.altas.serializer.ArgumentFormatter formatter) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Formatter name cannot be null or empty");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("Formatter cannot be null");
        }

        formatters.put(name.toLowerCase(), formatter);
        LogUtils.debug("Registered formatter: {} -> {}", name, formatter.getClass().getSimpleName());
    }

    /**
     * 获取格式化器
     *
     * @param name 格式化器名称
     * @return 格式化器实例，如果不存在则返回默认格式化器
     */
    public com.kuma.boot.web.request.altas.serializer.ArgumentFormatter getFormatter(String name) {
        if (name == null || name.trim().isEmpty()) {
            return defaultFormatter;
        }

        com.kuma.boot.web.request.altas.serializer.ArgumentFormatter formatter = formatters.get(name.toLowerCase());
        if (formatter == null) {
            LogUtils.warn("Formatter '{}' not found, using default formatter '{}'", name, defaultFormatterName);
            return defaultFormatter;
        }

        return formatter;
    }

    /**
     * 获取默认格式化器
     *
     * @return 默认格式化器
     */
    public com.kuma.boot.web.request.altas.serializer.ArgumentFormatter getDefaultFormatter() {
        return defaultFormatter;
    }

    /**
     * 获取默认格式化器名称
     *
     * @return 默认格式化器名称
     */
    public String getDefaultFormatterName() {
        return defaultFormatterName;
    }

    /**
     * 检查格式化器是否存在
     *
     * @param name 格式化器名称
     * @return 是否存在
     */
    public boolean hasFormatter(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return formatters.containsKey(name.toLowerCase());
    }

    /**
     * 获取所有已注册的格式化器名称
     *
     * @return 格式化器名称集合
     */
    public java.util.Set<String> getFormatterNames() {
        return new java.util.HashSet<>(formatters.keySet());
    }

    /**
     * 移除格式化器
     *
     * @param name 格式化器名称
     * @return 是否成功移除
     */
    public boolean removeFormatter(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // 不允许移除默认格式化器
        if (defaultFormatterName.equalsIgnoreCase(name)) {
            LogUtils.warn("Cannot remove default formatter: {}", name);
            return false;
        }

        com.kuma.boot.web.request.altas.serializer.ArgumentFormatter removed = formatters.remove(name.toLowerCase());
        if (removed != null) {
            LogUtils.debug("Removed formatter: {}", name);
            return true;
        }

        return false;
    }

    /**
     * 格式化方法参数
     *
     * @param formatterName 格式化器名称
     * @param args 方法参数
     * @param context 格式化上下文
     * @return 格式化后的字符串
     */
    public String formatArguments(String formatterName, Object[] args, com.kuma.boot.web.request.altas.serializer.ArgumentFormatter.FormatterContext context) {
        com.kuma.boot.web.request.altas.serializer.ArgumentFormatter formatter = getFormatter(formatterName);
        return formatter.formatArguments(args, context);
    }

    /**
     * 格式化方法返回值
     *
     * @param formatterName 格式化器名称
     * @param result 方法返回值
     * @param context 格式化上下文
     * @return 格式化后的字符串
     */
    public String formatResult(String formatterName, Object result, com.kuma.boot.web.request.altas.serializer.ArgumentFormatter.FormatterContext context) {
        com.kuma.boot.web.request.altas.serializer.ArgumentFormatter formatter = getFormatter(formatterName);
        return formatter.formatResult(result, context);
    }

    /**
     * 格式化HTTP请求参数
     *
     * @param formatterName 格式化器名称
     * @param parameters HTTP请求参数
     * @param context 格式化上下文
     * @return 格式化后的字符串
     */
    public String formatHttpParameters(String formatterName, Map<String, String[]> parameters, com.kuma.boot.web.request.altas.serializer.ArgumentFormatter.FormatterContext context) {
        com.kuma.boot.web.request.altas.serializer.ArgumentFormatter formatter = getFormatter(formatterName);
        return formatter.formatHttpParameters(parameters, context);
    }
}
