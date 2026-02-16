package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 音乐查询视图对象
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "音乐查询参数")
@Data
public class MusicQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 音乐名称（模糊查询）
     */
    private String name;

    /**
     * 艺术家/歌手（模糊查询）
     */
    private String artist;

    /**
     * 专辑名称（模糊查询）
     */
    private String album;

    /**
     * 音乐分类/风格
     */
    private String genre;

    /**
     * 状态：0-待审核，1-已发布，2-已删除
     */
    private Integer status;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isRecommend;

    /**
     * 排序字段：playCount-播放次数，likeCount-点赞数，createTime-创建时间
     */
    private String orderBy;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    private String orderDirection;
}
