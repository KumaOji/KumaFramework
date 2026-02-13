/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.tree;

import java.io.Serializable;
import java.util.List;

public interface INode extends Serializable {
    public Long getId();

    public Long getParentId();

    public <T extends INode> List<T> getChildren();

    default public Boolean getHasChildren() {
        return false;
    }
}

