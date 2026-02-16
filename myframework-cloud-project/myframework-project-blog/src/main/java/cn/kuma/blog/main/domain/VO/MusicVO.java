package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 音乐视图对象
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "音乐信息")
@Data
public class MusicVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 音乐ID
     */
    private Long id;

    /**
     * 音乐名称
     */
    private String name;

    /**
     * 艺术家/歌手
     */
    private String artist;

    /**
     * 专辑名称
     */
    private String album;

    /**
     * 音乐文件URL（访问地址）
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（如：mp3, flac, wav等）
     */
    private String fileType;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 音乐分类/风格
     */
    private String genre;

    /**
     * 歌词内容
     */
    private String lyrics;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态：0-待审核，1-已发布，2-已删除
     */
    private Integer status;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isRecommend;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
