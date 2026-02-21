package com.kuma.boot.web.support.encryption.api.core;


import com.kuma.boot.web.support.encryption.api.dto.req.CommonDecryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonEncryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonDecryptResponse;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonEncryptResponse;

/**
 * 加解密接口
 *
 * @author binbin.hou
 * @since 1.0.0
 */
public interface Encryption {

    /**
     * 加密
     *
     * @param request 请求
     * @param context 上下文
     * @return 结果
     */
    CommonEncryptResponse encrypt(final CommonEncryptRequest request,
                                  final EncryptionContext context);

    /**
     * 解密
     *
     * @param request 请求
     * @param context 上下文
     * @return 结果
     */
    CommonDecryptResponse decrypt(final CommonDecryptRequest request,
                                  final EncryptionContext context);

}
