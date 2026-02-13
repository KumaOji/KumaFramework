/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TreeUtil {
    public static <T, E> List<E> makeTree(List<E> menuList, Function<E, T> pId, Function<E, T> id, Predicate<E> rootCheck, BiConsumer<E, List<E>> setSubChildren) {
        Map parentMenuMap = menuList.stream().collect(Collectors.groupingBy(node -> Optional.ofNullable(pId.apply(node)), LinkedHashMap::new, Collectors.toList()));
        ArrayList<E> result = new ArrayList<E>();
        for (E node2 : menuList) {
            setSubChildren.accept(node2, (List)parentMenuMap.get(Optional.ofNullable(id.apply(node2))));
            if (!rootCheck.test(node2)) continue;
            result.add(node2);
        }
        return result;
    }

    public static <E> List<E> filter(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getChildren) {
        return tree.stream().filter(item -> {
            if (predicate.test(item)) {
                List children = (List)getChildren.apply(item);
                if (children != null && !children.isEmpty()) {
                    TreeUtil.filter(children, predicate, getChildren);
                }
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static <E> List<E> search(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getSubChildren) {
        Iterator<E> iterator = tree.iterator();
        while (iterator.hasNext()) {
            E item = iterator.next();
            List<E> childList = getSubChildren.apply(item);
            if (childList != null && !childList.isEmpty()) {
                TreeUtil.search(childList, predicate, getSubChildren);
            }
            if (predicate.test(item) || childList != null && !childList.isEmpty()) continue;
            iterator.remove();
        }
        return tree;
    }
}

