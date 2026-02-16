package cn.kuma.blog.main.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 播放列表实体类
 *
 * @author Kuma
 * @version 1.0
 */
@Data
@TableName("playlist")
public class Playlist implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 播放列表ID
     */
    @TableId(type = IdType.AUTO)
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
