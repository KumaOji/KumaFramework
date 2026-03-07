package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.vo.MusicQueryVO;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import com.kuma.cloud.blog.mapper.MusicMapper;
import com.kuma.cloud.blog.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final MusicMapper musicMapper;

    @Value("${blog.music.base-path:./music}")
    private String musicBasePath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMusic(Music music) {
        LocalDateTime now = LocalDateTime.now();
        music.setCreateTime(now);
        music.setUpdateTime(now);
        if (music.getPlayCount() == null) music.setPlayCount(0);
        if (music.getLikeCount() == null) music.setLikeCount(0);
        if (music.getStatus() == null) music.setStatus(1);
        if (music.getIsRecommend() == null) music.setIsRecommend(0);
        musicMapper.insert(music);
        return music.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMusic(Music music) {
        music.setUpdateTime(LocalDateTime.now());
        return musicMapper.updateById(music) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMusic(Long id) {
        Music music = new Music();
        music.setId(id);
        music.setStatus(2);
        music.setUpdateTime(LocalDateTime.now());
        return musicMapper.updateById(music) > 0;
    }

    @Override
    public MusicVO getMusicById(Long id) {
        Music music = musicMapper.selectById(id);
        if (music == null) return null;
        return toVO(music);
    }

    @Override
    public IPage<MusicVO> getMusicList(PageQuery pageQuery, MusicQueryVO queryVO) {
        QueryWrapper<Music> qw = new QueryWrapper<>();
        qw.ne("status", 2);

        if (queryVO != null) {
            if (StringUtils.isNotEmpty(queryVO.getName())) qw.like("name", queryVO.getName());
            if (StringUtils.isNotEmpty(queryVO.getArtist())) qw.like("artist", queryVO.getArtist());
            if (StringUtils.isNotEmpty(queryVO.getAlbum())) qw.like("album", queryVO.getAlbum());
            if (StringUtils.isNotEmpty(queryVO.getGenre())) qw.eq("genre", queryVO.getGenre());
            if (queryVO.getStatus() != null) qw.eq("status", queryVO.getStatus());
            if (queryVO.getIsRecommend() != null) qw.eq("is_recommend", queryVO.getIsRecommend());
            if (StringUtils.isNotEmpty(queryVO.getOrderBy())) {
                boolean asc = "asc".equalsIgnoreCase(queryVO.getOrderDirection());
                qw.orderBy(true, asc, queryVO.getOrderBy());
            } else {
                qw.orderByDesc("create_time");
            }
        } else {
            qw.orderByDesc("create_time");
        }

        IPage<Music> page = musicMapper.selectPage(
                new Page<>(pageQuery.getCurrentPage(), pageQuery.getPageSize()), qw);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementPlayCount(Long id) {
        return musicMapper.incrementPlayCount(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementLikeCount(Long id) {
        return musicMapper.incrementLikeCount(id) > 0;
    }

    @Override
    public List<MusicVO> getRecommendMusic(int limit) {
        QueryWrapper<Music> qw = new QueryWrapper<>();
        qw.eq("status", 1).eq("is_recommend", 1).orderByDesc("create_time").last("LIMIT " + limit);
        return musicMapper.selectList(qw).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<MusicVO> getHotMusic(int limit) {
        QueryWrapper<Music> qw = new QueryWrapper<>();
        qw.eq("status", 1).orderByDesc("play_count").last("LIMIT " + limit);
        return musicMapper.selectList(qw).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Resource getMusicFile(Long id) {
        Music music = musicMapper.selectById(id);
        if (music == null || StringUtils.isBlank(music.getFilePath())) {
            return null;
        }
        String filePath = music.getFilePath();
        Path path;
        if (Paths.get(filePath).isAbsolute()) {
            path = Paths.get(filePath);
        } else {
            path = Paths.get(musicBasePath, filePath);
        }
        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        return new FileSystemResource(file);
    }

    private MusicVO toVO(Music music) {
        MusicVO vo = new MusicVO();
        BeanUtils.copyProperties(music, vo);
        return vo;
    }
}
