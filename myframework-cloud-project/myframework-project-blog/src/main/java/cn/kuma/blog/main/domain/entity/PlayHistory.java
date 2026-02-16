package cn.kuma.blog.main.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 播放历史实体类
 *
 * @author Kuma
 * @version 1.0
 */
@Data
@TableName("play_history")
public class PlayHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 历史记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 音乐ID
     */
    private Long musicId;

    /**
     * 播放时长（秒）
     */
    private Integer playDuration;

    /**
     * 播放时间
     */
    private LocalDateTime playTime;
}
