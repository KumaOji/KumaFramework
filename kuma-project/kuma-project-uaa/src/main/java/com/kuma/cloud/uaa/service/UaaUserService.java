package com.kuma.cloud.uaa.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.cloud.uaa.domain.dto.PasswordChangeDTO;
import com.kuma.cloud.uaa.domain.dto.UserSaveDTO;
import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.domain.query.UserQuery;
import com.kuma.cloud.uaa.domain.vo.UserVO;

import java.util.List;

public interface UaaUserService {

    /**
     * 按登录名查询，不存在时返回 null。
     */
    UaaUser getByUsername(String username);

    /**
     * 按登录名查询，不存在时抛出业务异常。
     */
    UaaUser requireByUsername(String username);

    UserVO getDetail(Long userId);

    IPage<UserVO> page(UserQuery query);

    Long create(UserSaveDTO dto);

    void update(Long userId, UserSaveDTO dto);

    void delete(Long userId);

    void changeStatus(Long userId, boolean enabled);

    void changePassword(Long userId, PasswordChangeDTO dto);

    void resetPassword(Long userId, String rawPassword);

    /**
     * 覆盖式设置用户角色。
     */
    void assignRoles(Long userId, List<String> roleCodes);

    List<String> listRoleCodes(Long userId);

    List<String> listPermissionCodes(Long userId);

    void updateMfa(Long userId, String secret, boolean enabled);

    void recordLoginSuccess(Long userId, String clientIp);

    /**
     * 吊销指定用户的全部 OAuth2 授权，强制其下线。
     */
    void revokeSessions(Long userId);
}
