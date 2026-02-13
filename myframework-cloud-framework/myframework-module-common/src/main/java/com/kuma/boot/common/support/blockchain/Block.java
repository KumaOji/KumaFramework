/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.blockchain;

import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.secure.SHAUtils;

public class Block {
    public String hash;
    public String previousHash;
    private String data;
    private long timestamp;
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = DateUtils.getTimestamp();
        this.hash = this.calculateHash();
    }

    public String calculateHash() {
        return SHAUtils.encrypt256(this.previousHash + this.timestamp + this.nonce + this.data);
    }

    public String mineBlock(int difficulty) {
        String target = Block.getDificultyString(difficulty);
        while (!this.hash.substring(0, difficulty).equals(target)) {
            ++this.nonce;
            this.hash = this.calculateHash();
        }
        LogUtils.info("Block Mined: " + this.hash, new Object[0]);
        return this.hash;
    }

    private static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\u0000', '0');
    }

    public String getData() {
        return this.data;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public int getNonce() {
        return this.nonce;
    }
}

