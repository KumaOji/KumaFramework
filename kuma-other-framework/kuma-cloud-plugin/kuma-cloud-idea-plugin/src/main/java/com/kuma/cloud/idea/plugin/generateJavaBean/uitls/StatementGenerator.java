package com.kuma.cloud.idea.plugin.generateJavaBean.uitls;

/**
 * StatementGenerator
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
public class StatementGenerator {

    public static String defaultGetFormat = "/**\n * 获取\n * @return ${field.name} \n */ ";
    public static String defaultSetFormat = "/**\n * 设置\n * @param ${field.name} \n */ ";

    public StatementGenerator() {
    }
}
