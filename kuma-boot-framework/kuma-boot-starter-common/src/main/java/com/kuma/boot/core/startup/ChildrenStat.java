package com.kuma.boot.core.startup;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChildrenStat<T extends BaseStat> extends BaseStat {

    private List<T> children = new CopyOnWriteArrayList<>();

    public void addChild(T child) { this.children.add(child); }

    public List<T> getChildren() { return children; }
    public void setChildren(List<T> children) { this.children = children; }
}
