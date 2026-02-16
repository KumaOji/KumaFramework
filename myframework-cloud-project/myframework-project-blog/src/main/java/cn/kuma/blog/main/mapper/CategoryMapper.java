package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章分类 Mapper
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface CategoryMapper extends ExtendMapper<Category> {
}
