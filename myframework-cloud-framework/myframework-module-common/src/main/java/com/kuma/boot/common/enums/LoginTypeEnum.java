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

package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;
import java.util.Arrays;

/**
 *
 * String s = "b端用户 -> 用户+密码登录 手机号码+短信登录 用户+密码+验证码登录";
 *
 * String s1 =
 * "c端用户之pc端 -> 用户+密码登录 手机扫码登录 手机号码+短信登录 第三方登录(qq登录 微信登录 支付宝登录 github/gitee/weibo/抖音/钉钉/gitlab 等等)";
 * String s2 = "c端用户之小程序 -> 微信一键登录 手机号码+短信登录"; String s4 = "c端用户之微信公众号 -> 微信公众号登录"; String
 * s3 =
 * "c端用户之app -> 短信密码登录 本机号码一键登录(不需要密码) 手机号码+短信登录 指纹登录 面部识别登录 手势登录 第三方登录(qq登录 微信登录 支付宝登录)";
 *
 * // 单端登录：PC端，APP端，小程序只能有一端登录 // 双端登录：允许其中二个端登录 // 三端登录：三个端都可以同时登录
 *
 * // 对于三端可以同时登录就很简单，但是现在有个限制，就是app端只能登录一次，不能同时登录， //
 * 也就是我一个手机登录了APP，另外一个手机登录的话，之前登录的APP端就要强制下线
 *
 * // { // userId：用户的id // clientType：PC端，小程序端，APP端 //
 * imei：就是设备的唯一编号(对于PC端这个值就是ip地址，其余的就是手机设备的一个唯一编号) // }
 **/
public enum LoginTypeEnum implements CommonEnum {

    /** 用户+密码登录 */
    B_PC_ACCOUNT(1, "b_pc_account", "b端用户-> 用户+密码登录"),
    C_PC_ACCOUNT(2, "c_pc_account", "c端用户之pc端-> 用户+密码登录"),
    C_APP_ACCOUNT(3, "c_app_account", "c端用户之APP端-> 用户+密码登录"),

    /** 用户+密码+验证码登录 */
    B_PC_ACCOUNT_VERIFICATION(4, "b_pc_account_verification", "b端用户-> 用户+密码+验证码登录"),
    C_PC_ACCOUNT_VERIFICATION(5, "c_pc_account_verification", "c端用户之pc端-> 用户+密码+验证码登录"),

    /** 手机短信登录 */
    B_PC_PHONE(6, "b_pc_phone", "b端用户-> 手机号码+短信登录"),
    C_PC_PHONE(7, "c_pc_phone", "c端用户之pc端-> 手机号码+短信登录"),
    C_MIMI_PHONE(8, "c_mimi_phone", "c端用户之pc端-> 手机号码+短信登录"),
    C_APP_PHONE(9, "c_app_phone", "c端用户之pc端-> 手机号码+短信登录"),

    /** 面部识别登录 */
    C_APP_FACE(10, "c_app_face", "c端用户之app端-> 面部识别登录"),
    /**
     * c端用户之pc端 -> 用户+密码登录 手机扫码登录 手机号码+短信登录 第三方登录(qq登录 微信登录 支付宝登录
     * github/gitee/weibo/抖音/钉钉/gitlab 等等)
     */
    C_PC_QR_CODE(11, "c_pc_qr_code", "c端用户之pc端-> 手机扫码登录"),
    C_PC_QQ(12, "c_pc_qq", "c端用户之pc端-> qq登录"),
    c_pc_wechat(13, "c_pc_wechat", "c端用户之pc端-> 微信登录"),
    C_PC_ALIPAY(14, "c_pc_alipay", "c端用户之pc端-> 支付宝登录"),
    C_PC_GITHUB(15, "c_pc_github", "c端用户之pc端-> github登录"),
    C_PC_GITEE(16, "c_pc_gitee", "c端用户之pc端-> gitee登录"),
    C_PC_WEIBO(17, "c_pc_weibo", "c端用户之pc端-> weibo登录"),
    C_PC_TIKTOK(18, "c_pc_tiktok", "c端用户之pc端-> 抖音登录"),
    C_PC_DINGDING(19, "c_pc_dingding", "c端用户之pc端-> 钉钉登录"),
    C_PC_GITLAB(20, "c_pc_gitlab", "c端用户之pc端-> gitlab登录"),
    /** 微信一键登录 */
    C_MIMI_ONE_CLICK(21, "c_mimi_one_click", "c端用户之小程序端-> 微信一键登录"),
    /** c端用户之微信公众号 -> 微信公众号登录 */
    C_MP_ONE_CLICK(22, "c_mp_one_click", "c端用户之公众号端-> 微信公众号登录"),
    /**
     * c端用户之app -> 短信密码登录 本机号码一键登录(不需要密码) 手机号码+短信登录 指纹登录 面部识别登录 手势登录 第三方登录(qq登录 微信登录
     * 支付宝登录)
     */
    C_APP_ONE_CLICK(23, "c_app_one_click", "c端用户之app端-> 本机号码一键登录(不需要密码)"),
    C_APP_FINGERPRINT(24, "c_app_fingerprint", "c端用户之app端-> 指纹登录"),
    C_APP_FINGER(25, "c_app_finger", "c端用户之app端-> 手势登录"),
    C_APP_QQ(26, "c_app_qq", "c端用户之app端-> qq登录"),
    C_APP_WECHAT(27, "c_app_wechat", "c端用户之app端-> 微信登录"),
    C_APP_ALIPAY(28, "c_app_alipay", "c端用户之app端-> 支付宝登录");

    private final int code;

    private final String type;

    private final String description;

    public static Boolean hasType(String type) {
        return Arrays.stream(values()).anyMatch(x -> x.getType().equals(type));
    }

    LoginTypeEnum(int code, String type, String description) {
        this.code = code;
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return description;
    }
}
