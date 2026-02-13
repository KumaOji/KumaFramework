/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.bean.BeanUtil
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.lang.tree.TreeNodeConfig
 */
package com.kuma.boot.common.tree;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.tree.TreeUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
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
@SuppressWarnings("all")
public class TreeUtils
extends TreeUtil {
    public static final TreeNodeConfig DEFAULT_CONFIG = TreeNodeConfig.DEFAULT_CONFIG.setNameKey("label");
    private static final String CHILD_NAME = "children";
    public static final String GET = "get";
    public static final String IS = "is";
    private static final Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentHashMap();

    public static <T, R, M> Map<String, Object> buildTree(List<T> list, Function<T, R> idNameFunc, Function<T, R> parentIdNameFunc, M parentFlag) {
        String idName = TreeUtils.getLambdaFieldName(idNameFunc);
        String parentIdName = TreeUtils.getLambdaFieldName(parentIdNameFunc);
        List<Object> root = Collections.singletonList(list.stream().filter(item -> String.valueOf(parentFlag).equals(String.valueOf((char[]) ReflectionUtils.getFieldValue(item, parentIdName)))).toList());
        list.removeAll(root);
        HashMap<String, Object> map = new HashMap<String, Object>();
        AtomicInteger index = new AtomicInteger(0);
        root.forEach(item -> {
            Map itemMap = BeanUtil.beanToMap((Object)item, (String[])new String[0]);
            Map<String, Object> children = TreeUtils.getChildren(itemMap, list, idName, parentIdName);
            itemMap.put(CHILD_NAME, children);
            map.put(String.valueOf(index.get()), itemMap);
            index.getAndIncrement();
        });
        return map;
    }

    public static <T, R> Map<String, Object> buildTree(List<T> list, Function<T, R> idNameFunc) {
        String idName = TreeUtils.getLambdaFieldName(idNameFunc);
        String parentIdName = "parentId";
        String parentFlag = "0";
        List<Object> root = Collections.singletonList(list.stream().filter(item -> parentFlag.equals(String.valueOf((char[]) ReflectionUtils.getFieldValue(item, parentIdName)))).toList());
        list.removeAll(root);
        HashMap<String, Object> map = new HashMap<String, Object>();
        AtomicInteger index = new AtomicInteger(0);
        root.forEach(item -> {
            Map itemMap = BeanUtil.beanToMap((Object)item, (String[])new String[0]);
            Map<String, Object> children = TreeUtils.getChildren(itemMap, list, idName, parentIdName);
            itemMap.put(CHILD_NAME, children);
            map.put(String.valueOf(index.get()), itemMap);
            index.getAndIncrement();
        });
        return map;
    }

    public static <T> Map<String, Object> getChildren(Map<String, Object> itemMap, List<T> list, String idName, String parentIdName) {
        if (TreeUtils.hasChildren(itemMap, list, idName, parentIdName)) {
            List<Object> collect = Collections.singletonList(list.stream().filter(item -> String.valueOf((char[]) ReflectionUtils.getFieldValue(item, parentIdName)).equals(String.valueOf(itemMap.get(idName)))).toList());
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (CollUtil.isNotEmpty(collect)) {
                itemMap.put(CHILD_NAME, collect);
                list.removeAll(collect);
                AtomicInteger index = new AtomicInteger(0);
                collect.forEach(item -> {
                    Map childItemMap = BeanUtil.beanToMap((Object)item, (String[])new String[0]);
                    Map<String, Object> children = TreeUtils.getChildren(childItemMap, list, idName, parentIdName);
                    childItemMap.put(CHILD_NAME, children);
                    map.put(String.valueOf(index.get()), childItemMap);
                    index.getAndIncrement();
                });
            }
            return map;
        }
        return Collections.emptyMap();
    }

    public static <T> boolean hasChildren(Map<String, Object> itemMap, List<T> list, String idName, String parentIdName) {
        return list.stream().anyMatch(item -> {
            String a = String.valueOf((char[])ReflectionUtils.getFieldValue(item, parentIdName));
            String b = String.valueOf(itemMap.get(idName));
            return a.equals(b);
        });
    }

    private static SerializedLambda getSerializedLambda(Function<?, ?> fn) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass());
        if (lambda == null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace", new Class[0]);
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda)method.invoke(fn, new Object[0]);
                CLASS_LAMBDA_CACHE.put(fn.getClass(), lambda);
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return lambda;
    }

    public static <T, R> String getLambdaFieldName(Function<T, R> fn) {
        SerializedLambda lambda = TreeUtils.getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        if (methodName.startsWith(GET)) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith(IS)) {
            methodName = methodName.substring(2);
        } else {
            throw new IllegalArgumentException("\u65e0\u6548\u7684getter\u65b9\u6cd5\uff1a" + methodName);
        }
        return StringUtils.firstToLowerCase(methodName);
    }

    public static <T> List<T> buildTree(List<T> allNodes) {
        return TreeUtils.buildTree(allNodes, -1);
    }

    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId) {
        return TreeUtils.buildTree(allNodes, rootNodeId, "id");
    }

    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId, String idFieldName) {
        return TreeUtils.buildTree(allNodes, rootNodeId, idFieldName, "parentId", CHILD_NAME);
    }

    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId, String idFieldName, String parentIdFieldName, String childrenFieldName) {
        if (CollUtil.isEmpty(allNodes)) {
            return Collections.emptyList();
        }
        HashMap<Object, List<T>> parentId2ListMap = new HashMap<Object, List<T>>();
        for (T node : allNodes) {
            Object parentId = TreeUtils.getProperty(node, parentIdFieldName);
            Object nodeId = TreeUtils.getProperty(node, idFieldName);
            if (Objects.equals(nodeId, parentId)) {
                throw new BusinessException("parentId\u5173\u8054\u81ea\u8eab\uff0c\u8bf7\u68c0\u67e5\uff01" + node.getClass().getSimpleName() + ":" + String.valueOf(nodeId));
            }
            parentId2ListMap.computeIfAbsent(parentId, k -> new ArrayList()).add(node);
        }
        if (parentId2ListMap.containsKey(rootNodeId)) {
            return TreeUtils.buildTree((List)parentId2ListMap.remove(rootNodeId), parentId2ListMap, idFieldName, childrenFieldName);
        }
        if (rootNodeId != null) {
            return Collections.emptyList();
        }
        for (Object parentId : new HashSet(parentId2ListMap.keySet())) {
            if (!parentId2ListMap.containsKey(parentId)) continue;
            TreeUtils.buildTree((List)parentId2ListMap.get(parentId), parentId2ListMap, idFieldName, childrenFieldName);
        }
        if (parentId2ListMap.size() == 1) {
            return (List)parentId2ListMap.values().iterator().next();
        }
        throw new BusinessException("buildTree\u6839\u8282\u70b9ParentId\u4e0d\u552f\u4e00");
    }

    public static <T> List<T> buildTree(List<T> nodes, Map<Object, List<T>> parentId2ListMap, String idFieldName, String childrenFieldName) {
        for (T item : nodes) {
            Object id = TreeUtils.getProperty(item, idFieldName);
            if (!parentId2ListMap.containsKey(id)) continue;
            TreeUtils.setProperty(item, childrenFieldName, TreeUtils.buildTree(parentId2ListMap.remove(id), parentId2ListMap, idFieldName, childrenFieldName));
        }
        return nodes;
    }

    public static <T> List<T> buildTree(List<T> allNodes, Function<T, Object> getId, Function<T, Object> getParentId, BiConsumer<T, List<T>> setChildren) {
        return TreeUtils.buildTree(allNodes, null, getId, getParentId, setChildren);
    }

    public static <T> List<T> buildTree(List<T> allNodes, Serializable rootNodeId, Function<T, Object> getId, Function<T, Object> getParentId, BiConsumer<T, List<T>> setChildren) {
        if (CollUtil.isEmpty(allNodes)) {
            return Collections.emptyList();
        }
        HashMap<Object, List<T>> parentId2ListMap = new HashMap<Object, List<T>>();
        for (T node : allNodes) {
            Object parentId;
            Object nodeId = getId.apply(node);
            if (Objects.equals(nodeId, parentId = getParentId.apply(node))) {
                throw new BusinessException("parentId\u5173\u8054\u81ea\u8eab\uff0c\u8bf7\u68c0\u67e5\uff01" + node.getClass().getSimpleName() + ":" + String.valueOf(nodeId));
            }
            parentId2ListMap.computeIfAbsent(parentId, k -> new ArrayList()).add(node);
        }
        if (parentId2ListMap.containsKey(rootNodeId)) {
            return TreeUtils.buildTree((List)parentId2ListMap.remove(rootNodeId), parentId2ListMap, getId, setChildren);
        }
        if (rootNodeId != null) {
            return Collections.emptyList();
        }
        for (Object parentId : new HashSet(parentId2ListMap.keySet())) {
            if (!parentId2ListMap.containsKey(parentId)) continue;
            TreeUtils.buildTree((List)parentId2ListMap.get(parentId), parentId2ListMap, getId, setChildren);
        }
        if (parentId2ListMap.size() == 1) {
            return (List)parentId2ListMap.values().iterator().next();
        }
        throw new BusinessException("buildTree\u6839\u8282\u70b9ParentId\u4e0d\u552f\u4e00");
    }

    public static <T> List<T> buildTree(List<T> nodes, Map<Object, List<T>> parentId2ListMap, Function<T, Object> getId, BiConsumer<T, List<T>> setChildren) {
        for (T item : nodes) {
            Object id = getId.apply(item);
            if (!parentId2ListMap.containsKey(id)) continue;
            setChildren.accept(item, TreeUtils.buildTree(parentId2ListMap.remove(id), parentId2ListMap, getId, setChildren));
        }
        return nodes;
    }

    public static Object getProperty(Object obj, String field) {
        return BeanUtil.getProperty((Object)obj, (String)field);
    }

    public static void setProperty(Object obj, String field, Object value) {
        BeanUtil.setProperty((Object)obj, (String)field, (Object)value);
    }
}

