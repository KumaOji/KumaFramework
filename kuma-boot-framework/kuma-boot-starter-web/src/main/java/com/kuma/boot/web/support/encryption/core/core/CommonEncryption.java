package com.kuma.boot.web.support.encryption.core.core;


import com.kuma.boot.common.support.secret.core.HexUtil;
import com.kuma.boot.common.support.secret.core.SecretBs;
import com.kuma.boot.web.support.encryption.api.core.EncryptMaskContext;
import com.kuma.boot.web.support.encryption.api.core.EncryptMask;
import com.kuma.boot.web.support.encryption.api.core.Encryption;
import com.kuma.boot.web.support.encryption.api.core.EncryptionContext;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonDecryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonEncryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonDecryptResponse;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonEncryptResponse;

/**
 * 通用策略
 *
 * @author binbin.hou
 * @since 1.2.0
 */
public class CommonEncryption implements Encryption {

    protected String getMask( CommonEncryptRequest request) {
        EncryptMaskContext context = new EncryptMaskContext();
        context.setPlainText(request.getText());

        final EncryptMask encryptMask = request.getEncryptMask();
        return encryptMask.mask(context);
    }

    @Override
    public CommonEncryptResponse encrypt( CommonEncryptRequest request, EncryptionContext context) {
        String plain = request.getText();

        //1. 加密
        String cipher = context.secretBs().encryptToHexString(plain);

        //2. 摘要
        String hash = context.hashBs().execute(plain);

        //3. 掩码
        String mask = getMask(request);

        CommonEncryptResponse response = new CommonEncryptResponse();
        response.setMask(mask);
        response.setCipher(cipher);
        response.setHash(hash);

        return response;
    }

    @Override
    public CommonDecryptResponse decrypt( CommonDecryptRequest request, EncryptionContext context) {
        // 初始化一次
        SecretBs secretBs = context.secretBs();

        // https://www.jianshu.com/p/0d6b661b84dd
        byte[] bytes = HexUtil.hexStringToByte(request.getCipher());
        String plain = secretBs.decryptToString(bytes);

        CommonDecryptResponse response = new CommonDecryptResponse();
        response.setText(plain);
        return response;
    }

}
