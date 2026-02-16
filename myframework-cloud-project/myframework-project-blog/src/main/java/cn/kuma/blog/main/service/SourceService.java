package cn.kuma.blog.main.service;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.main.domain.entity.Source;

import java.util.List;

/**
 * 资源服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface SourceService {

    /**
     * 创建资源
     *
     * @param source 资源实体
     * @return 资源ID
     */
    Long createSource(Source source);

    /**
     * 更新资源
     *
     * @param source 资源实体
     * @return 是否成功
     */
    boolean updateSource(Source source);

    /**
     * 根据ID删除资源
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean deleteSource(Long id);

    /**
     * 根据ID查询资源
     *
     * @param id 资源ID
     * @return 资源实体
     */
    Source getSourceById(Long id);

    /**
     * 分页查询资源列表
     *
     * @param pageParam 分页参数
     * @param name 资源名称（模糊查询，可选）
     * @return 分页结果
     */
    PageResult<Source> getSourceList(PageParam pageParam, String name);

    /**
     * 查询所有资源列表
     *
     * @return 资源列表
     */
    List<Source> getAllSources();
}
