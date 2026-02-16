package cn.kuma.blog.main.service;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.main.domain.VO.ReadyQueryVO;
import cn.kuma.blog.main.domain.VO.ReadyVO;
import cn.kuma.blog.main.domain.entity.ReadyItem;

/**
 * 待处理事项服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface ReadyService {

    /**
     * 创建待处理事项
     *
     * @param item 实体
     * @return 主键ID
     */
    Long create(ReadyItem item);

    /**
     * 更新待处理事项
     *
     * @param item 实体（需包含 id）
     * @return 是否成功
     */
    boolean update(ReadyItem item);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 物理删除
     *
     * @param id 主键ID
     * @return 是否成功
     */
    boolean deletePhysical(Long id);

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return ReadyVO，不存在则 null
     */
    ReadyVO getById(Long id);

    /**
     * 切换状态
     * 待处理(0)/进行中(1) <-> 已完成(2)
     *
     * @param id 主键ID
     * @return ReadyVO，切换成功返回更新后的对象，失败或不存在则返回 null
     */
    ReadyVO toggle(Long id);

    /**
     * 分页查询列表（排除已删除）
     *
     * @param pageParam 分页参数
     * @param queryVO   查询条件
     * @return 分页结果
     */
    PageResult<ReadyVO> list(PageParam pageParam, ReadyQueryVO queryVO);
}
