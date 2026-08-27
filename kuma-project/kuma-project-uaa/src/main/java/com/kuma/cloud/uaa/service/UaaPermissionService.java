package com.kuma.cloud.uaa.service;

import com.kuma.cloud.uaa.domain.vo.PermissionVO;

import java.util.List;

public interface UaaPermissionService {

    List<PermissionVO> listAll();

    Long create(String code, String name, String resource);

    void delete(String code);
}
