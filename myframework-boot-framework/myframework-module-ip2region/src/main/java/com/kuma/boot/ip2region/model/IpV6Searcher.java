/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.model;

import com.kuma.boot.ip2region.utils.IpInfoUtil;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class IpV6Searcher {
    private final byte[] data;
    private final long total;
    private final long indexStartOffset;
    private final int offLen;
    private final int ipLen;

    public static IpV6Searcher newWithBuffer(byte[] cBuff) {
        return new IpV6Searcher(cBuff);
    }

    private IpV6Searcher(byte[] cBuff) {
        this.data = cBuff;
        this.indexStartOffset = this.read8(16L);
        this.offLen = this.read1(6L);
        this.ipLen = this.read1(7L);
        this.total = this.read8(8L);
    }

    public IpInfo query(String ip) {
        return this.query(new IpAddress(ip));
    }

    public IpInfo query(IpAddress ip) {
        long ipFind = this.find(ip, 0L, this.total);
        long ipOffset = this.indexStartOffset + ipFind * (long)(this.ipLen + this.offLen);
        long ipRecordOffset = this.read8(ipOffset + (long)this.ipLen, this.offLen);
        return IpInfoUtil.toIpV6Info(this.readRecord(ipRecordOffset));
    }

    private long find(IpAddress ip, long l, long r) {
        if (l + 1L >= r) {
            return l;
        }
        long m = (l + r) / 2L;
        byte[] mip = this.readRaw(this.indexStartOffset + m * (long)(this.ipLen + this.offLen), this.ipLen);
        IpAddress aip = IpAddress.fromBytesV6LE(mip);
        if (ip.compareTo(aip) < 0) {
            return this.find(ip, l, m);
        }
        return this.find(ip, m, r);
    }

    private String[] readRecord(long offset) {
        String[] recordInfo = new String[2];
        int flag = this.read1(offset);
        if (flag == 1) {
            long locationOffset = this.read8(offset + 1L, this.offLen);
            return this.readRecord(locationOffset);
        }
        byte[] rec0 = this.readLocation(offset);
        byte[] rec1 = flag == 2 ? this.readLocation(offset + (long)this.offLen + 1L) : this.readLocation(offset + (long)rec0.length + 1L);
        recordInfo[0] = new String(rec0, StandardCharsets.UTF_8);
        recordInfo[1] = new String(rec1, StandardCharsets.UTF_8);
        return recordInfo;
    }

    private byte[] readLocation(long offset) {
        if (offset == 0L) {
            return new byte[0];
        }
        int flag = this.read1(offset);
        if (flag == 0) {
            return new byte[0];
        }
        if (flag == 2) {
            offset = this.read8(offset + 1L, this.offLen);
            return this.readLocation(offset);
        }
        return this.readStr(offset);
    }

    private byte[] readRaw(byte[] dist, long offset, int size) {
        System.arraycopy(this.data, (int)offset, dist, 0, size);
        return dist;
    }

    private byte[] readRaw(long offset, int size) {
        byte[] bytes = new byte[size];
        return this.readRaw(bytes, offset, size);
    }

    private int read1(long offset) {
        return this.data[(int)offset];
    }

    private long read8(long offset) {
        return this.read8((int)offset, 8);
    }

    private long read8(long offset, int size) {
        byte[] b = new byte[8];
        this.readRaw(b, offset, size);
        ByteBuffer buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getLong();
    }

    private byte[] readStr(long offset) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(64);
        int ch = this.read1(offset);
        while (ch != 0) {
            os.write(ch);
            ch = this.read1(++offset);
        }
        return os.toByteArray();
    }
}

