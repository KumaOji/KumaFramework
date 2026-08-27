package com.kuma.cloud.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.uaa.domain.dto.PasswordChangeDTO;
import com.kuma.cloud.uaa.domain.dto.UserSaveDTO;
import com.kuma.cloud.uaa.domain.entity.UaaRole;
import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.domain.entity.UaaUserRole;
import com.kuma.cloud.uaa.domain.query.UserQuery;
import com.kuma.cloud.uaa.domain.vo.UserVO;
import com.kuma.cloud.uaa.mapper.UaaPermissionMapper;
import com.kuma.cloud.uaa.mapper.UaaRoleMapper;
import com.kuma.cloud.uaa.mapper.UaaUserMapper;
import com.kuma.cloud.uaa.mapper.UaaUserRoleMapper;
import com.kuma.cloud.uaa.service.UaaUserService;
import com.kuma.cloud.uaa.support.UaaTokenRevocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UaaUserServiceImpl implements UaaUserService {

    private final UaaUserMapper userMapper;
    private final UaaRoleMapper roleMapper;
    private final UaaPermissionMapper permissionMapper;
    private final UaaUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final UaaTokenRevocationService tokenRevocationService;

    @Override
    public UaaUser getByUsername(String username) {
        return StringUtils.hasText(username) ? userMapper.selectByUsername(username) : null;
    }

    @Override
    public UaaUser requireByUsername(String username) {
        UaaUser user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在: " + username);
        }
        return user;
    }

    @Override
    public UserVO getDetail(Long userId) {
        UaaUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = toVO(user);
        vo.setRoles(listRoleCodes(userId));
        vo.setPermissions(listPermissionCodes(userId));
        return vo;
    }

    @Override
    public IPage<UserVO> page(UserQuery query) {
        LambdaQueryWrapper<UaaUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(nested -> nested.like(UaaUser::getUsername, keyword)
                    .or()
                    .like(UaaUser::getNickname, keyword)
                    .or()
                    .like(UaaUser::getEmail, keyword));
        }
        if (query.getStatus() != null) {
            wrapper.eq(UaaUser::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(UaaUser::getId);

        IPage<UaaUser> page =
                userMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return page.convert(user -> {
            UserVO vo = toVO(user);
            vo.setRoles(listRoleCodes(user.getId()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserSaveDTO dto) {
        if (getByUsername(dto.getUsername()) != null) {
            throw new BusinessException("登录名已存在: " + dto.getUsername());
        }
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BusinessException("新建用户必须设置初始密码");
        }

        UaaUser user = new UaaUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setStatus(1);
        user.setLocked(0);
        user.setMfaEnabled(0);
        user.setTenantId("default");
        userMapper.insert(user);

        if (dto.getRoleCodes() != null) {
            assignRoles(user.getId(), new ArrayList<>(dto.getRoleCodes()));
        }
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, UserSaveDTO dto) {
        UaaUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userMapper.updateById(user);

        if (dto.getRoleCodes() != null) {
            assignRoles(userId, new ArrayList<>(dto.getRoleCodes()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId) {
        UaaUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        tokenRevocationService.revokeByUsername(user.getUsername());
        userRoleMapper.delete(new LambdaQueryWrapper<UaaUserRole>().eq(UaaUserRole::getUserId, userId));
        userMapper.deleteById(userId);
    }

    @Override
    public void changeStatus(Long userId, boolean enabled) {
        UaaUser existing = userMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        UaaUser user = new UaaUser();
        user.setId(userId);
        user.setStatus(enabled ? 1 : 0);
        userMapper.updateById(user);
        if (!enabled) {
            tokenRevocationService.revokeByUsername(existing.getUsername());
        }
    }

    @Override
    public void changePassword(Long userId, PasswordChangeDTO dto) {
        UaaUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        resetPassword(userId, dto.getNewPassword());
    }

    @Override
    public void resetPassword(Long userId, String rawPassword) {
        UaaUser existing = userMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        UaaUser user = new UaaUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(rawPassword));
        userMapper.updateById(user);
        tokenRevocationService.revokeByUsername(existing.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<String> roleCodes) {
        userRoleMapper.delete(new LambdaQueryWrapper<UaaUserRole>().eq(UaaUserRole::getUserId, userId));
        if (roleCodes == null || roleCodes.isEmpty()) {
            return;
        }
        Set<String> distinctCodes = new LinkedHashSet<>(roleCodes);
        for (String code : distinctCodes) {
            UaaRole role = roleMapper.selectByCode(code);
            if (role == null) {
                throw new BusinessException("角色不存在: " + code);
            }
            UaaUserRole relation = new UaaUserRole();
            relation.setUserId(userId);
            relation.setRoleId(role.getId());
            userRoleMapper.insert(relation);
        }
    }

    @Override
    public List<String> listRoleCodes(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<String> listPermissionCodes(Long userId) {
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public void updateMfa(Long userId, String secret, boolean enabled) {
        UaaUser user = new UaaUser();
        user.setId(userId);
        user.setMfaSecret(secret);
        user.setMfaEnabled(enabled ? 1 : 0);
        userMapper.updateById(user);
    }

    @Override
    public void recordLoginSuccess(Long userId, String clientIp) {
        userMapper.updateLastLogin(userId, LocalDateTime.now(), clientIp);
    }

    @Override
    public void revokeSessions(Long userId) {
        UaaUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        tokenRevocationService.revokeByUsername(user.getUsername());
    }

    private UserVO toVO(UaaUser user) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setLocked(user.isLocked());
        vo.setMfaEnabled(user.isMfaEnabled());
        vo.setTenantId(user.getTenantId());
        vo.setLastLoginAt(user.getLastLoginAt());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
