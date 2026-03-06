package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.entity.Playlist;
import com.kuma.cloud.blog.domain.entity.PlaylistMusic;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import com.kuma.cloud.blog.domain.vo.PlaylistVO;
import com.kuma.cloud.blog.mapper.MusicMapper;
import com.kuma.cloud.blog.mapper.PlaylistMapper;
import com.kuma.cloud.blog.mapper.PlaylistMusicMapper;
import com.kuma.cloud.blog.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistMapper playlistMapper;
    private final PlaylistMusicMapper playlistMusicMapper;
    private final MusicMapper musicMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlaylist(Playlist playlist) {
        LocalDateTime now = LocalDateTime.now();
        playlist.setCreateTime(now);
        playlist.setUpdateTime(now);
        if (playlist.getMusicCount() == null) playlist.setMusicCount(0);
        if (playlist.getPlayCount() == null) playlist.setPlayCount(0);
        if (playlist.getIsPublic() == null) playlist.setIsPublic(0);
        if (playlist.getStatus() == null) playlist.setStatus(0);
        playlistMapper.insert(playlist);
        return playlist.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePlaylist(Playlist playlist) {
        playlist.setUpdateTime(LocalDateTime.now());
        return playlistMapper.updateById(playlist) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePlaylist(Long id) {
        playlistMusicMapper.deleteByPlaylistId(id);
        return playlistMapper.deleteById(id) > 0;
    }

    @Override
    public PlaylistVO getPlaylistById(Long id) {
        Playlist playlist = playlistMapper.selectById(id);
        if (playlist == null) return null;
        return toVO(playlist, true);
    }

    @Override
    public IPage<PlaylistVO> getPlaylistList(PageQuery pageQuery) {
        IPage<Playlist> page = playlistMapper.selectPage(
                new Page<>(pageQuery.getCurrentPage(), pageQuery.getPageSize()),
                new LambdaQueryWrapper<Playlist>().eq(Playlist::getStatus, 0).orderByDesc(Playlist::getCreateTime));
        return page.convert(p -> toVO(p, false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMusic(Long playlistId, Long musicId) {
        LambdaQueryWrapper<PlaylistMusic> qw = new LambdaQueryWrapper<>();
        qw.eq(PlaylistMusic::getPlaylistId, playlistId).eq(PlaylistMusic::getMusicId, musicId);
        if (playlistMusicMapper.selectCount(qw) > 0) return false;

        int maxOrder = playlistMusicMapper.selectMusicIdsByPlaylistId(playlistId).size();
        PlaylistMusic pm = new PlaylistMusic();
        pm.setPlaylistId(playlistId);
        pm.setMusicId(musicId);
        pm.setSortOrder(maxOrder + 1);
        pm.setCreateTime(LocalDateTime.now());
        playlistMusicMapper.insert(pm);

        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist != null) {
            playlist.setMusicCount(playlist.getMusicCount() + 1);
            playlist.setUpdateTime(LocalDateTime.now());
            playlistMapper.updateById(playlist);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMusic(Long playlistId, Long musicId) {
        LambdaQueryWrapper<PlaylistMusic> qw = new LambdaQueryWrapper<>();
        qw.eq(PlaylistMusic::getPlaylistId, playlistId).eq(PlaylistMusic::getMusicId, musicId);
        int deleted = playlistMusicMapper.delete(qw);
        if (deleted > 0) {
            Playlist playlist = playlistMapper.selectById(playlistId);
            if (playlist != null && playlist.getMusicCount() > 0) {
                playlist.setMusicCount(playlist.getMusicCount() - 1);
                playlist.setUpdateTime(LocalDateTime.now());
                playlistMapper.updateById(playlist);
            }
        }
        return deleted > 0;
    }

    @Override
    public List<PlaylistVO> getUserPlaylists(Long userId) {
        List<Playlist> playlists = playlistMapper.selectList(
                new LambdaQueryWrapper<Playlist>().eq(Playlist::getUserId, userId).eq(Playlist::getStatus, 0)
                        .orderByDesc(Playlist::getCreateTime));
        return playlists.stream().map(p -> toVO(p, false)).collect(Collectors.toList());
    }

    private PlaylistVO toVO(Playlist playlist, boolean loadMusic) {
        PlaylistVO vo = new PlaylistVO();
        BeanUtils.copyProperties(playlist, vo);
        if (loadMusic) {
            List<Long> musicIds = playlistMusicMapper.selectMusicIdsByPlaylistId(playlist.getId());
            if (!musicIds.isEmpty()) {
                List<Music> musicList = musicMapper.selectBatchIds(musicIds);
                vo.setMusicList(musicList.stream().map(m -> {
                    MusicVO mv = new MusicVO();
                    BeanUtils.copyProperties(m, mv);
                    return mv;
                }).collect(Collectors.toList()));
            } else {
                vo.setMusicList(Collections.emptyList());
            }
        }
        return vo;
    }
}
