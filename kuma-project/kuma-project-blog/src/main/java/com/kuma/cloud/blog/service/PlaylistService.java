package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Playlist;
import com.kuma.cloud.blog.domain.vo.PlaylistVO;
import java.util.List;

public interface PlaylistService {
    Long createPlaylist(Playlist playlist);
    boolean updatePlaylist(Playlist playlist);
    boolean deletePlaylist(Long id);
    PlaylistVO getPlaylistById(Long id);
    IPage<PlaylistVO> getPlaylistList(PageQuery pageQuery);
    boolean addMusic(Long playlistId, Long musicId);
    boolean removeMusic(Long playlistId, Long musicId);
    List<PlaylistVO> getUserPlaylists(Long userId);
}
