package com.kuma.cloud.uaa.controller;

import com.kuma.cloud.uaa.config.UaaProperties;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 登录页渲染。协议端点由 Spring Authorization Server 提供，这里只负责人机交互界面。
 *
 * @author kuma
 */
@Hidden
@Controller
@RequiredArgsConstructor
public class LoginViewController {

    private final UaaProperties properties;

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        model.addAttribute("captchaEnabled", properties.getCaptcha().isEnabled());
        model.addAttribute("errorMessage", resolveErrorMessage(error));
        model.addAttribute("logoutMessage", logout == null ? null : "已安全退出");
        return "login";
    }

    private String resolveErrorMessage(String error) {
        if (error == null) {
            return null;
        }
        return switch (error) {
            case "captcha" -> "图形验证码不正确或已过期";
            case "mfa" -> "动态码不正确";
            case "locked" -> "连续登录失败次数过多，账号已临时锁定";
            case "disabled" -> "账号已被禁用";
            default -> "用户名或密码错误";
        };
    }
}
