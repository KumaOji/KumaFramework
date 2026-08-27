package com.kuma.cloud.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.uaa.domain.entity.UaaPermission;
import com.kuma.cloud.uaa.domain.entity.UaaRolePermission;
import com.kuma.cloud.uaa.domain.vo.PermissionVO;
import com.kuma.cloud.uaa.mapper.UaaPermissionMapper;
import com.kuma.cloud.uaa.mapper.UaaRolePermissionMapper;
import com.kuma.cloud.uaa.service.UaaPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UaaPermissionServiceImpl implements UaaPermissionService {

    private final UaaPermissionMapper permissionMapper;
    private final UaaRolePermissionMapper rolePermissionMapper;

    @Override
    public List<PermissionVO> listAll() {
        List<UaaPermission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<UaaPermission>().orderByAsc(UaaPermission::getCode));
        List<PermissionVO> result = new ArrayList<>(permissions.size());
        for (UaaPermission permission : permissions) {
            PermissionVO vo = new PermissionVO();
            vo.setPermissionId(permission.getId());
            vo.setCode(permission.getCode());
            vo.setName(permission.getName());
            vo.setResource(permission.getResource());
            vo.setStatus(permission.getStatus());
            result.add(vo);
        }
        return result;
    }

    @Override
    public Long create(String code, String name, String resource) {
        UaaPermission existing = permissionMapper.selectOne(
                new LambdaQueryWrapper<UaaPermission>().eq(UaaPermission::getCode, code));
        if (existing != null) {
            throw new BusinessException("权限编码已存在: " + code);
        }
        UaaPermission permission = new UaaPermission();
        permission.setCode(code);
        permission.setName(name);
        permission.setResource(resource);
        permission.setStatus(1);
        permissionMapper.insert(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String code) {
        UaaPermission permission = permissionMapper.selectOne(
                new LambdaQueryWrapper<UaaPermission>().eq(UaaPermission::getCode, code));
        if (permission == null) {
            throw new BusinessException("权限不存在: " + code);
        }
        rolePermissionMapper.delete(new LambdaQueryWrapper<UaaRolePermission>()
                .eq(UaaRolePermission::getPermissionId, permission.getId()));
        permissionMapper.deleteById(permission.getId());
    }
}
