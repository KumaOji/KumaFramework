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

package com.kuma.boot.core.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AsyncProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:44:31
 */
@ConfigurationProperties(prefix = AsyncProperties.PREFIX)
public class AsyncProperties {

    public static final String PREFIX = "kuma.boot.core.async";

    private boolean virtualThreads;

    /** 寮傛鏍稿績绾跨▼鏁帮紝榛樿锛?0 */
    private int corePoolSize = 10;

    /** 寮傛鏈€澶х嚎绋嬫暟锛岄粯璁わ細50 */
    private int maxPoolSiz = 50;

    /** 闃熷垪瀹归噺锛岄粯璁わ細10000 */
    private int queueCapacity = 10000;

    /** 绾跨▼瀛樻椿鏃堕棿锛岄粯璁わ細300 */
    private int keepAliveSeconds = 300;

    /**
     * 鏄惁鍏佽鏍稿績绾跨▼瓒呮椂
     *
     * <p>榛樿锛歠alse
     */
    private boolean allowCoreThreadTimeOut = false;

    /**
     * 搴旂敤鍏抽棴鏃?鏄惁绛夊緟鏈畬鎴愪换鍔＄户缁墽琛岋紝鍐嶇户缁攢姣佸叾浠栫殑Bean
     *
     * <p>榛樿锛歵rue
     */
    private boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * 渚濊禆 {@linkplain #waitForTasksToCompleteOnShutdown} 涓簍rue
     *
     * <p>搴旂敤鍏抽棴鏃?缁х画绛夊緟鏃堕棿锛堝崟浣嶏細绉掞級
     *
     * <p>濡傛灉瓒呰繃杩欎釜鏃堕棿杩樻病鏈夐攢姣佸氨寮哄埗閿€姣侊紝浠ョ‘淇濆簲鐢ㄦ渶鍚庤兘澶熻鍏抽棴锛岃€屼笉鏄樆濉炰綇
     *
     * <p>榛樿锛?
     */
    private Integer awaitTerminationSeconds = 5;

    /** 绾跨▼姹犲墠缂€ */
    private String threadNamePrefix = "kmc-async-executor";

    private boolean checkHealth = true;

    /**
     * 鏄惁寮€鍚?ServletAsyncContext
     *
     * <p>鐢ㄤ簬闃诲鐖剁嚎绋?Servlet 鐨勫叧闂紙璋冪敤 destroy() 鏂规硶锛夛紝瀵艰嚧瀛愮嚎绋嬭幏鍙栫殑涓婁笅鏂囦负绌?榛樿锛歠alse
     */
    private boolean enableServletAsyncContext = false;

    /**
     * ServletAsyncContext闃诲瓒呮椂鏃堕暱锛堝崟浣嶏細姣锛夛紝寮傛涓婁笅鏂囨渶闀跨敓鍛藉懆鏈燂紙鏈€澶ч樆濉炵埗绾跨▼澶氫箙锛?
     *
     * <p>鍗曚釜鏂规硶鐨勯樆濉炶秴鏃舵椂闂撮渶瑕佹洿闀挎椂锛屽彲浠ヤ娇鐢?{@code
     * ServletUtils.getRequest().setAttribute(AsyncProperties.SERVLET_ASYNC_CONTEXT_TIMEOUT_MILLIS,
     * 2000)}锛屼负鍗曚釜寮傛鏂规硶璁剧疆鍗曠嫭鐨勮秴鏃舵椂闀裤€?榛樿锛?00
     */
    private Long servletAsyncContextTimeoutMillis = 600L;

    /** 鏄惁寮€鍚紓姝?*/
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnableServletAsyncContext() {
        return enableServletAsyncContext;
    }

    public void setEnableServletAsyncContext(boolean enableServletAsyncContext) {
        this.enableServletAsyncContext = enableServletAsyncContext;
    }

    public Long getServletAsyncContextTimeoutMillis() {
        return servletAsyncContextTimeoutMillis;
    }

    public void setServletAsyncContextTimeoutMillis(Long servletAsyncContextTimeoutMillis) {
        this.servletAsyncContextTimeoutMillis = servletAsyncContextTimeoutMillis;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSiz() {
        return maxPoolSiz;
    }

    public void setMaxPoolSiz(int maxPoolSiz) {
        this.maxPoolSiz = maxPoolSiz;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public boolean isCheckHealth() {
        return checkHealth;
    }

    public void setCheckHealth(boolean checkHealth) {
        this.checkHealth = checkHealth;
    }

    public boolean isAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public boolean isWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    public Integer getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(Integer awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    public boolean isVirtualThreads() {
        return virtualThreads;
    }

    public void setVirtualThreads( boolean virtualThreads ) {
        this.virtualThreads = virtualThreads;
    }
}
