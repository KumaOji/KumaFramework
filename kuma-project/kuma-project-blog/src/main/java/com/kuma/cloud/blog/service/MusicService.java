package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.vo.MusicQueryVO;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import java.util.List;

public interface MusicService {
    Long createMusic(Music music);
    boolean updateMusic(Music music);
    boolean deleteMusic(Long id);
    MusicVO getMusicById(Long id);
    IPage<MusicVO> getMusicList(PageQuery pageQuery, MusicQueryVO queryVO);
    boolean incrementPlayCount(Long id);
    boolean incrementLikeCount(Long id);
    List<MusicVO> getRecommendMusic(int limit);
    List<MusicVO> getHotMusic(int limit);
}
