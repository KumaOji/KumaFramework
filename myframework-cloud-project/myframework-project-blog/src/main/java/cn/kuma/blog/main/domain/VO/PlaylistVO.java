package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 播放列表视图对象
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "播放列表信息")
@Data
public class PlaylistVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 播放列表ID
     */
    private Long id;

    /**
     * 播放列表名称
     */
    private String name;

    /**
     * 播放列表描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 音乐数量
     */
    private Integer musicCount;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 是否公开：0-私有，1-公开
     */
    private Integer isPublic;

    /**
     * 状态：0-正常，1-已删除
     */
    private Integer status;

    /**
     * 音乐列表
     */
    private List<MusicVO> musicList;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
