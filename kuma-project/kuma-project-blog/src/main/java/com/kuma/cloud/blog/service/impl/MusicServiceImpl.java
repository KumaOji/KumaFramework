package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.vo.MusicQueryVO;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import com.kuma.cloud.blog.mapper.MusicMapper;
import com.kuma.cloud.blog.service.MusicService;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private static final Set<String> ALLOWED_ORDER_COLUMNS =
            Set.of("create_time", "play_count", "like_count", "name", "artist", "album");

    private final MusicMapper musicMapper;

    @Value("${blog.music.base-path:./music}")
    private String musicBasePath;

    private Path resolvedBasePath;

    @PostConstruct
    void init() {
        resolvedBasePath = Paths.get(musicBasePath).toAbsolutePath().normalize();
    }

    @Override
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
    public boolean updateMusic(Music music) {
        music.setUpdateTime(LocalDateTime.now());
        return musicMapper.updateById(music) > 0;
    }

    @Override
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
                if (!ALLOWED_ORDER_COLUMNS.contains(queryVO.getOrderBy())) {
                    throw new BusinessException("非法排序字段: " + queryVO.getOrderBy());
                }
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
    public boolean incrementPlayCount(Long id) {
        return musicMapper.incrementPlayCount(id) > 0;
    }

    @Override
    public boolean incrementLikeCount(Long id) {
        return musicMapper.incrementLikeCount(id) > 0;
    }

    @Cached(name = "music:recommend:", cacheType = CacheType.LOCAL, expire = 300)
    @Override
    public List<MusicVO> getRecommendMusic(int limit) {
        QueryWrapper<Music> qw = new QueryWrapper<>();
        qw.eq("status", 1).eq("is_recommend", 1).orderByDesc("create_time").last("LIMIT " + limit);
        return musicMapper.selectList(qw).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Cached(name = "music:hot:", cacheType = CacheType.LOCAL, expire = 300)
    @Override
    public List<MusicVO> getHotMusic(int limit) {
        QueryWrapper<Music> qw = new QueryWrapper<>();
        qw.eq("status", 1).orderByDesc("play_count").last("LIMIT " + limit);
        return musicMapper.selectList(qw).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Cached(name = "music:file:", cacheType = CacheType.LOCAL, expire = 3600)
    @Override
    public Resource getMusicFile(Long id) {
        Music music = musicMapper.selectById(id);
        if (music == null || StringUtils.isBlank(music.getFilePath())) {
            return null;
        }

        try {
            String filePath = music.getFilePath();
            Path basePath = resolvedBasePath;
            // 兼容数据库中存储的 Windows 绝对路径（如 E:\blog-resource\music\xxx.flac）
            // 在 Linux 环境下 isAbsolute() 为 false，会错误拼接成 /data/music/E:\...
            // 处理方式：提取文件名，在 basePath 下查找
            if (filePath.matches("^[A-Za-z]:[/\\\\].*")) {
                filePath = Paths.get(filePath.replace('\\', '/')).getFileName().toString();
            }
            Path resolvedPath;
            if (Paths.get(filePath).isAbsolute()) {
                resolvedPath = Paths.get(filePath).normalize();
            } else {
                resolvedPath = basePath.resolve(filePath).normalize();
            }

            // 防止路径遍历攻击
            if (!resolvedPath.startsWith(basePath)) {
                log.warn("Security: Path traversal attempt detected. Base: {}, Resolved: {}", basePath, resolvedPath);
                return null;
            }

            File file = resolvedPath.toFile();
            if (!file.exists() || !file.isFile()) {
                log.warn("Music file not found for id: {}, path: {}", id, resolvedPath);
                return null;
            }
            return new FileSystemResource(file);
        } catch (Exception e) {
            log.error("Failed to resolve music file path for music id: {}", id, e);
            return null;
        }
    }

    private MusicVO toVO(Music music) {
        MusicVO vo = new MusicVO();
        BeanUtils.copyProperties(music, vo);
        return vo;
    }
}
