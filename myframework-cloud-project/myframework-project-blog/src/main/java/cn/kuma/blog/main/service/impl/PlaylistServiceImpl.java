package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.framework.mybatisplus.toolkit.PageUtil;
import cn.kuma.blog.main.domain.VO.MusicVO;
import cn.kuma.blog.main.domain.VO.PlaylistVO;
import cn.kuma.blog.main.domain.entity.Playlist;
import cn.kuma.blog.main.domain.entity.PlaylistMusic;
import cn.kuma.blog.main.mapper.MusicMapper;
import cn.kuma.blog.main.mapper.PlaylistMapper;
import cn.kuma.blog.main.mapper.PlaylistMusicMapper;
import cn.kuma.blog.main.service.MusicService;
import cn.kuma.blog.main.service.PlaylistService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 播放列表服务实现类
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistMapper playlistMapper;

    @Autowired
    private PlaylistMusicMapper playlistMusicMapper;

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private MusicService musicService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlaylist(Playlist playlist) {
        LocalDateTime now = LocalDateTime.now();
        playlist.setCreateTime(now);
        playlist.setUpdateTime(now);

        // 初始化统计数据
        if (playlist.getMusicCount() == null) {
            playlist.setMusicCount(0);
        }
        if (playlist.getPlayCount() == null) {
            playlist.setPlayCount(0);
        }
        if (playlist.getIsPublic() == null) {
            playlist.setIsPublic(0); // 默认私有
        }
        if (playlist.getStatus() == null) {
            playlist.setStatus(0); // 默认正常
        }

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
        Playlist playlist = new Playlist();
        playlist.setId(id);
        playlist.setStatus(1); // 逻辑删除
        playlist.setUpdateTime(LocalDateTime.now());
        return playlistMapper.updateById(playlist) > 0;
    }

    @Override
    public PlaylistVO getPlaylistById(Long id) {
        Playlist playlist = playlistMapper.selectById(id);
        if (playlist == null || playlist.getStatus() == 1) {
            return null;
        }

        PlaylistVO playlistVO = new PlaylistVO();
        BeanUtils.copyProperties(playlist, playlistVO);

        // 查询播放列表中的音乐
        List<Long> musicIds = playlistMusicMapper.selectMusicIdsByPlaylistId(id);
        if (musicIds != null && !musicIds.isEmpty()) {
            List<MusicVO> musicList = musicIds.stream()
                    .map(musicId -> musicService.getMusicById(musicId))
                    .filter(music -> music != null)
                    .collect(Collectors.toList());
            playlistVO.setMusicList(musicList);
        } else {
            playlistVO.setMusicList(new ArrayList<>());
        }

        return playlistVO;
    }

    @Override
    public PageResult<PlaylistVO> getPlaylistList(PageParam pageParam, Long userId, Integer isPublic) {
        QueryWrapper<Playlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0); // 只查询正常状态的播放列表

        // 用户ID查询
        Optional.ofNullable(userId)
                .ifPresent(id -> queryWrapper.eq("user_id", id));

        // 是否公开查询
        Optional.ofNullable(isPublic)
                .ifPresent(publicFlag -> queryWrapper.eq("is_public", publicFlag));

        // 排序：按创建时间降序
        queryWrapper.orderByDesc("create_time");

        // 分页查询
        IPage<Playlist> playlistPage = playlistMapper.selectPage(PageUtil.prodPage(pageParam), queryWrapper);

        // 转换为 VO
        List<PlaylistVO> playlistVOList = playlistPage.getRecords().stream()
                .map(playlist -> {
                    PlaylistVO playlistVO = new PlaylistVO();
                    BeanUtils.copyProperties(playlist, playlistVO);
                    // 不加载音乐列表，减少查询量
                    playlistVO.setMusicList(new ArrayList<>());
                    return playlistVO;
                })
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<PlaylistVO> pageResult = new PageResult<>();
        pageResult.setRecords(playlistVOList);
        pageResult.setTotal(playlistPage.getTotal());

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMusicToPlaylist(Long playlistId, Long musicId) {
        // 检查播放列表是否存在
        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist == null || playlist.getStatus() == 1) {
            return false;
        }

        // 检查音乐是否存在
        if (musicMapper.selectById(musicId) == null) {
            return false;
        }

        // 检查是否已存在
        QueryWrapper<PlaylistMusic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", playlistId)
                    .eq("music_id", musicId);
        if (playlistMusicMapper.selectOne(queryWrapper) != null) {
            return false; // 已存在
        }

        // 获取当前最大排序顺序
        QueryWrapper<PlaylistMusic> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("playlist_id", playlistId)
                    .orderByDesc("sort_order")
                    .last("LIMIT 1");
        PlaylistMusic lastMusic = playlistMusicMapper.selectOne(orderWrapper);
        int nextOrder = (lastMusic != null && lastMusic.getSortOrder() != null) 
                        ? lastMusic.getSortOrder() + 1 : 1;

        // 添加关联
        PlaylistMusic playlistMusic = new PlaylistMusic();
        playlistMusic.setPlaylistId(playlistId);
        playlistMusic.setMusicId(musicId);
        playlistMusic.setSortOrder(nextOrder);
        playlistMusic.setCreateTime(LocalDateTime.now());
        playlistMusicMapper.insert(playlistMusic);

        // 更新播放列表音乐数量
        playlist.setMusicCount(playlist.getMusicCount() + 1);
        playlist.setUpdateTime(LocalDateTime.now());
        playlistMapper.updateById(playlist);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMusicFromPlaylist(Long playlistId, Long musicId) {
        QueryWrapper<PlaylistMusic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", playlistId)
                    .eq("music_id", musicId);

        int deleted = playlistMusicMapper.delete(queryWrapper);
        if (deleted > 0) {
            // 更新播放列表音乐数量
            Playlist playlist = playlistMapper.selectById(playlistId);
            if (playlist != null) {
                playlist.setMusicCount(Math.max(0, playlist.getMusicCount() - 1));
                playlist.setUpdateTime(LocalDateTime.now());
                playlistMapper.updateById(playlist);
            }
        }

        return deleted > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePlaylistMusicOrder(Long playlistId, List<Long> musicIds) {
        // 删除所有现有关联
        playlistMusicMapper.deleteByPlaylistId(playlistId);

        // 重新添加关联，按新顺序
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < musicIds.size(); i++) {
            PlaylistMusic playlistMusic = new PlaylistMusic();
            playlistMusic.setPlaylistId(playlistId);
            playlistMusic.setMusicId(musicIds.get(i));
            playlistMusic.setSortOrder(i + 1);
            playlistMusic.setCreateTime(now);
            playlistMusicMapper.insert(playlistMusic);
        }

        // 更新播放列表音乐数量
        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist != null) {
            playlist.setMusicCount(musicIds.size());
            playlist.setUpdateTime(LocalDateTime.now());
            playlistMapper.updateById(playlist);
        }

        return true;
    }

    @Override
    public List<PlaylistVO> getUserPlaylists(Long userId) {
        QueryWrapper<Playlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                    .eq("status", 0)
                    .orderByDesc("create_time");

        List<Playlist> playlists = playlistMapper.selectList(queryWrapper);
        return playlists.stream()
                .map(playlist -> {
                    PlaylistVO playlistVO = new PlaylistVO();
                    BeanUtils.copyProperties(playlist, playlistVO);
                    playlistVO.setMusicList(new ArrayList<>());
                    return playlistVO;
                })
                .collect(Collectors.toList());
    }
}
