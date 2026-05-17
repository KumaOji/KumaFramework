/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.core.tree;

import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.tree.TreeUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;

/**
 * 扩展 hutool TreeUtil 封装系统树构建
 */
public class TreeUtils extends TreeUtil {

    /**
     * 根据前端定制差异化字段
     */
    public static final TreeNodeConfig DEFAULT_CONFIG =
            TreeNodeConfig.DEFAULT_CONFIG.setNameKey("label");

//    public static <T, K> List<MapTree<K>> build(List<T> list, NodeParser<T, K> nodeParser) {
//        if (CollUtil.isEmpty(list)) {
//            return null;
//        }
//        K k = ReflectUtils.invokeGetter(list.get(0), "parentId");
//        return TreeUtil.makeTree(list, k, DEFAULT_CONFIG, nodeParser);
//    }

    private static final String CHILD_NAME = "children";

    /**
     * 获取树形结构
     * @param list 需要转换树的集合
     * @param idNameFunc 主键名字的lambda表达式 如：User::getUserId
     * @param parentIdNameFunc 父id名字的lambda表达式 如：User::getParentId
     * @param parentFlag 父类标识 靠此字段判断谁是父类 一般为0
     * @return {@link List<T>}
     */
    public static <T, R, M> Map<String, Object> buildTree(
            List<T> list,
            Function<T, R> idNameFunc,
            Function<T, R> parentIdNameFunc,
            M parentFlag) {
        String idName = getLambdaFieldName(idNameFunc);
        String parentIdName = getLambdaFieldName(parentIdNameFunc);
        // 获取到一级分类
        // 获取到一级分类
        List<T> root =
                list.stream()
                        .filter(
                                item ->
                                        String.valueOf(parentFlag)
                                                .equals(
                                                        String.valueOf(
                                                                ReflectionUtils.getFieldValue(
                                                                        item, parentIdName))))
                        .toList();
        list.removeAll(root);
        Map<String, Object> map = new HashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        root.forEach(
                item -> {
                    Map<String, Object> itemMap = BeanUtil.beanToMap(item);
                    Map<String, Object> children = getChildren(itemMap, list, idName, parentIdName);
                    itemMap.put(CHILD_NAME, children);
                    map.put(String.valueOf(index.get()), itemMap);
                    index.getAndIncrement();
                });
        return map;
    }

    /**
     * 获取树形结构
     * @param list 需要转换树的集合
     * @param idNameFunc 主键名字的lambda表达式 如：User::getUserId
     * @return {@link List<T>}
     */
    public static <T, R> Map<String, Object> buildTree(List<T> list, Function<T, R> idNameFunc) {
        String idName = getLambdaFieldName(idNameFunc);
        String parentIdName = "parentId";
        String parentFlag = "0";
        // 获取到一级分类
        // 获取到一级分类
        List<T> root =
                list.stream()
                        .filter(
                                item ->
                                        parentFlag.equals(
                                                String.valueOf(
                                                        ReflectionUtils.getFieldValue(
                                                                item, parentIdName))))
                        .toList();
        list.removeAll(root);
        Map<String, Object> map = new HashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        root.forEach(
                item -> {
                    Map<String, Object> itemMap = BeanUtil.beanToMap(item);
                    Map<String, Object> children = getChildren(itemMap, list, idName, parentIdName);
                    itemMap.put(CHILD_NAME, children);
                    map.put(String.valueOf(index.get()), itemMap);
                    index.getAndIncrement();
                });
        return map;
    }

