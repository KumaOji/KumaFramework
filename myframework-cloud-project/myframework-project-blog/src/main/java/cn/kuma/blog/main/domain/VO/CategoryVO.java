package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文章分类 VO
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "文章分类信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long id;

    /**
     * 分类代码（英文标识）
     */
    @Schema(description = "分类代码（英文标识）", example = "Diary")
    private String code;

    /**
     * 分类名称（中文显示）
     */
    @Schema(description = "分类名称（中文显示）", example = "日记")
    private String name;

    /**
     * 父分类ID（根分类为 null）
     */
    @Schema(description = "父分类ID（根分类为 null）", example = "null")
    private Long parentId;

    /**
     * 层级：0=顶级，1=二级，2=三级…
     */
    @Schema(description = "层级：0=顶级，1=二级，2=三级…", example = "0")
    private Integer level;

    /**
     * 小图标：图标类名 / 图标 key / 图片 URL，前端按约定渲染
     */
    @Schema(description = "小图标（图标类名/key/图片URL）", example = "BookOutlined")
    private String icon;

    /**
     * 完整路径（例如：技术/Android）
     */
    @Schema(description = "完整路径（例如：技术/Android）", example = "技术/Android")
    private String fullPath;
}
