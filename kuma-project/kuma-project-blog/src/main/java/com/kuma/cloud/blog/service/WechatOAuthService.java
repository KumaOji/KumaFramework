package com.kuma.cloud.blog.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.blog.config.WechatProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatOAuthService {

    private static final String WECHAT_OAUTH_URL = "https://open.weixin.qq.com/connect/qrconnect";
    private static final String WECHAT_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String WECHAT_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    private static final String STATE_PREFIX = "blog:wechat:state:";

    private final WechatProperties wechatProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /** 生成微信扫码授权 URL，state 存入 Redis 防 CSRF */
    public String buildQrUrl() {
        String state = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(STATE_PREFIX + state, "pending", 10, TimeUnit.MINUTES);

        String encodedRedirect = URLEncoder.encode(wechatProperties.getRedirectUri(), StandardCharsets.UTF_8);
        return WECHAT_OAUTH_URL
                + "?appid=" + wechatProperties.getAppId()
                + "&redirect_uri=" + encodedRedirect
                + "&response_type=code"
                + "&scope=snsapi_login"
                + "&state=" + state
                + "#wechat_redirect";
    }

    /** 校验 state 有效性（防 CSRF） */
    public void validateState(String state) {
        String key = STATE_PREFIX + state;
        Boolean exists = stringRedisTemplate.hasKey(key);
        if (!Boolean.TRUE.equals(exists)) {
            throw new BusinessException("state 无效或已过期");
        }
        stringRedisTemplate.delete(key);
    }

    /** 用 code 换取 access_token 和 openId */
    public WechatTokenResult getAccessToken(String code) {
        String url = WECHAT_TOKEN_URL
                + "?appid=" + wechatProperties.getAppId()
                + "&secret=" + wechatProperties.getAppSecret()
                + "&code=" + code
                + "&grant_type=authorization_code";

        String body = HttpUtil.get(url);
        log.debug("wechat token response: {}", body);
        JSONObject json = JSONUtil.parseObj(body);

        if (json.containsKey("errcode")) {
            throw new BusinessException("微信授权失败：" + json.getStr("errmsg"));
        }
        WechatTokenResult result = new WechatTokenResult();
        result.setAccessToken(json.getStr("access_token"));
        result.setOpenId(json.getStr("openid"));
        return result;
    }

    /** 用 access_token + openId 获取微信用户信息 */
    public WechatUserInfo getUserInfo(String accessToken, String openId) {
        String url = WECHAT_USERINFO_URL
                + "?access_token=" + accessToken
                + "&openid=" + openId
                + "&lang=zh_CN";

        String body = HttpUtil.get(url);
        log.debug("wechat userinfo response: {}", body);
        JSONObject json = JSONUtil.parseObj(body);

        if (json.containsKey("errcode")) {
            throw new BusinessException("获取微信用户信息失败：" + json.getStr("errmsg"));
        }
        WechatUserInfo info = new WechatUserInfo();
        info.setOpenId(json.getStr("openid"));
        info.setNickname(StrUtil.blankToDefault(json.getStr("nickname"), "微信用户"));
        info.setAvatar(json.getStr("headimgurl"));
        return info;
    }

    // ── 内部 VO ────────────────────────────────────────────────

    @lombok.Data
    public static class WechatTokenResult {
        private String accessToken;
        private String openId;
    }

    @lombok.Data
    public static class WechatUserInfo {
        private String openId;
        private String nickname;
        private String avatar;
    }
}
