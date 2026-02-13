/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 */
package com.kuma.boot.common.support.blockchain;

import com.kuma.boot.common.support.blockchain.Block;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class BlockChain {
    private static final List<Block> BLOCK_CHAIN = new ArrayList<Block>();
    private static final int DIFFICULTY = 5;

    private BlockChain() {
    }

    public static String minedBlockChain(String data) {
        String hash = BLOCK_CHAIN.isEmpty() ? BlockChain.addBlock(new Block(data, "0")) : BlockChain.addBlock(new Block(data, BlockChain.BLOCK_CHAIN.get((int)(BlockChain.BLOCK_CHAIN.size() - 1)).hash));
        return hash;
    }

    public static String decryptBlockchain(String blockHash) {
        if ("ALL".equalsIgnoreCase(blockHash)) {
            return JacksonUtils.toJSONString(BLOCK_CHAIN);
        }
        List<Block> blockList = BLOCK_CHAIN.parallelStream().filter(b -> b.hash.equals(blockHash)).toList();
        if (CollectionUtils.isNotEmpty(blockList)) {
            return JacksonUtils.toJSONString(blockList);
        }
        return null;
    }

    public static Boolean isChainValid() {
        String hashTarget = new String(new char[5]).replace('\u0000', '0');
        for (int i = 1; i < BLOCK_CHAIN.size(); ++i) {
            Block currentBlock = BLOCK_CHAIN.get(i);
            Block previousBlock = BLOCK_CHAIN.get(i - 1);
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                LogUtils.warn("\u5f53\u524d\u7684Hash\u6563\u5217\u4e0d\u76f8\u7b49", new Object[0]);
                return false;
            }
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                LogUtils.warn("\u4ee5\u524d\u7684Hash\u6563\u5217\u4e0d\u76f8\u7b49", new Object[0]);
                return false;
            }
            if (currentBlock.hash.substring(0, 5).equals(hashTarget)) continue;
            LogUtils.warn("\u5f53\u524d\u5757\u94fe\u8fd8\u6ca1\u6709\u88ab\u5f00\u91c7", new Object[0]);
            return false;
        }
        return true;
    }

    private static String addBlock(Block block) {
        String hash = block.mineBlock(5);
        BLOCK_CHAIN.add(block);
        return hash;
    }
}

