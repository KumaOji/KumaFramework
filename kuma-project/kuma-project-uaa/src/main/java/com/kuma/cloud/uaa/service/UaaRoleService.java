package com.kuma.cloud.uaa.service;

import com.kuma.cloud.uaa.domain.dto.RoleSaveDTO;
import com.kuma.cloud.uaa.domain.vo.RoleVO;

import java.util.List;

public interface UaaRoleService {

    List<RoleVO> listAll();

    RoleVO getByCode(String code);

    Long create(RoleSaveDTO dto);

    void update(String code, RoleSaveDTO dto);

    void delete(String code);

    /**
     * 覆盖式设置角色权限。
     */
    void assignPermissions(String roleCode, List<String> permissionCodes);
}
