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

package com.kuma.boot.common.constant;

import com.kuma.boot.common.support.version.KmcVersion;

import java.time.format.DateTimeFormatter;

/**
 * 全局公共常量
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:35:37
 */
public final class CommonConstants {

    private CommonConstants() {}

    public static String REQUEST_ERROR = "请求错误";

    public static String REQUEST_SUCCESS = "请求成功";

    public static String REQUEST_FAILED = "请求失败";

    public static String SPRING_APP_NAME_KEY = "spring.application.name";

    public static String ACTIVE_PROFILES_PROPERTY = "spring.profiles.active";

    public static String ACTIVE_PROFILES_ACTIVE = "ACTIVE_PROFILES_ACTIVE";

    public static String ENV = "env";

    public static String DEFAULT_KMC_VERSION = KmcVersion.getVersion();
    public static String DEFAULT_SYSTEM_CODE = "000000";
    public static String DEFAULT_CATEGORY_CODE = "000";

    public static String KMC_ENV = "kmc_env";

    public static String KMC_MAIN = "kmc_main";

    public static String KMC_ENV_VERSION = "kmcVersion";

    public static String ACTIVE_ON_PROFILE_PROPERTY = "spring.config.activate.on-profile";

    public static String LOGGING_FILE_TOTAL_SIZE = "logging.file.total-size";

    public static String CONTEXT_RESTART_TEXT = "kuma.boot.core.context.restart.text";

    /**
     * utf-8
     */
    public static final String UTF8 = "UTF-8";

    /**
     * utf-16
     */
    public static final String UTF16 = "UTF-16";

    /**
     * gbk
     */
    public static final String GBK = "GBK";

    /**
     * 逗号
     */
    public static final char COMMA = ',';

    /**
     * 冒号
     */
    public static final char COLON = ':';

    /**
     * 符号：感叹号
     */
    public static final String EXCLAMATORY_MARK = "!";

    /**
     * char 小数点
     */
    public static final char DOT = '.';

    /**
     * char 双引号
     */
    public static final char DOUBLE_QUOTES = '"';

    /**
     * char 反斜杠
     */
    public static final char BACK_SLASH = '\\';

    /**
     * char 空格
     */
    public static final char BLANK = ' ';

    /**
     * 星星
     */
    public static final char STAR = '*';

    /**
     * String 类型的空值
     */
    public static final String STRING_NULL = "null";

    /**
     * 未知的
     */
    public static final String UNKNOWN = "unknown";

    /**
     * 文件分隔符
     */
    public static final String PATH_SPLIT = "/";

    public static final String DASH = "-";

    public static final boolean SUCCESS = true;

    public static final boolean ERROR = false;

    public static final String TIME_ZONE_GMT8 = "GMT+8";

    public static final String BEARER_TYPE = "bearea";

    public static final String KUMA = "kuma";

    public static final String KMC = "kmc";

    public static final String CLOUD = "cloud";

    public static final String DEPENDENCIES = "dependencies";

    public static final String SPRING = "spring";

    public static final String BOOT = "boot";

    public static final String ALIBABA = "alibaba";

    public static final String UNDER = "_";

    /**
     * banner 配置
     */
    public static final String VERSION = "version";

    public static final String KMC_VERSION =
            KMC.toUpperCase() + UNDER + VERSION.toUpperCase() + COLON;

    public static final String SPRING_VERSION = "springVersion";

    public static final String KMC_SPRING_VERSION =
            SPRING.toUpperCase() + UNDER + VERSION.toUpperCase() + COLON;

    public static final String SPRING_BOOT_VERSION = "springBootVersion";

    public static final String KMC_SPRING_BOOT_VERSION =
            SPRING.toUpperCase()
                    + UNDER
                    + BOOT.toUpperCase()
                    + UNDER
                    + VERSION.toUpperCase()
                    + COLON;

    public static final String SPRING_CLOUD_VERSION = "springCloudVersion";

