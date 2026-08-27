package com.kuma.cloud.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.uaa.domain.dto.RoleSaveDTO;
import com.kuma.cloud.uaa.domain.entity.UaaPermission;
import com.kuma.cloud.uaa.domain.entity.UaaRole;
import com.kuma.cloud.uaa.domain.entity.UaaRolePermission;
import com.kuma.cloud.uaa.domain.entity.UaaUserRole;
import com.kuma.cloud.uaa.domain.vo.RoleVO;
import com.kuma.cloud.uaa.mapper.UaaPermissionMapper;
import com.kuma.cloud.uaa.mapper.UaaRoleMapper;
import com.kuma.cloud.uaa.mapper.UaaRolePermissionMapper;
import com.kuma.cloud.uaa.mapper.UaaUserRoleMapper;
import com.kuma.cloud.uaa.service.UaaRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UaaRoleServiceImpl implements UaaRoleService {

    private final UaaRoleMapper roleMapper;
    private final UaaPermissionMapper permissionMapper;
    private final UaaRolePermissionMapper rolePermissionMapper;
    private final UaaUserRoleMapper userRoleMapper;

    @Override
    public List<RoleVO> listAll() {
        List<UaaRole> roles =
                roleMapper.selectList(new LambdaQueryWrapper<UaaRole>().orderByAsc(UaaRole::getCode));
        List<RoleVO> result = new ArrayList<>(roles.size());
        for (UaaRole role : roles) {
            result.add(toVO(role));
        }
        return result;
    }

    @Override
    public RoleVO getByCode(String code) {
        return toVO(requireRole(code));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(RoleSaveDTO dto) {
        if (roleMapper.selectByCode(dto.getCode()) != null) {
            throw new BusinessException("角色编码已存在: " + dto.getCode());
        }
        UaaRole role = new UaaRole();
        role.setCode(dto.getCode());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setStatus(1);
        roleMapper.insert(role);

        if (dto.getPermissionCodes() != null) {
            assignPermissions(dto.getCode(), new ArrayList<>(dto.getPermissionCodes()));
        }
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String code, RoleSaveDTO dto) {
        UaaRole role = requireRole(code);
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        roleMapper.updateById(role);

        if (dto.getPermissionCodes() != null) {
            assignPermissions(code, new ArrayList<>(dto.getPermissionCodes()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String code) {
        UaaRole role = requireRole(code);
        Long bound = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UaaUserRole>().eq(UaaUserRole::getRoleId, role.getId()));
        if (bound != null && bound > 0) {
            throw new BusinessException("角色仍被 " + bound + " 个用户引用，无法删除");
        }
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<UaaRolePermission>().eq(UaaRolePermission::getRoleId, role.getId()));
        roleMapper.deleteById(role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(String roleCode, List<String> permissionCodes) {
        UaaRole role = requireRole(roleCode);
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<UaaRolePermission>().eq(UaaRolePermission::getRoleId, role.getId()));
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return;
        }
        Set<String> distinctCodes = new LinkedHashSet<>(permissionCodes);
        for (String code : distinctCodes) {
            UaaPermission permission = permissionMapper.selectOne(
                    new LambdaQueryWrapper<UaaPermission>().eq(UaaPermission::getCode, code));
            if (permission == null) {
                throw new BusinessException("权限不存在: " + code);
            }
            UaaRolePermission relation = new UaaRolePermission();
            relation.setRoleId(role.getId());
            relation.setPermissionId(permission.getId());
            rolePermissionMapper.insert(relation);
        }
    }

    private UaaRole requireRole(String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException("角色编码不能为空");
        }
        UaaRole role = roleMapper.selectByCode(code);
        if (role == null) {
            throw new BusinessException("角色不存在: " + code);
        }
        return role;
    }

    private RoleVO toVO(UaaRole role) {
        RoleVO vo = new RoleVO();
        vo.setRoleId(role.getId());
        vo.setCode(role.getCode());
        vo.setName(role.getName());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        List<String> permissions = new ArrayList<>();
        for (UaaPermission permission : permissionMapper.selectPermissionsByRoleId(role.getId())) {
            permissions.add(permission.getCode());
        }
        vo.setPermissions(permissions);
        return vo;
    }
}
