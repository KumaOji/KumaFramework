package com.kuma.boot.session.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 分布式 Session 配置
 *
 * <pre>
 * kuma:
 *   boot:
 *     session:
 *       enabled: true
 *       max-sessions-per-user: -1          # <=0 不限并发登录；> 0 启用 Spring Security 并发 Session 控制
 *       cookie:
 *         name: SESSION
 *         http-only: true
 *         secure: false
 *         same-site: Lax                   # None / Lax / Strict
 *         path: /
 *         domain:                          # 不填则不设置
 *
 * # Redis / Session 超时等标准配置请继续使用 spring.session.* 和 spring.data.redis.*
 * spring:
 *   session:
 *     store-type: redis
 *     timeout: 1800
 *     redis:
 *       namespace: spring:session
 *       flush-mode: on-save               # on-save / immediate
 *       save-mode: on-set-attribute        # always / on-set-attribute / on-get-attribute
 *       repository-type: default           # default（非索引）/ indexed（支持按用户名查 Session）
 * </pre>
 */
@RefreshScope
@ConfigurationProperties(prefix = SessionProperties.PREFIX)
public class SessionProperties {

    public static final String PREFIX = "kuma.boot.session";

    private boolean enabled = false;

    /**
     * 并发登录控制（需 Spring Security + indexed repository）：
     * <=0 不限制；> 0 为同一用户最多允许的在线 Session 数。
     */
    private int maxSessionsPerUser = -1;

    /** Session Cookie 属性 */
    private Cookie cookie = new Cookie();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxSessionsPerUser() {
        return maxSessionsPerUser;
    }

    public void setMaxSessionsPerUser(int maxSessionsPerUser) {
        this.maxSessionsPerUser = maxSessionsPerUser;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    /** Cookie 属性 */
    public static class Cookie {

        /** Cookie 名称，默认 SESSION */
        private String name = "SESSION";

        /** 禁止 JavaScript 通过 document.cookie 访问 */
        private boolean httpOnly = true;

        /** 仅通过 HTTPS 传输（生产环境建议 true） */
        private boolean secure = false;

        /**
         * SameSite 属性：None / Lax / Strict。
         * 跨站请求时 Lax 允许顶层导航携带 Cookie，None 需搭配 secure=true。
         */
        private String sameSite = "Lax";

        /** Cookie 有效路径 */
        private String path = "/";

        /** Cookie 绑定域名，留空则不设置（沿用浏览器默认） */
        private String domain;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isHttpOnly() {
            return httpOnly;
        }

        public void setHttpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
        }

        public boolean isSecure() {
            return secure;
        }

        public void setSecure(boolean secure) {
            this.secure = secure;
        }

        public String getSameSite() {
            return sameSite;
        }

        public void setSameSite(String sameSite) {
            this.sameSite = sameSite;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }
}
