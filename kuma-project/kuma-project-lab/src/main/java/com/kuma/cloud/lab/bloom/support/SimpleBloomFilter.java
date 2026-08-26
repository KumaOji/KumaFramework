package com.kuma.cloud.lab.bloom.support;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * 简易布隆过滤器实现，用于演示核心原理。
 * <p>
 * 通过多个哈希函数将元素映射到位数组，支持「可能存在」与「一定不存在」两种判定。
 */
public class SimpleBloomFilter {

    private final BitSet bitSet;
    private final int bitSize;
    private final int hashFunctions;
    private int insertedCount;

    public SimpleBloomFilter(int expectedInsertions, double falsePositiveProbability) {
        this.bitSize = optimalBitSize(expectedInsertions, falsePositiveProbability);
        this.hashFunctions = optimalHashFunctions(bitSize, expectedInsertions);
        this.bitSet = new BitSet(bitSize);
    }

    public void add(String value) {
        for (int seed = 0; seed < hashFunctions; seed++) {
            bitSet.set(index(value, seed));
        }
        insertedCount++;
    }

    public boolean mightContain(String value) {
        for (int seed = 0; seed < hashFunctions; seed++) {
            if (!bitSet.get(index(value, seed))) {
                return false;
            }
        }
        return true;
    }

    public int getBitSize() {
        return bitSize;
    }

    public int getHashFunctions() {
        return hashFunctions;
    }

    public int getInsertedCount() {
        return insertedCount;
    }

    public int getSetBitCount() {
        return bitSet.cardinality();
    }

    private int index(String value, int seed) {
        int hash = hash(value, seed);
        return Math.floorMod(hash, bitSize);
    }

    private static int hash(String value, int seed) {
        int hash = seed;
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            hash = 31 * hash + b;
        }
        hash ^= (hash >>> 16);
        return hash;
    }

    private static int optimalBitSize(int expectedInsertions, double falsePositiveProbability) {
        double size = -expectedInsertions * Math.log(falsePositiveProbability) / (Math.log(2) * Math.log(2));
        return Math.max(64, (int) Math.ceil(size));
    }

    private static int optimalHashFunctions(int bitSize, int expectedInsertions) {
        int functions = (int) Math.round((double) bitSize / expectedInsertions * Math.log(2));
        return Math.max(1, functions);
    }

}
