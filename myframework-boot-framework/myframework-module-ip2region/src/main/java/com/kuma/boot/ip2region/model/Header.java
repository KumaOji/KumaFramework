/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.model;

public class Header {
    public final int version;
    public final int indexPolicy;
    public final int createdAt;
    public final int startIndexPtr;
    public final int endIndexPtr;

    public Header(byte[] buff) {
        assert (buff.length >= 16);
        this.version = Searcher.getInt2(buff, 0);
        this.indexPolicy = Searcher.getInt2(buff, 2);
        this.createdAt = Searcher.getInt(buff, 4);
        this.startIndexPtr = Searcher.getInt(buff, 8);
        this.endIndexPtr = Searcher.getInt(buff, 12);
    }

    public String toString() {
        return "{Version: " + this.version + ",IndexPolicy" + this.indexPolicy + ",CreatedAt" + this.createdAt + ",StartIndexPtr" + this.startIndexPtr + ",EndIndexPtr" + this.endIndexPtr + "}";
    }
}

