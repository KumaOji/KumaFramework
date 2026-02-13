/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.support.tuple.impl.Pair;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ClassTypeUtils;
import com.kuma.boot.common.utils.reflect.GenericArrayTypeImpl;
import com.kuma.boot.common.utils.reflect.ParameterizedTypeImpl;
import com.kuma.boot.common.utils.reflect.PrimitiveUtils;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static Collection createCollection(Type type) {
        return TypeUtils.createCollection(type, 8);
    }

    public static Map<Object, Object> createMap(Type type) {
        if (type == Properties.class) {
            return new Properties();
        }
        if (type == Hashtable.class) {
            return new Hashtable<Object, Object>();
        }
        if (type == IdentityHashMap.class) {
            return new IdentityHashMap<Object, Object>();
        }
        if (type == SortedMap.class || type == TreeMap.class) {
            return new TreeMap<Object, Object>();
        }
        if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) {
            return new ConcurrentHashMap<Object, Object>();
        }
        if (type == HashMap.class) {
            return new HashMap<Object, Object>();
        }
        if (type == LinkedHashMap.class) {
            return new LinkedHashMap<Object, Object>();
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            if (EnumMap.class.equals((Object)rawType)) {
                Type[] actualArgs = parameterizedType.getActualTypeArguments();
                return new EnumMap<Object, Object>((Class)actualArgs[0]);
            }
            return TypeUtils.createMap(rawType);
        }
        Class clazz = (Class)type;
        if (clazz.isInterface()) {
            throw new BootException("unsupport type " + String.valueOf(type));
        }
        if ("java.util.Collections$UnmodifiableMap".equals(clazz.getName())) {
            return new HashMap<Object, Object>();
        }
        try {
            return (Map)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new BootException(e);
        }
    }

    public static Pair<Type, Type> getMapKeyValueType(Type mapType) {
        if (mapType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)mapType;
            Type keyType = parameterizedType.getActualTypeArguments()[0];
            Object valueType = null;
            valueType = mapType.getClass().getName().equals("org.springframework.util.LinkedMultiValueMap") ? List.class : parameterizedType.getActualTypeArguments()[1];
            return Pair.of(keyType, valueType);
        }
        Class<Object> objectType = Object.class;
        return Pair.of(objectType, objectType);
    }

    public static Collection createCollection(Type type, int size) {
        Collection list;
        Class rawClass = TypeUtils.getRawClass(type);
        if (rawClass == AbstractCollection.class || rawClass == Collection.class) {
            list = new ArrayList(size);
        } else if (rawClass.isAssignableFrom(HashSet.class)) {
            list = new HashSet(size);
        } else if (rawClass.isAssignableFrom(LinkedHashSet.class)) {
            list = new LinkedHashSet(size);
        } else if (rawClass.isAssignableFrom(TreeSet.class)) {
            list = new TreeSet();
        } else if (rawClass.isAssignableFrom(ArrayList.class)) {
            list = new ArrayList(size);
        } else if (rawClass.isAssignableFrom(EnumSet.class)) {
            Class itemType = TypeUtils.getGenericType(type);
            list = EnumSet.noneOf(itemType);
        } else if (rawClass.isAssignableFrom(Queue.class)) {
            list = new LinkedList();
        } else {
            try {
                list = (Collection)rawClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new BootException(e);
            }
        }
        return list;
    }

    private static Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return TypeUtils.getRawClass(((ParameterizedType)type).getRawType());
        }
        throw new UnsupportedOperationException();
    }

    public static Class getGenericType(Type type) {
        Object itemType = type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class;
        return itemType;
    }

    public static Type getCollectionItemType(Type collectionType) {
        if (collectionType instanceof ParameterizedType) {
            return TypeUtils.getCollectionItemType((ParameterizedType)collectionType);
        }
        if (collectionType instanceof Class) {
            return TypeUtils.getCollectionItemType((Class)collectionType);
        }
        return Object.class;
    }

    private static Type getCollectionItemType(Class<?> clazz) {
        return clazz.getName().startsWith("java.") ? Object.class : TypeUtils.getCollectionItemType(TypeUtils.getCollectionSuperType(clazz));
    }

    private static Type getCollectionItemType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (rawType == Collection.class) {
            return TypeUtils.getWildcardTypeUpperBounds(actualTypeArguments[0]);
        }
        Class rawClass = (Class)rawType;
        Map<TypeVariable, Type> actualTypeMap = TypeUtils.createActualTypeMap(rawClass.getTypeParameters(), actualTypeArguments);
        Type superType = TypeUtils.getCollectionSuperType(rawClass);
        if (superType instanceof ParameterizedType) {
            Class<?> superClass = TypeUtils.getRawClass(superType);
            Type[] superClassTypeParameters = ((ParameterizedType)superType).getActualTypeArguments();
            return superClassTypeParameters.length > 0 ? TypeUtils.getCollectionItemType(TypeUtils.makeParameterizedType(superClass, superClassTypeParameters, actualTypeMap)) : TypeUtils.getCollectionItemType(superClass);
        }
        return TypeUtils.getCollectionItemType((Class)superType);
    }

    private static Type getWildcardTypeUpperBounds(Type type) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            return upperBounds.length > 0 ? upperBounds[0] : Object.class;
        }
        return type;
    }

    private static Map<TypeVariable, Type> createActualTypeMap(TypeVariable[] typeParameters, Type[] actualTypeArguments) {
        int length = typeParameters.length;
        HashMap<TypeVariable, Type> actualTypeMap = new HashMap<TypeVariable, Type>(length);
        for (int i = 0; i < length; ++i) {
            actualTypeMap.put(typeParameters[i], actualTypeArguments[i]);
        }
        return actualTypeMap;
    }

    private static Type getCollectionSuperType(Class<?> clazz) {
        Type assignable = null;
        for (Type type : clazz.getGenericInterfaces()) {
            Class<?> rawClass = TypeUtils.getRawClass(type);
            if (rawClass == Collection.class) {
                return type;
            }
            if (!Collection.class.isAssignableFrom(rawClass)) continue;
            assignable = type;
        }
        return assignable == null ? clazz.getGenericSuperclass() : assignable;
    }

    private static ParameterizedType makeParameterizedType(Class<?> rawClass, Type[] typeParameters, Map<TypeVariable, Type> actualTypeMap) {
        int length = typeParameters.length;
        Type[] actualTypeArguments = new Type[length];
        for (int i = 0; i < length; ++i) {
            actualTypeArguments[i] = TypeUtils.getActualType(typeParameters[i], actualTypeMap);
        }
        return new ParameterizedTypeImpl(actualTypeArguments, null, rawClass);
    }

    private static Type getActualType(Type typeParameter, Map<TypeVariable, Type> actualTypeMap) {
        if (typeParameter instanceof TypeVariable) {
            return actualTypeMap.get(typeParameter);
        }
        if (typeParameter instanceof ParameterizedType) {
            return TypeUtils.makeParameterizedType(TypeUtils.getRawClass(typeParameter), ((ParameterizedType)typeParameter).getActualTypeArguments(), actualTypeMap);
        }
        if (typeParameter instanceof GenericArrayType) {
            return new GenericArrayTypeImpl(TypeUtils.getActualType(((GenericArrayType)typeParameter).getGenericComponentType(), actualTypeMap));
        }
        return typeParameter;
    }

    public static Class getGenericParamType(Type genericType, int paramIndex) {
        if (ObjectUtils.isNull(genericType)) {
            return null;
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)genericType;
            Object[] types = parameterizedType.getActualTypeArguments();
            if (ArrayUtils.isEmpty(types)) {
                return null;
            }
            Object type = types[paramIndex];
            if (ClassTypeUtils.isWildcardGenericType((Type)type)) {
                return Object.class;
            }
            return (Class)type;
        }
        return null;
    }

    public static Class getGenericParamType(Type genericType) {
        return TypeUtils.getGenericParamType(genericType, 0);
    }

    public static <T> T cast(Object obj, Type type) {
        if (obj == null) {
            if (ClassTypeUtils.isPrimitive((Class)type)) {
                return (T)PrimitiveUtils.getDefaultValue((Class)type);
            }
            return null;
        }
        if (type instanceof ParameterizedType) {
            return TypeUtils.cast(obj, (ParameterizedType)type);
        }
        if (obj instanceof String) {
            String strVal = (String)obj;
            if (strVal.length() == 0 || "null".equals(strVal) || "NULL".equals(strVal)) {
                return null;
            }
            return (T)strVal;
        }
        return (T)obj;
    }

    private static <T> T cast(Object obj, ParameterizedType type) {
        Type argType;
        String strVal;
        Type itemType;
        Type rawTye = type.getRawType();
        if (rawTye == List.class || rawTye == ArrayList.class) {
            itemType = type.getActualTypeArguments()[0];
            if (obj instanceof List) {
                List listObj = (List)obj;
                ArrayList<T> arrayList = new ArrayList<T>(listObj.size());
                for (Object item : listObj) {
                    T itemValue = itemType instanceof Class ? TypeUtils.cast(item, (Class)itemType) : TypeUtils.cast(item, itemType);
                    arrayList.add(itemValue);
                }
                return (T)arrayList;
            }
        }
        if (rawTye == Set.class || rawTye == HashSet.class || rawTye == TreeSet.class || rawTye == Collection.class || rawTye == List.class || rawTye == ArrayList.class) {
            itemType = type.getActualTypeArguments()[0];
            if (obj instanceof Iterable) {
                AbstractCollection collection = rawTye == Set.class || rawTye == HashSet.class ? new HashSet() : (rawTye == TreeSet.class ? new TreeSet() : new ArrayList());
                for (Object item : (Iterable)obj) {
                    T itemValue = itemType instanceof Class ? TypeUtils.cast(item, (Class)itemType) : TypeUtils.cast(item, itemType);
                    collection.add(itemValue);
                }
                return (T)collection;
            }
        }
        if (rawTye == Map.class || rawTye == HashMap.class) {
            Type keyType = type.getActualTypeArguments()[0];
            Type valueType = type.getActualTypeArguments()[1];
            if (obj instanceof Map) {
                HashMap<T, T> map = new HashMap<T, T>();
                for (Map.Entry entry : ((Map)obj).entrySet()) {
                    T key = TypeUtils.cast(entry.getKey(), keyType);
                    T value = TypeUtils.cast(entry.getValue(), valueType);
                    map.put(key, value);
                }
                return (T)map;
            }
        }
        if (obj instanceof String && (strVal = (String)obj).length() == 0) {
            return null;
        }
        if (type.getActualTypeArguments().length == 1 && (argType = type.getActualTypeArguments()[0]) instanceof WildcardType) {
            return TypeUtils.cast(obj, rawTye);
        }
        if (rawTye == Map.Entry.class && obj instanceof Map && ((Map)obj).size() == 1) {
            Map.Entry entry = ((Map)obj).entrySet().iterator().next();
            return (T)entry;
        }
        throw new BootException("\u65e0\u6cd5\u8f6c\u6362\u4e3a\u7c7b\u578b\uff1a " + String.valueOf(type));
    }
}

