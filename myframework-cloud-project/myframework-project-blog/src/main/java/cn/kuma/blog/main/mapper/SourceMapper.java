package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.Source;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源Mapper接口
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface SourceMapper extends ExtendMapper<Source> {
}
