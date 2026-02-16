package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.ReadyItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 待处理事项 Mapper
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface ReadyMapper extends ExtendMapper<ReadyItem> {

    /**
     * 逻辑删除：状态改为 3-已删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByIdLogic(@Param("id") Long id);

    /**
     * 物理删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByIdPhysical(@Param("id") Long id);
}
