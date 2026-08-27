package com.kuma.cloud.uaa.service;

import com.kuma.cloud.uaa.domain.dto.ClientSaveDTO;
import com.kuma.cloud.uaa.domain.vo.ClientVO;

import java.util.List;

/**
 * 接入客户端管理，底层复用 Spring Authorization Server 的
 * {@code JdbcRegisteredClientRepository}，保证与协议端点读写同一份数据。
 */
public interface UaaClientService {

    List<ClientVO> listAll();

    ClientVO getByClientId(String clientId);

    /**
     * 按 clientId 幂等写入：存在则更新，不存在则新建。
     * {@code dto.clientSecret} 为空时保留原有密钥。
     */
    void save(ClientSaveDTO dto);

    void delete(String clientId);
}
