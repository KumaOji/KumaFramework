/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.id;

import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.id.UuidUtils;
import java.util.Date;

public final class IdGeneratorUtils {
    private IdGeneratorUtils() {
    }

    public static String getId() {
        String id = UuidUtils.generateV7().toString();
        id = id.replace("-", "");
        return id;
    }

    public static String getIdStr() {
        String id = UuidUtils.generateV7().toString();
        id = id.replace("-", "");
        return id;
    }

    public static String createStr(String prefix) {
        return prefix + DateUtils.toString(new Date(), "yyyyMMdd") + IdGeneratorUtils.getId();
    }
}

