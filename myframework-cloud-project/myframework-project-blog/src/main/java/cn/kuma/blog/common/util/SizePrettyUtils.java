package cn.kuma.blog.common.util;

import java.text.DecimalFormat;

public class SizePrettyUtils {

    private static final String[] SIZE_UNITS = new String[] { "B", "KB", "MB", "GB", "TB" };

    public static String pretty(long size) {
        if (size <= 0) {
            return "0 B";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + SIZE_UNITS[digitGroups];

    }

}
