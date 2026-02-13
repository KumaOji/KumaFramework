/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.MDC
 */
package com.kuma.boot.common.support.mdc;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;

public class MdcAttr {
    public static final String MDC_TRACE_ID_KEY = "__traceId";
    public static final String MDC_REQUEST_ID_KEY = "__requestId";
    public static final String MDC_CLIENT_ID_KEY = "__clientId";
    public static final String MDC_SESSION_ID_KEY = "__sessionId";
    public static final String MDC_APP_CODE_KEY = "__appCode";
    public static final String MDC_SRC_APP_CODE_KEY = "__srcAppCode";
    public static final String MDC_TOKEN_KEY = "__token";
    public static final String MDC_USER_ID_KEY = "__userId";
    public static final String MDC_GROUP_ID_KEY = "__groupId";
    public static final String MDC_APP_ID_KEY = "__appId";
    public static final String MDC_APP_NAME_KEY = "__appName";
    public static final String MDC_VERSION_KEY = "__version";
    public static final String MDC_EXTRA_KEY = "__extra";
    public static final String MDC_SERVICE_ID_KEY = "__serviceId";
    public static final String MDC_SERVICE_NAME_KEY = "__serviceName";
    public static final String MDC_SERVICE_ADDR_KEY = "__serviceAddr";
    public static final String MDC_ORG_KEY = "__organization";
    public static final String MDC_OWNER_KEY = "__owner";
    public static final String MDC_MESSAGE_ID_KEY = "__messageId";
    public static final String MDC_SOURCE_REQUEST_ID_KEY = "__sourceRequestId";
    public static final String MDC_FROM_SERVICE_ID_KEY = "__fromServiceId";
    public static final String MDC_FROM_SERVICE_NAME_KEY = "__fromServiceName";
    public static final String MDC_FROM_SERVICE_ADDR_KEY = "__fromServiceAddr";
    public static final String MDC_IS_ENTRY_KEY = "__isEntry";
    public static final String[] TRANSFER_MDC_KEYS = new String[]{"__traceId", "__requestId", "__clientId", "__sessionId", "__messageId", "__sourceRequestId", "__appCode", "__srcAppCode", "__token", "__userId", "__groupId", "__appId", "__appName", "__version", "__extra"};
    public static final String[] TRANSFER_MDC_ALL_KEYS = new String[]{"__traceId", "__requestId", "__clientId", "__sessionId", "__messageId", "__sourceRequestId", "__appCode", "__srcAppCode", "__token", "__userId", "__groupId", "__appId", "__appName", "__version", "__extra", "__fromServiceId", "__fromServiceName", "__fromServiceAddr"};
    Map<String, String> ctxMap;

    public MdcAttr() {
        this.ctxMap = new HashMap<String, String>();
    }

    public MdcAttr(Map<String, String> mdcMap) {
        this.ctxMap = mdcMap;
    }

    public static MdcAttr from(Map<String, String> mdcMap) {
        return new MdcAttr(mdcMap);
    }

    public static MdcAttr fromMdc() {
        return new MdcAttr(MDC.getCopyOfContextMap() == null ? new HashMap() : MDC.getCopyOfContextMap());
    }

    public void putMdc() {
        this.ctxMap.forEach(MDC::put);
    }

    public void removeMdc() {
        this.removeMdc(true);
    }

    public void removeMdc(boolean clearMdcMap) {
        this.ctxMap.forEach((k, v) -> MDC.remove((String)k));
        if (clearMdcMap) {
            this.ctxMap.clear();
        }
    }

    public void set(String key, String val) {
        this.ctxMap.put(key, val);
    }

    public String remove(String key) {
        return this.ctxMap.remove(key);
    }

    public String get(String key) {
        return this.ctxMap.get(key);
    }

    public Map<String, String> getCtxMap() {
        return this.ctxMap;
    }

    public void setCtxMap(Map<String, String> ctxMap) {
        this.ctxMap = ctxMap;
    }

    public String getTraceId() {
        return this.get(MDC_TRACE_ID_KEY);
    }

    public void setTraceId(String traceId) {
        this.set(MDC_TRACE_ID_KEY, traceId);
    }

    public String getClientId() {
        return this.get(MDC_CLIENT_ID_KEY);
    }

    public void setClientId(String clientId) {
        this.set(MDC_CLIENT_ID_KEY, clientId);
    }

    public String getRequestId() {
        return this.get(MDC_REQUEST_ID_KEY);
    }

    public void setRequestId(String requestId) {
        this.set(MDC_REQUEST_ID_KEY, requestId);
    }

