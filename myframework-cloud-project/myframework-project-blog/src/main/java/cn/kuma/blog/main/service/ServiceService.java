package cn.kuma.blog.main.service;

import java.util.Map;

/**
 * 服务管理服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface ServiceService {

    /**
     * 启动 LibreTranslate 服务
     *
     * @return 启动结果
     */
    Map<String, Object> startLibreTranslate();

    /**
     * 停止 LibreTranslate 服务
     *
     * @return 停止结果消息
     */
    String stopLibreTranslate();

    /**
     * 查询 LibreTranslate 服务状态
     *
     * @return 服务状态信息
     */
    Map<String, Object> getLibreTranslateStatus();

    /**
     * 启动 MediaCrawler 服务
     *
     * @return 启动结果
     */
    Map<String, Object> startMediaCrawler();

    /**
     * 停止 MediaCrawler 服务
     *
     * @return 停止结果消息
     */
    String stopMediaCrawler();

    /**
     * 查询 MediaCrawler 服务状态
     *
     * @return 服务状态信息
     */
    Map<String, Object> getMediaCrawlerStatus();
}
