/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.codec.Base62
 *  com.github.f4b6a3.uuid.UuidCreator
 */
package com.kuma.boot.common.utils.id;

import cn.hutool.core.codec.Base62;
import com.github.f4b6a3.uuid.UuidCreator;
import com.kuma.boot.common.utils.secure.AESUtils;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UuidUtils {
    public static UUID generateV7() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    public static byte[] toBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static UUID fromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static String generateOrderId(Long userId) {
        UUID uuid = UuidUtils.generateV7();
        long timestamp = uuid.getMostSignificantBits() >>> 16 & 0xFFFFFFFFFFFFL;
        String date = new SimpleDateFormat("yyMMdd").format(new Date(timestamp));
        return String.format("ORD%s%s%04d", date, String.format("%06d", userId % 1000000L), uuid.getLeastSignificantBits() & 0xFFFFL);
    }

    public static String generatePaymentId() {
        UUID uuid = UuidUtils.generateV7();
        byte[] encrypted = AESUtils.encrypt(uuid.toString(), "11111111111");
        return Base62.encode((byte[])encrypted).substring(0, 22);
    }

    public static String generateTrackingNo(String regionCode) {
        UUID uuid = UuidUtils.generateV7();
        return String.format("%s%s%s", regionCode, new SimpleDateFormat("yyMMdd").format(new Date()), uuid.toString().substring(24));
    }
}

