/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

/**
 * 树操作方法工具类
 */
public class TreeUtil {

    /**
     * 使用Map合成树
     * @param menuList 需要合成树的List
     * @param pId 对象中的父ID字段,如:Menu:getPid
     * @param id 对象中的id字段 ,如：Menu:getId
     * @param rootCheck 判断E中为根节点的条件，如：x->x.getPId()==-1L ,
     * x->x.getParentId()==null,x->x.getParentMenuId()==0
     * @param setSubChildren E中设置下级数据方法，如： Menu::setSubMenus
     * @param <T> ID字段类型
     * @param <E> 泛型实体对象
     * @return
     */
    public static <T, E> List<E> makeTree(
            List<E> menuList,
            Function<E, T> pId,
            Function<E, T> id,
            Predicate<E> rootCheck,
            BiConsumer<E, List<E>> setSubChildren) {
        // 按原数组顺序构建父级数据Map，使用Optional考虑pId为null
        Map<Optional<T>, List<E>> parentMenuMap =
                menuList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        node -> Optional.ofNullable(pId.apply(node)),
                                        LinkedHashMap::new,
                                        Collectors.toList()));
        List<E> result = new ArrayList<>();
        for (E node : menuList) {
            // 添加到下级数据中
            setSubChildren.accept(node, parentMenuMap.get(Optional.ofNullable(id.apply(node))));
            // 如里是根节点，加入结构
            if (rootCheck.test(node)) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 树中过滤
     * @param tree 需要过滤的树
     * @param predicate 过滤条件
     * @param getChildren 获取下级数据方法，如：MenuVo::getSubMenus
     * @param <E> 泛型实体对象
     * @return List<E> 过滤后的树
     */
    public static <E> List<E> filter(
            List<E> tree, Predicate<E> predicate, Function<E, List<E>> getChildren) {
        return tree.stream()
                .filter(
                        item -> {
                            if (predicate.test(item)) {
                                List<E> children = getChildren.apply(item);
                                if (children != null && !children.isEmpty()) {
                                    filter(children, predicate, getChildren);
                                }
                                return true;
                            }
                            return false;
                        })
                .collect(Collectors.toList());
    }

    /**
     * 树中搜索
     * @param tree
     * @param predicate
     * @param getSubChildren
     * @param <E>
     * @return 返回搜索到的节点及其父级到根节点
     */
    public static <E> List<E> search(
            List<E> tree, Predicate<E> predicate, Function<E, List<E>> getSubChildren) {
        Iterator<E> iterator = tree.iterator();
        while (iterator.hasNext()) {
            E item = iterator.next();
            List<E> childList = getSubChildren.apply(item);
            if (childList != null && !childList.isEmpty()) {
                search(childList, predicate, getSubChildren);
            }
            if (!predicate.test(item) && (childList == null || childList.isEmpty())) {
                iterator.remove();
            }
        }
        return tree;
    }
}
