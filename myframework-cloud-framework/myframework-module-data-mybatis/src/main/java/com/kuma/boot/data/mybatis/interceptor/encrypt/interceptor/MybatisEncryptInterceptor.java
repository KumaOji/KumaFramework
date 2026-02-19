/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.interceptor.encrypt.interceptor;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.interceptor.encrypt.annotation.EncryptField;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.EncryptContext;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.EncryptorManager;
import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.AlgorithmType;
import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.EncodeType;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisInterceptorProperties;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;

/** 入参加密拦截器 */
@Intercepts({
        @Signature(
                type = ParameterHandler.class,
                method = "setParameters",
                args = {PreparedStatement.class})
})
public class MybatisEncryptInterceptor implements Interceptor {

    private final EncryptorManager encryptorManager;
    private final MybatisInterceptorProperties.FieldEncrypt defaultProperties;

    public MybatisEncryptInterceptor(
            EncryptorManager encryptorManager,
            MybatisInterceptorProperties.FieldEncrypt defaultProperties) {
        this.encryptorManager = encryptorManager;
        this.defaultProperties = defaultProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ParameterHandler parameterHandler) {
            // 进行加密操作
            Object parameterObject = parameterHandler.getParameterObject();
            if (ObjUtil.isNotNull(parameterObject) && !(parameterObject instanceof String)) {
                this.encryptHandler(parameterObject);
            }
        }
        return target;
    }

    /**
     * 加密对象
     *
     * @param sourceObject 待加密对象
     */
    private void encryptHandler(Object sourceObject) {
        if (ObjUtil.isNull(sourceObject)) {
            return;
        }
        if (sourceObject instanceof Map<?, ?>) {
            new HashSet<>(((Map<?, ?>) sourceObject).values()).forEach(this::encryptHandler);
            return;
        }
        if (sourceObject instanceof List<?> sourceList) {
            if (CollUtil.isEmpty(sourceList)) {
                return;
            }
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            Object firstItem = sourceList.get(0);
            if (CollUtil.isEmpty(encryptorManager.getFieldCache(firstItem.getClass()))) {
                return;
            }
            ((List<?>) sourceObject).forEach(this::encryptHandler);
            return;
        }
        Set<Field> fields = encryptorManager.getFieldCache(sourceObject.getClass());
        try {
            for (Field field : fields) {
                field.set(
                        sourceObject,
                        this.encryptField(String.valueOf(field.get(sourceObject)), field));
            }
        } catch (Exception e) {
            LogUtils.error("处理加密字段时出错", e);
        }
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    private String encryptField(String value, Field field) {
        EncryptField encryptField = field.getAnnotation(EncryptField.class);
        EncryptContext encryptContext = new EncryptContext();
        encryptContext.setAlgorithm(
                encryptField.algorithm() == AlgorithmType.DEFAULT
                        ? defaultProperties.getAlgorithm()
                        : encryptField.algorithm());
        encryptContext.setEncode(
                encryptField.encode() == EncodeType.DEFAULT
                        ? defaultProperties.getEncode()
                        : encryptField.encode());
        encryptContext.setPassword(
                StringUtils.isBlank(encryptField.password())
                        ? defaultProperties.getPassword()
                        : encryptField.password());
        encryptContext.setPrivateKey(
                StringUtils.isBlank(encryptField.privateKey())
                        ? defaultProperties.getPrivateKey()
                        : encryptField.privateKey());
        encryptContext.setPublicKey(
                StringUtils.isBlank(encryptField.publicKey())
                        ? defaultProperties.getPublicKey()
                        : encryptField.publicKey());
        return this.encryptorManager.encrypt(value, encryptContext);
    }

    @Override
    public void setProperties(Properties properties) {}
}
