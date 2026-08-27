package com.kuma.cloud.uaa.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.uaa.domain.dto.RoleSaveDTO;
import com.kuma.cloud.uaa.domain.vo.RoleVO;
import com.kuma.cloud.uaa.security.UaaPermissions;
import com.kuma.cloud.uaa.service.UaaRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final UaaRoleService roleService;

    @Operation(summary = "查询全部角色及其权限")
    @GetMapping
    @Authorize(UaaPermissions.ROLE_READ)
    public Result<List<RoleVO>> list() {
        return Result.success(roleService.listAll());
    }

    @Operation(summary = "查询角色详情")
    @GetMapping("/{code}")
    @Authorize(UaaPermissions.ROLE_READ)
    public Result<RoleVO> detail(@PathVariable String code) {
        return Result.success(roleService.getByCode(code));
    }

    @Operation(summary = "新建角色")
    @PostMapping
    @Authorize(UaaPermissions.ROLE_WRITE)
    public Result<Long> create(@Valid @RequestBody RoleSaveDTO dto) {
        return Result.success(roleService.create(dto));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{code}")
    @Authorize(UaaPermissions.ROLE_WRITE)
    public Result<String> update(@PathVariable String code, @Valid @RequestBody RoleSaveDTO dto) {
        roleService.update(code, dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "覆盖式设置角色权限")
    @PutMapping("/{code}/permissions")
    @Authorize(UaaPermissions.ROLE_WRITE)
    public Result<String> assignPermissions(
            @PathVariable String code, @RequestBody List<String> permissionCodes) {
        roleService.assignPermissions(code, permissionCodes);
        return Result.success("权限已更新");
    }

    @Operation(summary = "删除角色，仍被用户引用时拒绝")
    @DeleteMapping("/{code}")
    @Authorize(UaaPermissions.ROLE_WRITE)
    public Result<String> delete(@PathVariable String code) {
        roleService.delete(code);
        return Result.success("删除成功");
    }
}
