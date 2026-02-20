package com.kuma.boot.common.utils.id;

import cn.hutool.core.codec.Base62;
import com.github.f4b6a3.uuid.UuidCreator;
import com.kuma.boot.common.utils.secure.AESUtils;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * UuidUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class UuidUtils {

    /**
     * 生成UUIDv7
     */
    public static UUID generateV7() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    /**
     * UUID转字节数组（数据库存储优化）
     */
    public static byte[] toBytes( UUID uuid ) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * 字节数组转UUID
     */
    public static UUID fromBytes( byte[] bytes ) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static String generateOrderId( Long userId ) {
        // 生成UUIDv7
        UUID uuid = UuidUtils.generateV7();
        // 提取时间戳部分（前48位）转为日期字符串
        long timestamp = ( uuid.getMostSignificantBits() >>> 16 ) & 0xFFFFFFFFFFFFL;
        String date = new SimpleDateFormat("yyMMdd").format(new Date(timestamp));
        // 组合用户ID后6位+UUID后4位，生成业务可读ID
        return String.format("ORD%s%s%04d", date,
                String.format("%06d", userId % 1000000),
                uuid.getLeastSignificantBits() & 0xFFFF);
    }

    public static String generatePaymentId() {
        UUID uuid = UuidUtils.generateV7();
        // 使用AES加密UUID核心部分
        byte[] encrypted = AESUtils.encrypt(uuid.toString(), "11111111111");
        // 转为Base62编码，缩短长度
        return Base62.encode(encrypted).substring(0, 22);
    }

    public static String generateTrackingNo( String regionCode ) {
        UUID uuid = UuidUtils.generateV7();
        // 区域编码(2位)+时间戳(6位)+UUID尾号(8位)
        return String.format("%s%s%s",
                regionCode,
                new SimpleDateFormat("yyMMdd").format(new Date()),
                uuid.toString().substring(24));
    }

}
