/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tree;

import com.kuma.boot.common.support.tree.AbstractTreeNode;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractTree<V, I, N extends AbstractTreeNode<V, I, N>> {
    protected Type type;
    protected N root;
    protected Set<N> topNodes;
    private int size;

    public <C extends Collection<N>> AbstractTree(C nodes) {
        this(nodes, false);
    }

    public <C extends Collection<N>> AbstractTree(C nodes, OrphanPolicy orphanPolicy) {
        this(nodes, false, orphanPolicy);
    }

    public <C extends Collection<N>> AbstractTree(C nodes, boolean noRoot) {
        this(nodes, noRoot, null);
    }

    public <C extends Collection<N>> AbstractTree(C nodes, boolean noRoot, OrphanPolicy orphanPolicy) {
        if (nodes == null || nodes.isEmpty()) {
            throw new RuntimeException("Nodes can not be empty");
        }
        this.type = noRoot ? Type.FOREST : Type.TREE;
        this.treeize(nodes, noRoot, orphanPolicy);
    }

    private <C extends Collection<N>> void treeize(C nodes, boolean noRoot, OrphanPolicy orphanPolicy) {
        Map map = nodes.stream().collect(HashMap::new, (m, e) -> m.put(e.extractIdentifier(), e), Map::putAll);
        LinkedList<AbstractTreeNode> orphans = new LinkedList<AbstractTreeNode>();
        for (AbstractTreeNode node : nodes) {
            if (!node.isValidNode()) continue;
            if (node.isTopNode()) {
                if (noRoot) {
                    if (this.topNodes == null) {
                        this.topNodes = this.initTopNodes();
                    }
                    this.topNodes.add(node);
                } else if (this.root == null) {
                    this.root = node;
                } else {
                    throw new IllegalStateException("Found replicated root node " + String.valueOf(node));
                }
                ++this.size;
                continue;
            }
            Object parentIdentifier = node.extractParentIdentifier();
            AbstractTreeNode parentNode = (AbstractTreeNode)map.get(parentIdentifier);
            if (parentNode != null) {
                parentNode.addChild(node);
                ++this.size;
                continue;
            }
            if (orphanPolicy == null || orphanPolicy == OrphanPolicy.DISCARD) continue;
            if (orphanPolicy == OrphanPolicy.REJECT) {
                throw new IllegalStateException("Cannot find parent node '" + String.valueOf(parentIdentifier) + "' for node '" + String.valueOf(node.extractIdentifier()) + "'");
            }
            if (orphanPolicy != OrphanPolicy.TOP) continue;
            orphans.add(node);
        }
        if (!orphans.isEmpty()) {
            if (this.type == Type.FOREST) {
                if (this.topNodes == null) {
                    throw new IllegalStateException("Cannot process orphan nodes because this tree has no top nodes");
                }
                this.topNodes.addAll(orphans);
            } else {
                if (this.root == null) {
                    throw new IllegalStateException("Cannot process orphan nodes because this tree has no root");
                }
                nodes.forEach(arg_0 -> this.root.addChild(arg_0));
            }
            this.size += orphans.size();
        }
    }

    public void dft(Collection<N> fromNodes, Function<N, TraversingAction> visitor) {
        AbstractTreeNode current;
        TraversingAction action;
        LinkedList<N> linkedList = new LinkedList<N>(fromNodes);
        while (!linkedList.isEmpty() && (action = visitor.apply(current = (AbstractTreeNode)linkedList.removeFirst())) != TraversingAction.STOP) {
            if (action == TraversingAction.SKIP || current.isLeaf()) continue;
            linkedList.addAll(0, current.getChildren());
        }
    }

    public void dft(N fromNode, Function<N, TraversingAction> visitor) {
        this.dft((N)Collections.singletonList(fromNode), visitor);
    }

    public void dft(Function<N, TraversingAction> visitor) {
        if (this.type == Type.TREE) {
            this.dft(this.root, visitor);
        } else {
            this.dft((N)this.topNodes, visitor);
        }
    }

    public void dft(Collection<N> fromNodes, Consumer<N> consumer) {
        this.dft((N)fromNodes, this.continueTraversing(consumer));
    }

    public void dft(N fromNode, Consumer<N> consumer) {
        this.dft(fromNode, this.continueTraversing(consumer));
    }

    public void dft(Consumer<N> consumer) {
        this.dft(this.continueTraversing(consumer));
    }

    private Function<N, TraversingAction> continueTraversing(Consumer<N> consumer) {
        return x -> {
            consumer.accept(x);
            return TraversingAction.CONTINUE;
        };
    }

    public List<N> flat(Collection<N> fromNodes, Predicate<N> predicate) {
        ArrayList list = new ArrayList();
        this.dft((N)fromNodes, (Consumer<N>)((Consumer<AbstractTreeNode>)current -> {
            if (predicate.test(current)) {
                list.add(current);
            }
        }));
        return list;
    }

    public List<N> flat(N fromNode, Predicate<N> predicate) {
        return this.flat((N)Collections.singletonList(fromNode), predicate);
    }

    public List<N> flat(Collection<N> fromNodes) {
        ArrayList list = new ArrayList();
        this.dft((N)fromNodes, (Consumer<N>)((Consumer<AbstractTreeNode>)current -> list.add(current)));
        return list;
    }

    public List<N> flat(N fromNode) {
        return this.flat((N)Collections.singletonList(fromNode));
    }

    public List<N> flat(Predicate<N> predicate) {
        if (this.type == Type.TREE) {
            return this.flat(this.root, predicate);
        }
        return this.flat((N)this.topNodes, predicate);
    }

    public List<N> flat() {
        if (this.type == Type.TREE) {
            return this.flat(this.root);
        }
        return this.flat((N)this.topNodes);
    }

    public N dfs(Collection<N> fromNodes, Predicate<N> predicate) {
        AtomicReference nodeRef = new AtomicReference();
        this.dft((N)fromNodes, (Function<N, TraversingAction>)((Function<AbstractTreeNode, TraversingAction>)current -> {
            if (predicate.test(current)) {
                nodeRef.set(current);
            }
            return nodeRef.get() == null ? TraversingAction.CONTINUE : TraversingAction.STOP;
        }));
        return (N)((AbstractTreeNode)nodeRef.get());
    }

    public N dfs(N fromNode, Predicate<N> predicate) {
        return (N)this.dfs((N)Collections.singletonList(fromNode), predicate);
    }

    public N dfs(Predicate<N> predicate) {
        if (this.type == Type.TREE) {
            return this.dfs(this.root, predicate);
        }
        return (N)this.dfs((N)this.topNodes, predicate);
    }

    public int cachedSize() {
        return this.size;
    }

    public int size() {
        int[] temp = new int[]{0};
        this.dft((N node) -> {
            temp[0] = temp[0] + 1;
        });
        return temp[0];
    }

    public void print(Consumer<String> printer, Function<N, String> formatter, String firstPrefix, String otherPrefix) {
        this.dft((N x) -> {
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            int len = x.getLayer();
            while (i < len) {
                stringBuilder.append(firstPrefix != null ? firstPrefix : otherPrefix);
                while (++i < len) {
                    stringBuilder.append(otherPrefix);
                }
            }
            stringBuilder.append((String)formatter.apply(x));
            printer.accept(stringBuilder.toString());
        });
    }

    public void print(Consumer<String> printer, Function<N, String> formatter, String prefix) {
        this.print(printer, formatter, null, prefix);
    }

    public void print(PrintStream printStream, Function<N, String> formatter, String firstPrefix, String otherPrefix) {
        this.print(printStream::println, formatter, firstPrefix, otherPrefix);
    }

    public void print(PrintStream printStream, Function<N, String> formatter, String prefix) {
        this.print(printStream, formatter, null, prefix);
    }

    public <T> Map<T, N> toMap(Function<N, T> keyMapping, Predicate<N> predicate) {
        LinkedHashMap map = new LinkedHashMap();
        this.dft((N x) -> {
            if (predicate.test(x)) {
                map.put(keyMapping.apply(x), x);
            }
        });
        return map;
    }

    public <T> Map<T, N> toMap(Function<N, T> keyMapping) {
        LinkedHashMap map = new LinkedHashMap();
        this.dft((N x) -> map.put(keyMapping.apply(x), x));
        return map;
    }

    protected Set<N> initTopNodes() {
        return new LinkedHashSet();
    }

    public Type getType() {
        return this.type;
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]").add("type=" + String.valueOf((Object)this.getType())).add("size=" + this.size()).toString();
    }

    public static enum OrphanPolicy {
        DISCARD,
        TOP,
        REJECT;

    }

    public static enum Type {
        TREE,
        FOREST;

    }

    public static enum TraversingAction {
        CONTINUE,
        STOP,
        SKIP;

    }
}

