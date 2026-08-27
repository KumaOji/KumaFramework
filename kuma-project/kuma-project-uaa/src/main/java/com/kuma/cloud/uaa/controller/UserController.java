package com.kuma.cloud.uaa.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.uaa.domain.dto.UserSaveDTO;
import com.kuma.cloud.uaa.domain.query.UserQuery;
import com.kuma.cloud.uaa.domain.vo.UserVO;
import com.kuma.cloud.uaa.service.UaaUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UaaUserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping
    @PreAuthorize("hasAuthority('uaa:user:read')")
    public Result<IPage<UserVO>> page(UserQuery query) {
        return Result.success(userService.page(query));
    }

    @Operation(summary = "查询用户详情，含角色与生效权限")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('uaa:user:read')")
    public Result<UserVO> detail(@PathVariable Long userId) {
        return Result.success(userService.getDetail(userId));
    }

    @Operation(summary = "新建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('uaa:user:write')")
    public Result<Long> create(@Valid @RequestBody UserSaveDTO dto) {
        return Result.success(userService.create(dto));
    }

    @Operation(summary = "修改用户，password 留空表示不改密码")
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('uaa:user:write')")
    public Result<String> update(@PathVariable Long userId, @Valid @RequestBody UserSaveDTO dto) {
        userService.update(userId, dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "启用或禁用用户，禁用后其在途 JWT 仍在有效期内，需配合 Token 撤销")
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('uaa:user:write')")
    public Result<String> changeStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        userService.changeStatus(userId, enabled);
        return Result.success(enabled ? "已启用" : "已禁用");
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasAuthority('uaa:user:write')")
    public Result<String> resetPassword(@PathVariable Long userId, @RequestParam String password) {
        userService.resetPassword(userId, password);
        return Result.success("密码已重置");
    }

    @Operation(summary = "覆盖式设置用户角色")
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('uaa:user:write')")
    public Result<String> assignRoles(@PathVariable Long userId, @RequestBody List<String> roleCodes) {
        userService.assignRoles(userId, roleCodes);
        return Result.success("角色已更新");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('uaa:user:write')")
    public Result<String> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return Result.success("删除成功");
    }
}
