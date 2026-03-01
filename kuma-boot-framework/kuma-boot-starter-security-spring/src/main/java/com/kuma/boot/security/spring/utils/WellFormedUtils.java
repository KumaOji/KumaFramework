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

package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.constants.DefaultConstants;
import com.kuma.boot.security.spring.constants.SymbolConstants;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;

import static org.apache.commons.lang3.StringUtils.endsWith;

/**
 * <p>符合语法规则的工具类
 * <p>
 * 主要包含增强代码健壮性和目前将无法形成工具箱的、不方便归类的工具方法，统一放在该类中。待某一类方法比较多了之后，再行拆分。
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:09:32
 */
public class WellFormedUtils {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(WellFormedUtils.class);

    /**
     * 映射类型引用
     */
    public static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {};

    /**
     * 符合语法规则的 URL
     * <p>
     * 检测地址相关字符串是否以"/"结尾，如果没有就帮助增加一个 ""/""
     *
     * @param url http 请求地址字符串
     * @return {@link String }
     * @since 2023-07-04 10:09:32
     */
    public static String url(String url) {
        if (StringUtils.endWith(url, SymbolConstants.FORWARD_SLASH)) {
            return url;
        } else {
            return url + SymbolConstants.FORWARD_SLASH;
        }
    }

    /**
     * 符合语法规则的 ParentId
     * <p>
     * 树形结构 ParentId 健壮性校验方法。
     *
     * @param parentId 父节点ID
     * @return {@link String }
     * @since 2023-07-04 10:09:32
     */
    public static String parentId(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return DefaultConstants.TREE_ROOT_ID;
        } else {
            return parentId;
        }
    }

    /**
     * 将IP地址加端口号，转换为http地址。
     *
     * @param address             ip地址加端口号，格式：ip:port
     * @param protocol            http协议类型 {@link Protocol}
     * @param endWithForwardSlash 是否在结尾添加“/”
     * @return http格式地址
     */
    //	public static String addressToUri(String address, Protocol protocol, boolean
    // endWithForwardSlash) {
    //		StringBuilder stringBuilder = new StringBuilder();
    //
    //		if (!StringUtils.startsWith(address, protocol.getFormat())) {
    //			stringBuilder.append(protocol.getFormat());
    //		}
    //
    //		if (endWithForwardSlash) {
    //			stringBuilder.append(url(address));
    //		} else {
    //			stringBuilder.append(address);
    //		}
    //
    //		return stringBuilder.toString();
    //	}

    /**
     * 将IP地址加端口号，转换为http地址。
     *
     * @param address             ip地址加端口号，格式：ip:port
     * @param endWithForwardSlash 是否在结尾添加“/”
     * @return http格式地址
     */
    //	public static String addressToUri(String address, boolean endWithForwardSlash) {
    //		return addressToUri(address, Protocol.HTTP, endWithForwardSlash);
    //	}

    /**
     * 将IP地址加端口号，转换为http地址。
     *
     * @param address ip地址加端口号，格式：ip:port
     * @return http格式地址
     */
    //	public static String addressToUri(String address) {
    //		return addressToUri(address, false);
    //	}

    /**
     * 获取运行主机ip地址
     *
     * @return {@link String }
     * @since 2023-07-04 10:09:32
     */
    public static String getHostAddress() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Get host address error: {}", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 服务uri
     *
     * @param gatewayServiceUri 网关服务uri
     * @param serviceUri        服务uri
     * @param serviceName       服务名称
     * @param abbreviation      缩写
     * @return {@link String }
     * @since 2023-07-04 10:09:32
     */
    public static String serviceUri(
            String gatewayServiceUri, String serviceUri, String serviceName, String abbreviation) {
        if (StringUtils.isNotBlank(serviceUri)) {
            return serviceUri;
        } else {
            if (StringUtils.isBlank(serviceName)) {
                log.error(
                        "Property [{} Service Name] is not set or property format is incorrect!",
                        abbreviation);
                //				throw new PropertyValueIsNotSetException();
                return null;
            } else {
                return WellFormedUtils.url(gatewayServiceUri) + serviceName;
            }
        }
    }
}
