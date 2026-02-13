/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.tree;

import com.kuma.boot.common.tree.ForestNodeManager;
import com.kuma.boot.common.tree.INode;
import java.util.List;

public class ForestNodeMerger {
    public static <T extends INode> List<T> merge(List<T> items) {
        ForestNodeManager forestNodeManager = new ForestNodeManager(items);
        items.forEach(forestNode -> {
            if (forestNode.getParentId() != 0L) {
                INode node = forestNodeManager.getTreeNodeAT(forestNode.getParentId());
                if (node != null) {
                    node.getChildren().add(forestNode);
                } else {
                    forestNodeManager.addParentId(forestNode.getId());
                }
            }
        });
        return forestNodeManager.getRoot();
    }
}

