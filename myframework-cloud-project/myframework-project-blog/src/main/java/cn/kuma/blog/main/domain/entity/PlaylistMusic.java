package cn.kuma.blog.main.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 播放列表音乐关联实体类
 *
 * @author Kuma
 * @version 1.0
 */
@Data
@TableName("playlist_music")
public class PlaylistMusic implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 播放列表ID
     */
    private Long playlistId;

    /**
     * 音乐ID
     */
    private Long musicId;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 添加时间
     */
    private LocalDateTime createTime;
}
