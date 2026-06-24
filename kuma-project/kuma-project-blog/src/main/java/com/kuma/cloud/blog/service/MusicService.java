package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.query.MusicQuery;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MusicService {
    Long createMusic(Music music);
    boolean updateMusic(Music music);
    boolean deleteMusic(Long id);
    MusicVO getMusicById(Long id);
    IPage<MusicVO> getMusicList(PageQuery pageQuery, MusicQuery queryVO);
    boolean incrementPlayCount(Long id);
    boolean incrementLikeCount(Long id);
    List<MusicVO> getRecommendMusic(int limit);
    List<MusicVO> getHotMusic(int limit);

    /** 获取音乐文件资源，用于流式播放 */
    Resource getMusicFile(Long id);

    /** 上传音乐文件到本地存储，返回存储文件名 */
    String uploadMusicFile(MultipartFile file);

    /** 删除指定音乐记录对应的物理文件 */
    boolean deleteMusicFile(Long id);
}
