/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 */
package com.kuma.boot.web.support.template;

import com.kuma.boot.common.utils.json.JacksonUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HtmlHelper
extends SimpleTemplateProvider {
    public String substring3(String str, int maxlen) {
        return this.cutString(str, maxlen);
    }

    public int totalPageNum(int totalRecord, int pageSize) {
        return (totalRecord + pageSize - 1) / pageSize + 1;
    }

    public String help(String str) {
        return String.format("<img class='texthelp' width=\"20\" height=\"20\" title=\"%s\" style=\"\" src=\"/content/images/help.png\">", str);
    }

    public Boolean isnull(Object o) {
        return o == null;
    }

    public Object empty(Object o) {
        if (o == null || o.equals("null")) {
            return "";
        }
        return o;
    }

    public Object defaultValue(Object o, Object defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        return o;
    }

    public Object w2(String condition, Object data1, Object data2, Object trueObj, Object falseObj) {
        if ("==".equals(condition)) {
            if (data1 == data2) {
                return trueObj;
            }
            if (data1 != null && data1.equals(data2)) {
                return trueObj;
            }
            return falseObj;
        }
        if (">".equals(condition)) {
            if (((Number)data1).doubleValue() > ((Number)data2).doubleValue()) {
                return trueObj;
            }
            return falseObj;
        }
        if ("<".equals(condition)) {
            if (((Number)data1).doubleValue() < ((Number)data2).doubleValue()) {
                return trueObj;
            }
            return falseObj;
        }
        throw new RuntimeException("\u6761\u4ef6\u4e0d\u7b26\u5408\u89c4\u8303");
    }

    public String enumDesc(String enumClass, Object value) {
        try {
            for (Object item : Class.forName(enumClass).getEnumConstants()) {
                if (item != value && !item.getClass().getField("Value").get(item).toString().equals(value.toString())) continue;
                return item.getClass().getField("Desc").get(item).toString();
            }
            return "";
        }
        catch (Exception e) {
            return "";
        }
    }

    public List<Object> enums(String enumClass) {
        try {
            ArrayList<Object> objs = new ArrayList<Object>();
            objs.addAll(Arrays.asList(Class.forName(enumClass).getEnumConstants()));
            return objs;
        }
        catch (Exception e) {
            return new ArrayList<Object>();
        }
    }

    public Object filed(Object o, String filedName) {
        try {
            Field f = o.getClass().getField(filedName);
            f.setAccessible(true);
            return f.get(o);
        }
        catch (Exception e) {
            return "";
        }
    }

    public Object filed2(Object o, String filedName) {
        try {
            String[] fs = filedName.split("\\.");
            Object d = o;
            for (String f : fs) {
                if (d == null) {
                    return "";
                }
                Field v = d.getClass().getDeclaredField(f);
                v.setAccessible(true);
                d = v.get(d);
            }
            return d;
        }
        catch (Exception e) {
            return "";
        }
    }

    public String toJson(Object o) {
        return JacksonUtils.toJSONString((Object)o);
    }
}

