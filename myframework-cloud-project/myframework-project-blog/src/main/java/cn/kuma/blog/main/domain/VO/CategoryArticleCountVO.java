package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分类文章数统计 VO
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "分类文章数统计")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryArticleCountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "文章数量（排除已删除）")
    private Long count;
}
