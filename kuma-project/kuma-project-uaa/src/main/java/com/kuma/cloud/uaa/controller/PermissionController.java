package com.kuma.cloud.uaa.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.uaa.domain.vo.PermissionVO;
import com.kuma.cloud.uaa.service.UaaPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final UaaPermissionService permissionService;

    @Operation(summary = "查询全部权限字典")
    @GetMapping
    @PreAuthorize("hasAuthority('uaa:permission:read')")
    public Result<List<PermissionVO>> list() {
        return Result.success(permissionService.listAll());
    }

    @Operation(summary = "新增权限")
    @PostMapping
    @PreAuthorize("hasAuthority('uaa:permission:write')")
    public Result<Long> create(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String resource) {
        return Result.success(permissionService.create(code, name, resource));
    }

    @Operation(summary = "删除权限，同时解除与角色的绑定")
    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('uaa:permission:write')")
    public Result<String> delete(@PathVariable String code) {
        permissionService.delete(code);
        return Result.success("删除成功");
    }
}
