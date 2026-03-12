package com.kuma.cloud.blog.service;

import java.util.Map;

/**
 * 服务管理接口（管理外部进程服务，如 LibreTranslate）
 *
 * @author Kuma
 */
public interface ServiceService {

    /**
     * 启动 LibreTranslate 翻译服务
     *
     * @return 启动结果信息（异步启动，需通过 status 接口确认）
     */
    Map<String, Object> startLibreTranslate();

    /**
     * 停止 LibreTranslate 翻译服务
     *
     * @return 操作结果描述
     */
    String stopLibreTranslate();

    /**
     * 查询 LibreTranslate 服务运行状态
     *
     * @return 状态信息（running、pid、host、port 等）
     */
    Map<String, Object> getLibreTranslateStatus();
}
