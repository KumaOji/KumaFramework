package com.kuma.cloud.uaa.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.uaa.domain.dto.PasswordChangeDTO;
import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.domain.vo.UserVO;
import com.kuma.cloud.uaa.service.UaaUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "当前用户")
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UaaUserService userService;

    @Operation(summary = "查询当前用户的资料、角色与生效权限")
    @GetMapping
    public Result<UserVO> me(Principal principal) {
        return Result.success(userService.getDetail(currentUser(principal).getId()));
    }

    @Operation(summary = "修改当前用户密码，成功后吊销全部令牌并销毁当前会话")
    @PostMapping("/password")
    public Result<String> changePassword(
            Principal principal,
            HttpServletRequest request,
            @Valid @RequestBody PasswordChangeDTO dto) {
        UaaUser user = currentUser(principal);
        userService.changePassword(user.getId(), dto);
        invalidateCurrentSession(request);
        return Result.success("密码修改成功，请重新登录");
    }

    private UaaUser currentUser(Principal principal) {
        if (principal == null) {
            throw new BusinessException("未登录");
        }
        return userService.requireByUsername(principal.getName());
    }

    private void invalidateCurrentSession(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