    /**
     * 递归获取子类
     * @param list 需要转换树的集合
     * @param idName 主键id的名字 如：deptId
     * @param parentIdName 父id的名字 如：parentId
     */
    public static <T> Map<String, Object> getChildren(
            Map<String, Object> itemMap, List<T> list, String idName, String parentIdName) {
        if (hasChildren(itemMap, list, idName, parentIdName)) {
            List<T> collect =
                    list.stream()
                            .filter(
                                    item ->
                                            String.valueOf(
                                                            ReflectionUtils.getFieldValue(
                                                                    item, parentIdName))
                                                    .equals(String.valueOf(itemMap.get(idName))))
                            .toList();
            Map<String, Object> map = new HashMap<>();
            if (CollUtil.isNotEmpty(collect)) {
                itemMap.put(CHILD_NAME, collect);
                list.removeAll(collect);
                AtomicInteger index = new AtomicInteger(0);
                collect.forEach(
                        item -> {
                            Map<String, Object> childItemMap = BeanUtil.beanToMap(item);
                            Map<String, Object> children =
                                    getChildren(childItemMap, list, idName, parentIdName);
                            childItemMap.put(CHILD_NAME, children);
                            map.put(String.valueOf(index.get()), childItemMap);
                            index.getAndIncrement();
                        });
            }
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * 判断是否拥有子类
     * @param list 需要转换树形的集合
     * @param idName id的名字 如：deptId
     * @param parentIdName 父id的名字 如：parentId
     * @return {@link boolean}
     */
    public static <T> boolean hasChildren(
            Map<String, Object> itemMap, List<T> list, String idName, String parentIdName) {
        return list.stream()
                .anyMatch(
                        item -> {
                            String a =
                                    String.valueOf(
                                            ReflectionUtils.getFieldValue(item, parentIdName));
                            String b = String.valueOf(itemMap.get(idName));
                            return a.equals(b);
                        });
    }

    public static final String GET = "get";

    public static final String IS = "is";

    private static final Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE =
            new ConcurrentHashMap<>();

    private static SerializedLambda getSerializedLambda(Function<?, ?> fn) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass());
        // 先检查缓存中是否存在
        if (lambda == null) {
            try {
                // 提取SerializedLambda并缓存
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMBDA_CACHE.put(fn.getClass(), lambda);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return lambda;
    }

    /**
     * 将lambda表达式转换为属性名
     * @param fn lambda表达式
     * @return {@link String}
     */
    public static <T, R> String getLambdaFieldName(Function<T, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        if (methodName.startsWith(GET)) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith(IS)) {
            methodName = methodName.substring(2);
        } else {
            throw new IllegalArgumentException("无效的getter方法：" + methodName);
        }
        return StringUtils.firstToLowerCase(methodName);
    }

    /**
     * 构建指定根节点的上下级关联的树形结构（主键id，上级属性parentId、子节点属性children）
     * @param allNodes 所有节点对象
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes) {
        return buildTree(allNodes, -1);
    }

    /**
     * 构建指定根节点的上下级关联的树形结构（主键id，上级属性parentId、子节点属性children）
     * @param allNodes 所有节点对象
     * @param rootNodeId 跟节点ID
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId) {
        return buildTree(allNodes, rootNodeId, "id");
    }

    /**
     * 构建指定根节点的上下级关联的树形结构（主键指定，上级属性parentId、子节点属性children）
     * @param allNodes 所有节点对象
     * @param rootNodeId 根节点ID
     * @param idFieldName 主键属性名
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId, String idFieldName) {
        return buildTree(allNodes, rootNodeId, idFieldName, "parentId", "children");
    }

    /**
     * 构建指定根节点的上下级关联的树形结构（指定主键属性，上级属性、子节点属性名）
     * @param allNodes 所有节点对象
     * @param rootNodeId 根节点ID
     * @param idFieldName 主键属性名
     * @param parentIdFieldName 父节点属性名
     * @param childrenFieldName 子节点集合属性名
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(
            List<T> allNodes,
            Object rootNodeId,
            String idFieldName,
            String parentIdFieldName,
            String childrenFieldName) {
        if (CollUtil.isEmpty(allNodes)) {
            return Collections.emptyList();
        }

        Map<Object, List<T>> parentId2ListMap = new HashMap<>();
        // 提取所有的top level对象
        for (T node : allNodes) {
            Object parentId = getProperty(node, parentIdFieldName);
            Object nodeId = getProperty(node, idFieldName);
            if (Objects.equals(nodeId, parentId)) {
                throw new BusinessException(
                        "parentId关联自身，请检查！" + node.getClass().getSimpleName() + ":" + nodeId);
            }
            parentId2ListMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }

        if (parentId2ListMap.containsKey(rootNodeId)) {
            return buildTree(
                    parentId2ListMap.remove(rootNodeId),
                    parentId2ListMap,
                    idFieldName,
                    childrenFieldName);
        } else if (rootNodeId != null) {
            return Collections.emptyList();
        }

        for (Object parentId : new HashSet<>(parentId2ListMap.keySet())) {
            if (parentId2ListMap.containsKey(parentId)) {
                buildTree(
                        parentId2ListMap.get(parentId),
                        parentId2ListMap,
                        idFieldName,
                        childrenFieldName);
            }
        }

        if (parentId2ListMap.size() == 1) {
            return parentId2ListMap.values().iterator().next();
        }
        throw new BusinessException("buildTree根节点ParentId不唯一");
    }

    /**
     * 构建tree结构数据
     * @param nodes 顶级节点数据列表
     * @param parentId2ListMap 父ID与子元素列表映射
     * @param idFieldName ID属性名
     * @param childrenFieldName children属性名
     * @param <T> 节点类型
     * @return 构建Tree结构完成的数据列表
     */
    public static <T> List<T> buildTree(
            List<T> nodes,
            Map<Object, List<T>> parentId2ListMap,
            String idFieldName,
            String childrenFieldName) {
        for (T item : nodes) {
            Object id = getProperty(item, idFieldName);
            if (parentId2ListMap.containsKey(id)) {
                setProperty(
                        item,
                        childrenFieldName,
                        buildTree(
                                parentId2ListMap.remove(id),
                                parentId2ListMap,
                                idFieldName,
                                childrenFieldName));
            }
        }
        return nodes;
    }

    /**
     * 构建tree结构数据 （可不指定根节点ID，但需保证根节点ParentId唯一）
     * @param allNodes 数据列表
     * @param getId 获取ID方法
     * @param getParentId 获取ParentId方法
     * @param setChildren 设置Children方法
     * @param <T> 节点类型
     * @return Tree结构数据
     */
    public static <T> List<T> buildTree(
            List<T> allNodes,
            Function<T, Object> getId,
            Function<T, Object> getParentId,
            BiConsumer<T, List<T>> setChildren) {
        return buildTree(allNodes, null, getId, getParentId, setChildren);
    }

    /**
     * 构建tree结构数据
     * @param allNodes 数据列表
     * @param rootNodeId 顶级节点Id
     * @param getId 获取ID方法
     * @param getParentId 获取ParentId方法
     * @param setChildren 设置Children方法
     * @param <T> 节点类型
     * @return Tree结构数据
     */
    public static <T> List<T> buildTree(
            List<T> allNodes,
            Serializable rootNodeId,
            Function<T, Object> getId,
            Function<T, Object> getParentId,
            BiConsumer<T, List<T>> setChildren) {
        if (CollUtil.isEmpty(allNodes)) {
            return Collections.emptyList();
        }
        Map<Object, List<T>> parentId2ListMap = new HashMap<>();
        for (T node : allNodes) {
            Object nodeId = getId.apply(node);
            Object parentId = getParentId.apply(node);
            if (Objects.equals(nodeId, parentId)) {
                throw new BusinessException(
                        "parentId关联自身，请检查！" + node.getClass().getSimpleName() + ":" + nodeId);
            }
            parentId2ListMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }
        if (parentId2ListMap.containsKey(rootNodeId)) {
            return buildTree(
                    parentId2ListMap.remove(rootNodeId), parentId2ListMap, getId, setChildren);
        } else if (rootNodeId != null) {
            return Collections.emptyList();
        }
        for (Object parentId : new HashSet<>(parentId2ListMap.keySet())) {
            if (parentId2ListMap.containsKey(parentId)) {
                buildTree(parentId2ListMap.get(parentId), parentId2ListMap, getId, setChildren);
            }
        }
        if (parentId2ListMap.size() == 1) {
            return parentId2ListMap.values().iterator().next();
        }
        throw new BusinessException("buildTree根节点ParentId不唯一");
    }

    /**
     * 构建tree结构数据
     * @param nodes 顶级节点数据列表
     * @param parentId2ListMap 父ID与子元素列表映射
     * @param getId 获取ID方法
     * @param setChildren 设置Children方法
     * @param <T> 节点类型
     * @return 构建Tree结构完成的数据列表
     */
    public static <T> List<T> buildTree(
            List<T> nodes,
            Map<Object, List<T>> parentId2ListMap,
            Function<T, Object> getId,
            BiConsumer<T, List<T>> setChildren) {
        for (T item : nodes) {
            Object id = getId.apply(item);
            if (parentId2ListMap.containsKey(id)) {
                setChildren.accept(
                        item,
                        buildTree(
                                parentId2ListMap.remove(id), parentId2ListMap, getId, setChildren));
            }
        }
        return nodes;
    }

    /***
     * 获取对象的属性值
     * @param obj
     * @param field
     * @return
     */
    public static Object getProperty(Object obj, String field) {
        return BeanUtil.getProperty(obj, field);
    }

    /***
     * 设置属性值
     * @param obj
     * @param field
     * @param value
     */
    public static void setProperty(Object obj, String field, Object value) {
        BeanUtil.setProperty(obj, field, value);
    }

    // public static void main(String[] args) {
    // List<Dept> list = new ArrayList<>();
    // Dept dept1 = new Dept();
    // dept1.setDeptId(1);
    // dept1.setParentId(0);
    // list.add(dept1);
    // Dept dept2 = new Dept();
    // dept2.setDeptId(2);
    // dept2.setParentId(1);
    // list.add(dept2);
    // Dept dept3 = new Dept();
    // dept3.setDeptId(3);
    // dept3.setParentId(1);
    // list.add(dept3);
    // Dept dept4 = new Dept();
    // dept4.setDeptId(4);
    // dept4.setParentId(2);
    // list.add(dept4);
    // Dept dept5 = new Dept();
    // dept5.setDeptId(5);
    // dept5.setParentId(4);
    // list.add(dept5);
    //
    // List<DeptVo> tree = TreeUtils.buildTree(list, Dept::getDeptId);
    // LogUtils.info(tree);
    // }

}
