package cn.kuma.blog.main.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章分类实体（数据库表 article_category）
 *
 * @author Kuma
 * @version 1.0
 */
@Data
@TableName("article_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类代码（英文标识）
     */
    private String code;

    /**
     * 分类名称（中文显示）
     */
    private String name;

    /**
     * 父分类ID（null 表示顶级分类）
     */
    private Long parentId;

    /**
     * 层级：0=顶级，1=二级，2=三级…
     */
    private Integer level;

    /**
     * 小图标：图标类名 / 图标 key / 图片 URL，前端按约定渲染
     */
    private String icon;

    /**
     * 排序（数值越小越靠前）
     */
    private Integer sortOrder;
}
