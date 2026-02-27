/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.support;

import com.kuma.boot.dingtalk.exception.SendMsgException;
import java.util.Map;

public interface DingerHttpClient {
    public String get(String var1) throws SendMsgException;

    public String get(String var1, Map<String, String> var2) throws SendMsgException;

    public String get(String var1, Map<String, String> var2, Map<String, ?> var3) throws SendMsgException;

    public String post(String var1, String var2) throws SendMsgException;

    public String post(String var1, Map<String, String> var2, String var3) throws SendMsgException;

    public <T> String post(String var1, Map<String, String> var2, T var3) throws SendMsgException;

    public String post(String var1, Map<String, String> var2, Map<String, ?> var3) throws SendMsgException;
}

