package cn.kuma.blog.common.util;

import java.text.NumberFormat;

public class NumberFormatUtility {

    private static final NumberFormat INSTANCE = NumberFormat.getInstance();

    private static final NumberFormat PERCENT_INSTANCE = NumberFormat.getPercentInstance();

    static {
        INSTANCE.setMinimumFractionDigits(2);
        INSTANCE.setMaximumFractionDigits(2);
        PERCENT_INSTANCE.setMinimumFractionDigits(2);
        PERCENT_INSTANCE.setMaximumFractionDigits(2);
    }

    public static String percentFormat(Double number) {
        return PERCENT_INSTANCE.format(number);
    }

    public static void main(String[] args) {
        System.out.println(format(10.12312));
        System.out.println(percentFormat(0.12312));
    }

    public static String format(Number number) {
        return INSTANCE.format(number);
    }

}
