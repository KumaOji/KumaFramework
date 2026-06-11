package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.entity.SysPermission;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.vo.GrantPermissionRequest;
import com.kuma.cloud.blog.domain.vo.RevokePermissionRequest;
import com.kuma.cloud.blog.domain.vo.UserAuthoritiesVO;
import com.kuma.cloud.blog.domain.vo.UserBriefVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.PermissionService;
import com.kuma.cloud.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final UserService userService;

    @Operation(summary = "查询所有权限列表")
    @GetMapping
    @Authorize(BlogPermissions.SYSTEM_USER)
    public Result<List<SysPermission>> listAll() {
        return Result.success(permissionService.listAll());
    }

    @Operation(summary = "分页查询用户（管理界面选人，支持关键字/状态过滤）")
    @GetMapping("/users")
    @Authorize(BlogPermissions.SYSTEM_USER)
    public Result<IPage<UserBriefVO>> pageUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            PageQuery pageQuery) {
        IPage<UserBriefVO> page = userService.pageUsers(pageQuery, keyword, status).convert(u -> {
            UserBriefVO vo = new UserBriefVO();
            BeanUtils.copyProperties(u, vo);
            return vo;
        });
        return Result.success(page);
    }

    @Operation(summary = "查询用户已授予的直接权限")
    @GetMapping("/user/{userId}")
    @Authorize(BlogPermissions.SYSTEM_USER)
    public Result<List<SysPermission>> listUserPermissions(@PathVariable Long userId) {
        return Result.success(permissionService.listUserDirectPermissions(userId));
    }

    @Operation(summary = "查询用户完整生效权限（角色 / 角色权限 / 直接授权分层）")
    @GetMapping("/user/{userId}/authorities")
    @Authorize(BlogPermissions.SYSTEM_USER)
    public Result<UserAuthoritiesVO> userAuthorities(@PathVariable Long userId) {
        return Result.success(permissionService.getUserAuthorities(userId));
    }

    @Operation(summary = "给用户授权")
    @PostMapping("/grant")
    @Authorize(BlogPermissions.SYSTEM_USER)
    public Result<Void> grant(@Valid @RequestBody GrantPermissionRequest req) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getByUsername(currentUsername);
        if (currentUser == null) {
            throw new BusinessException("当前用户不存在");
        }
        permissionService.grantPermission(req.getUserId(), req.getPermissionId(), currentUser.getId());

        User targetUser = userService.getById(req.getUserId());
        if (targetUser != null) {
            permissionService.evictCache(targetUser.getUsername());
        }
        return Result.success(null);
    }

    @Operation(summary = "撤销用户权限")
    @DeleteMapping("/revoke")
    @Authorize(BlogPermissions.SYSTEM_USER)
    public Result<Void> revoke(@Valid @RequestBody RevokePermissionRequest req) {
        permissionService.revokePermission(req.getUserId(), req.getPermissionId());

        User targetUser = userService.getById(req.getUserId());
        if (targetUser != null) {
            permissionService.evictCache(targetUser.getUsername());
        }
        return Result.success(null);
    }

    @Operation(summary = "刷新用户权限缓存")
    @PostMapping("/cache/evict/{username}")
    @Authorize(BlogPermissions.SYSTEM_USER)
    public Result<Void> evictCache(@PathVariable String username) {
        permissionService.evictCache(username);
        return Result.success(null);
    }
}