    public static final String KMC_SPRING_CLOUD_VERSION =
            SPRING.toUpperCase()
                    + UNDER
                    + CLOUD.toUpperCase()
                    + UNDER
                    + VERSION.toUpperCase()
                    + COLON;

    public static final String SPRING_CLOUD_DEPENDENCIES_VERSION = "springCloudDependenciesVersion";

    public static final String KMC_SPRING_CLOUD_DEPENDENCIES_VERSION =
            SPRING.toUpperCase()
                    + UNDER
                    + CLOUD.toUpperCase()
                    + UNDER
                    + DEPENDENCIES.toUpperCase()
                    + UNDER
                    + VERSION.toUpperCase()
                    + COLON;

    public static final String SPRING_CLOUD_ALIBABA_VERSION = "springCloudAlibabaVersion";

    public static final String KMC_SPRING_CLOUD_ALIBABA_VERSION =
            SPRING.toUpperCase()
                    + UNDER
                    + CLOUD.toUpperCase()
                    + UNDER
                    + ALIBABA.toUpperCase()
                    + UNDER
                    + VERSION.toUpperCase()
                    + COLON;

    public static final String KMC_BANNER_DEFAULT_RESOURCE_LOCATION = "/banner/banner.txt";

    public static final String KMC_BANNER_DEFAULT = "WELCOME TO KUMA CLOUD";

    public static final String KMC_BANNER_GITHUB = "KMC_GITHUB:";

    public static final String KMC_BANNER_GITHUB_URL =
            "https://github.com/kuma/kuma-cloud-project";

    public static final String KMC_BANNER_GITEE = "KMC_GITEE:";

    public static final String KMC_BANNER_GITEE_URL =
            "https://gitee.com/dtbox/kuma-cloud-project";

    public static final String KMC_BANNER_BLOG = "KMC_BLOG:";

    public static final String KMC_BANNER_BLOG_URL = "https://blog.kumacloud.top";

    public static final String KMC_BANNER_DATAV = "KMC_DATAV:";

    public static final String KMC_BANNER_DATAV_URL = "https://datav.kumacloud.top";

    public static final String KMC_BANNER_BACKEND = "KMC_BACKEND:";

    public static final String KMC_BANNER_BACKEND_URL = "https://backend.kumacloud.top";

    public static final String KMC_BANNER_MANAGER = "KMC_MANAGER:";

    public static final String KMC_BANNER_MANAGER_URL = "https://manager.kumacloud.top";

    public static final String KMC_BANNER_MERCHANT = "KMC_MERCHANT:";

    public static final String KMC_BANNER_MERCHANT_URL = "https://merchant.kumacloud.top";

    public static final String KMC_BANNER_OPEN = "KMC_OPEN:";

    public static final String KMC_BANNER_OPEN_URL = "https://open.kumacloud.top";

    public static final String KMC_BANNER_M = "KMC_M:";

    public static final String KMC_BANNER_M_URL = "https://m.kumacloud.top";

    public static final String KMC_BANNER = "KMC:";

    public static final String KMC_BANNER_URL = "https://kumacloud.top";

    /**
     * 请求头 配置
     */
    public static final String KMC_FROM_INNER = "kmc-from-inner";

    public static final String KMC_TENANT_ID_DEFAULT = "1";

    public static final String KMC_TENANT_ID = "kmc-tenant-id";

    public static final String KMC_TRACE_ID = "kmc-trace-id";

    public static final String KMC_REQUEST_VERSION = "kmc-request-version";

    public static final String KMC_USER_HEADER = "kmc-user-header";

    public static final String KMC_USER_NAME_HEADER = "kmc-user-name-header";

    public static final String KMC_USER_ID_HEADER = "kmc-user-id-header";

    public static final String KMC_USER_ROLE_HEADER = "kmc-user-role-header";

    public static final String KMC_VERSION_HEADER = "kmc-user-role-header";

    public static final String OTLP_TRACE_ID = "X-B3-TraceId";

    public static final String TRACE_ID = "traceId";

    public static final String OTLP_SPANE_ID = "X-B3-SpanId";

