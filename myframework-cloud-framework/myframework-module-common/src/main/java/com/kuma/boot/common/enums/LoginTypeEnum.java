/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;
import java.util.Arrays;

public enum LoginTypeEnum implements CommonEnum
{
    B_PC_ACCOUNT(1, "b_pc_account", "b\u7aef\u7528\u6237-> \u7528\u6237+\u5bc6\u7801\u767b\u5f55"),
    C_PC_ACCOUNT(2, "c_pc_account", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u7528\u6237+\u5bc6\u7801\u767b\u5f55"),
    C_APP_ACCOUNT(3, "c_app_account", "c\u7aef\u7528\u6237\u4e4bAPP\u7aef-> \u7528\u6237+\u5bc6\u7801\u767b\u5f55"),
    B_PC_ACCOUNT_VERIFICATION(4, "b_pc_account_verification", "b\u7aef\u7528\u6237-> \u7528\u6237+\u5bc6\u7801+\u9a8c\u8bc1\u7801\u767b\u5f55"),
    C_PC_ACCOUNT_VERIFICATION(5, "c_pc_account_verification", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u7528\u6237+\u5bc6\u7801+\u9a8c\u8bc1\u7801\u767b\u5f55"),
    B_PC_PHONE(6, "b_pc_phone", "b\u7aef\u7528\u6237-> \u624b\u673a\u53f7\u7801+\u77ed\u4fe1\u767b\u5f55"),
    C_PC_PHONE(7, "c_pc_phone", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u624b\u673a\u53f7\u7801+\u77ed\u4fe1\u767b\u5f55"),
    C_MIMI_PHONE(8, "c_mimi_phone", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u624b\u673a\u53f7\u7801+\u77ed\u4fe1\u767b\u5f55"),
    C_APP_PHONE(9, "c_app_phone", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u624b\u673a\u53f7\u7801+\u77ed\u4fe1\u767b\u5f55"),
    C_APP_FACE(10, "c_app_face", "c\u7aef\u7528\u6237\u4e4bapp\u7aef-> \u9762\u90e8\u8bc6\u522b\u767b\u5f55"),
    C_PC_QR_CODE(11, "c_pc_qr_code", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u624b\u673a\u626b\u7801\u767b\u5f55"),
    C_PC_QQ(12, "c_pc_qq", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> qq\u767b\u5f55"),
    c_pc_wechat(13, "c_pc_wechat", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u5fae\u4fe1\u767b\u5f55"),
    C_PC_ALIPAY(14, "c_pc_alipay", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u652f\u4ed8\u5b9d\u767b\u5f55"),
    C_PC_GITHUB(15, "c_pc_github", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> github\u767b\u5f55"),
    C_PC_GITEE(16, "c_pc_gitee", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> gitee\u767b\u5f55"),
    C_PC_WEIBO(17, "c_pc_weibo", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> weibo\u767b\u5f55"),
    C_PC_TIKTOK(18, "c_pc_tiktok", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u6296\u97f3\u767b\u5f55"),
    C_PC_DINGDING(19, "c_pc_dingding", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> \u9489\u9489\u767b\u5f55"),
    C_PC_GITLAB(20, "c_pc_gitlab", "c\u7aef\u7528\u6237\u4e4bpc\u7aef-> gitlab\u767b\u5f55"),
    C_MIMI_ONE_CLICK(21, "c_mimi_one_click", "c\u7aef\u7528\u6237\u4e4b\u5c0f\u7a0b\u5e8f\u7aef-> \u5fae\u4fe1\u4e00\u952e\u767b\u5f55"),
    C_MP_ONE_CLICK(22, "c_mp_one_click", "c\u7aef\u7528\u6237\u4e4b\u516c\u4f17\u53f7\u7aef-> \u5fae\u4fe1\u516c\u4f17\u53f7\u767b\u5f55"),
    C_APP_ONE_CLICK(23, "c_app_one_click", "c\u7aef\u7528\u6237\u4e4bapp\u7aef-> \u672c\u673a\u53f7\u7801\u4e00\u952e\u767b\u5f55(\u4e0d\u9700\u8981\u5bc6\u7801)"),
    C_APP_FINGERPRINT(24, "c_app_fingerprint", "c\u7aef\u7528\u6237\u4e4bapp\u7aef-> \u6307\u7eb9\u767b\u5f55"),
    C_APP_FINGER(25, "c_app_finger", "c\u7aef\u7528\u6237\u4e4bapp\u7aef-> \u624b\u52bf\u767b\u5f55"),
    C_APP_QQ(26, "c_app_qq", "c\u7aef\u7528\u6237\u4e4bapp\u7aef-> qq\u767b\u5f55"),
    C_APP_WECHAT(27, "c_app_wechat", "c\u7aef\u7528\u6237\u4e4bapp\u7aef-> \u5fae\u4fe1\u767b\u5f55"),
    C_APP_ALIPAY(28, "c_app_alipay", "c\u7aef\u7528\u6237\u4e4bapp\u7aef-> \u652f\u4ed8\u5b9d\u767b\u5f55");

    private final int code;
    private final String type;
    private final String description;

    public static Boolean hasType(String type) {
        return Arrays.stream(LoginTypeEnum.values()).anyMatch(x -> x.getType().equals(type));
    }

    private LoginTypeEnum(int code, String type, String description) {
        this.code = code;
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.description;
    }
}