    public String getSessionId() {
        return this.get(MDC_SESSION_ID_KEY);
    }

    public void setSessionId(String sessionId) {
        this.set(MDC_SESSION_ID_KEY, sessionId);
    }

    public String getMessageId() {
        return this.get(MDC_MESSAGE_ID_KEY);
    }

    public void setMessageId(String messageId) {
        this.set(MDC_MESSAGE_ID_KEY, messageId);
    }

    public String getSourceRequestId() {
        return this.get(MDC_SOURCE_REQUEST_ID_KEY);
    }

    public void setSourceRequestId(String sourceRequestId) {
        this.set(MDC_SOURCE_REQUEST_ID_KEY, sourceRequestId);
    }

    public String getToken() {
        return this.get(MDC_TOKEN_KEY);
    }

    public void setToken(String token) {
        this.set(MDC_TOKEN_KEY, token);
    }

    public String getUserId() {
        return this.get(MDC_USER_ID_KEY);
    }

    public void setUserId(String userId) {
        this.set(MDC_USER_ID_KEY, userId);
    }

    public String getGroupId() {
        return this.get(MDC_GROUP_ID_KEY);
    }

    public void setGroupId(String groupId) {
        this.set(MDC_GROUP_ID_KEY, groupId);
    }

    public String getVersion() {
        return this.get(MDC_VERSION_KEY);
    }

    public void setVersion(String version) {
        this.set(MDC_VERSION_KEY, version);
    }

    public String getExtra() {
        return this.get(MDC_EXTRA_KEY);
    }

    public void setExtra(String extra) {
        this.set(MDC_EXTRA_KEY, extra);
    }

    public String getAppCode() {
        return this.get(MDC_APP_CODE_KEY);
    }

    public void setAppCode(String appCode) {
        this.set(MDC_APP_CODE_KEY, appCode);
    }

    public String getSrcAppCode() {
        return this.get(MDC_SRC_APP_CODE_KEY);
    }

    public void setSrcAppCode(String srcAppCode) {
        this.set(MDC_SRC_APP_CODE_KEY, srcAppCode);
    }

    public String getAppName() {
        return this.get(MDC_APP_NAME_KEY);
    }

    public void setAppName(String appName) {
        this.set(MDC_APP_NAME_KEY, appName);
    }

    public String getAppId() {
        return this.get(MDC_APP_ID_KEY);
    }

    public void setAppId(String appId) {
        this.set(MDC_APP_ID_KEY, appId);
    }

    public String getServiceId() {
        return this.get(MDC_SERVICE_ID_KEY);
    }

    public void setServiceId(String serviceId) {
        this.set(MDC_SERVICE_ID_KEY, serviceId);
    }

    public String getServiceName() {
        return this.get(MDC_SERVICE_NAME_KEY);
    }

    public void setServiceName(String serviceName) {
        this.set(MDC_SERVICE_NAME_KEY, serviceName);
    }

    public String getServiceAddr() {
        return this.get(MDC_SERVICE_ADDR_KEY);
    }

    public void setServiceAddr(String serviceAddr) {
        this.set(MDC_SERVICE_ADDR_KEY, serviceAddr);
    }

    public String getFromServiceId() {
        return this.get(MDC_FROM_SERVICE_ID_KEY);
    }

    public void setFromServiceId(String fromServiceId) {
        this.set(MDC_FROM_SERVICE_ID_KEY, fromServiceId);
    }

    public String getFromServiceName() {
        return this.get(MDC_FROM_SERVICE_NAME_KEY);
    }

    public void setFromServiceName(String fromServiceName) {
        this.set(MDC_FROM_SERVICE_NAME_KEY, fromServiceName);
    }

    public String getFromServiceAddr() {
        return this.get(MDC_FROM_SERVICE_ADDR_KEY);
    }

    public void setFromServiceAddr(String fromServiceAddr) {
        this.set(MDC_FROM_SERVICE_ADDR_KEY, fromServiceAddr);
    }

    public String getOrganization() {
        return this.get(MDC_ORG_KEY);
    }

    public void setOrganization(String orgName) {
        this.set(MDC_ORG_KEY, orgName);
    }

    public String getOwner() {
        return this.get(MDC_OWNER_KEY);
    }

    public void setOwner(String ownerName) {
        this.set(MDC_OWNER_KEY, ownerName);
    }

    public String getIsEntry() {
        return this.get(MDC_IS_ENTRY_KEY);
    }

    public void setIsEntry(String isEntry) {
        this.set(MDC_IS_ENTRY_KEY, isEntry);
    }
}