    public static final String SPANE_ID = "spanId";

    /**
     * 公共日期格式 配置
     */
    public static final String MONTH_FORMAT = "yyyy-MM";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String SIMPLE_MONTH_FORMAT = "yyyyMM";

    public static final String SIMPLE_DATE_FORMAT = "yyyyMMdd";

    public static final String SIMPLE_DATETIME_FORMAT = "yyyyMMddHHmmss";

    public static final String DEF_USER_PASSWORD = "123456";

    public static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public static final String YEAR_MONTH_FORMATTER = "yyyy-MM";

    public static final String DATE_FORMATTER = "yyyy-MM-dd";

    public static final String TIME_FORMATTER = "HH:mm:ss";

    public static final String YEAR_MONTH_FORMATTER_SHORT = "yyyyMM";

    public static final String DATE_FORMATTER_SHORT = "yyyyMMdd";

    public static final String DATETIME_FORMATTER_SHORT = "yyyyMMddHHmmss";

    public static final String TIME_FORMATTER_SHORT = "HHmmss";

    /**
     * 操作日志类型 配置
     */
    public static final int OPERATE_TYPE_GET = 1;

    public static final int OPERATE_TYPE_SAVE = 2;

    public static final int OPERATE_TYPE_UPDATE = 3;

    public static final int OPERATE_TYPE_DELETE = 4;

    /**
     * 请求方式 配置
     */
    public static final String GET = "GET";

    public static final String HEAD = "HEAD";

    public static final String POST = "POST";

    public static final String PUT = "PUT";

    public static final String PATCH = "PATCH";

    public static final String DELETE = "DELETE";

    public static final String OPTIONS = "OPTIONS";

    public static final String TRACE = "TRACE";

    /**
     * 用户类型 配置
     */
    public static final String MEMBER_USER = "member";

    public static final String BACKEND_USER = "backend";

    /**
     * 登录类型 配置
     */
    public static final String PHONE_LOGIN = "phone";

    public static final String QR_LOGIN = "qr";

    public static final String PASSWORD_LOGIN = "password";

    /**
     * 资源 配置
     */
    public static final Long RESOURCE_EXPIRE = 18000L;

    /**
     * token信息 配置
     */
    public static final String KMC_TOKEN_HEADER = "Authorization";

    public static final String BEARER = "Bearer";

    public static final String ACCESS_TOKEN = "access_token";

    public static final String GRANT_TYPE = "grant_type";

    public static final String USER_TYPE = "user_type";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String OAUTH_LOGIN = "/oauth/login";

    public static final String PHONE = "phone";

    public static final String VERIFY_CODE = "verify_code";

    public static final String QR_CODE = "qr_code";

    /**
     * 邮箱
     */
    public static final String RESET_MAIL = "MAIL";

    /**
     * 权重key
     */
    public static final String WEIGHT_KEY = "weight";

    /**
     * 删除
     */
    public static final String STATUS_DEL = "1";

    /**
     * 正常
     */
    public static final String STATUS_NORMAL = "0";

    /**
     * 锁定
     */
    public static final String STATUS_LOCK = "9";

    /**
     * 目录
     */
    public static final Integer CATALOG = -1;

    /**
     * 菜单
     */
    public static final Integer MENU = 1;

    /**
     * 权限
     */
    public static final Integer PERMISSION = 2;

    /**
     * 删除标记
     */
    public static final String DEL_FLAG = "is_del";

    /**
     * 超级管理员用户名
     */
    public static final String ADMIN_USER_NAME = "admin";

    /**
     * 注册中心元数据 版本号
     */
    public static final String METADATA_VERSION = "version";

    /**
     * 邮箱
     */
    public static final Long MENU_TREE_ROOT_ID = 0L;

    // 免费图床
    public static final String SM_MS_URL = "https://sm.ms/api";

    // IP归属地查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";

    public static final String LOWERCASE_DEFAULT = "default";

    public static final int KB = 1024;

    public static final int MB = KB * 1024;

    public static final int GB = MB * 1024;

}
