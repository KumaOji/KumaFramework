/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Searcher {
    public static final int HEADER_INFO_LENGTH = 256;
    public static final int VECTOR_INDEX_ROWS = 256;
    public static final int VECTOR_INDEX_COLS = 256;
    public static final int VECTOR_INDEX_SIZE = 8;
    public static final int SEGMENT_INDEX_SIZE = 14;
    private final RandomAccessFile handle;
    private int ioCount = 0;
    private final byte[] vectorIndex;
    private final byte[] contentBuff;
    private static final byte[] SHIFT_INDEX = new byte[]{24, 16, 8, 0};

    public static Searcher newWithFileOnly(String dbPath) throws IOException {
        return new Searcher(dbPath, null, null);
    }

    public static Searcher newWithVectorIndex(String dbPath, byte[] vectorIndex) throws IOException {
        return new Searcher(dbPath, vectorIndex, null);
    }

    public static Searcher newWithBuffer(byte[] cBuff) throws IOException {
        return new Searcher(null, null, cBuff);
    }

    public Searcher(String dbFile, byte[] vectorIndex, byte[] cBuff) throws IOException {
        if (cBuff != null) {
            this.handle = null;
            this.vectorIndex = null;
            this.contentBuff = cBuff;
        } else {
            this.handle = new RandomAccessFile(dbFile, "r");
            this.vectorIndex = vectorIndex;
            this.contentBuff = null;
        }
    }

    public void close() throws IOException {
        if (this.handle != null) {
            this.handle.close();
        }
    }

    public int getIOCount() {
        return this.ioCount;
    }

    public String search(String[] ipParts) throws IOException {
        return this.search(Searcher.getIpAdder(ipParts));
    }

    public String search(long ip) throws IOException {
        byte[] buff;
        int ePtr;
        int sPtr;
        this.ioCount = 0;
        int il0 = (int)(ip >> 24 & 0xFFL);
        int il1 = (int)(ip >> 16 & 0xFFL);
        int idx = il0 * 256 * 8 + il1 * 8;
        if (this.vectorIndex != null) {
            sPtr = Searcher.getInt(this.vectorIndex, idx);
            ePtr = Searcher.getInt(this.vectorIndex, idx + 4);
        } else if (this.contentBuff != null) {
            sPtr = Searcher.getInt(this.contentBuff, 256 + idx);
            ePtr = Searcher.getInt(this.contentBuff, 256 + idx + 4);
        } else {
            buff = new byte[8];
            this.read(256 + idx, buff);
            sPtr = Searcher.getInt(buff, 0);
            ePtr = Searcher.getInt(buff, 4);
        }
        buff = new byte[14];
        int dataLen = -1;
        int dataPtr = -1;
        int l = 0;
        int h = (ePtr - sPtr) / 14;
        while (l <= h) {
            int m = l + h >> 1;
            int p = sPtr + m * 14;
            this.read(p, buff);
            long sip = Searcher.getIntLong(buff, 0);
            if (ip < sip) {
                h = m - 1;
                continue;
            }
            long eip = Searcher.getIntLong(buff, 4);
            if (ip > eip) {
                l = m + 1;
                continue;
            }
            dataLen = Searcher.getInt2(buff, 8);
            dataPtr = Searcher.getInt(buff, 10);
            break;
        }
        if (dataPtr < 0) {
            return null;
        }
        byte[] regionBuff = new byte[dataLen];
        this.read(dataPtr, regionBuff);
        return new String(regionBuff);
    }

    protected void read(int offset, byte[] buffer) throws IOException {
        if (this.contentBuff != null) {
            System.arraycopy(this.contentBuff, offset, buffer, 0, buffer.length);
            return;
        }
        assert (this.handle != null);
        this.handle.seek(offset);
        ++this.ioCount;
        int rLen = this.handle.read(buffer);
        if (rLen != buffer.length) {
            throw new IOException("incomplete read: read bytes should be " + buffer.length);
        }
    }

    public static Header loadHeader(RandomAccessFile handle) throws IOException {
        handle.seek(0L);
        byte[] buff = new byte[256];
        handle.read(buff);
        return new Header(buff);
    }

    public static Header loadHeaderFromFile(String dbPath) throws IOException {
        RandomAccessFile handle = new RandomAccessFile(dbPath, "r");
        return Searcher.loadHeader(handle);
    }

    public static byte[] loadVectorIndex(RandomAccessFile handle) throws IOException {
        handle.seek(256L);
        int len = 917504;
        byte[] buff = new byte[len];
        int rLen = handle.read(buff);
        if (rLen != len) {
            throw new IOException("incomplete read: read bytes should be " + len);
        }
        return buff;
    }

    public static byte[] loadVectorIndexFromFile(String dbPath) throws IOException {
        RandomAccessFile handle = new RandomAccessFile(dbPath, "r");
        return Searcher.loadVectorIndex(handle);
    }

    public static byte[] loadContent(RandomAccessFile handle) throws IOException {
        handle.seek(0L);
        byte[] buff = new byte[(int)handle.length()];
        int rLen = handle.read(buff);
        if (rLen != buff.length) {
            throw new IOException("incomplete read: read bytes should be " + buff.length);
        }
        return buff;
    }

    public static byte[] loadContentFromFile(String dbPath) throws IOException {
        RandomAccessFile handle = new RandomAccessFile(dbPath, "r");
        return Searcher.loadContent(handle);
    }

    public static long getIntLong(byte[] b, int offset) {
        return (long)b[offset++] & 0xFFL | (long)(b[offset++] << 8) & 0xFF00L | (long)(b[offset++] << 16) & 0xFF0000L | (long)(b[offset] << 24) & 0xFF000000L;
    }

    public static int getInt(byte[] b, int offset) {
        return b[offset++] & 0xFF | b[offset++] << 8 & 0xFF00 | b[offset++] << 16 & 0xFF0000 | b[offset] << 24 & 0xFF000000;
    }

    public static int getInt2(byte[] b, int offset) {
        return b[offset++] & 0xFF | b[offset] & 0xFF00;
    }

    public static String long2ip(long ip) {
        return String.valueOf(ip >> 24 & 0xFFL) + "." + (ip >> 16 & 0xFFL) + "." + (ip >> 8 & 0xFFL) + "." + (ip & 0xFFL);
    }

    public static long getIpAdder(String[] ipParts) {
        long ipAdder = 0L;
        for (int i = 0; i < ipParts.length; ++i) {
            int val = Integer.parseInt(ipParts[i]);
            if (val > 255) {
                throw new IllegalArgumentException("ip part `" + ipParts[i] + "` should be less then 256");
            }
            ipAdder |= (long)val << SHIFT_INDEX[i];
        }
        return ipAdder & 0xFFFFFFFFL;
    }
}

