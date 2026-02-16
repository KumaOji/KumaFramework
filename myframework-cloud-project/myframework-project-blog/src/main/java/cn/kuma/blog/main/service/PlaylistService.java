package cn.kuma.blog.main.service;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.main.domain.VO.PlaylistVO;
import cn.kuma.blog.main.domain.entity.Playlist;

import java.util.List;

/**
 * 播放列表服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface PlaylistService {

    /**
     * 创建播放列表
     *
     * @param playlist 播放列表实体
     * @return 播放列表ID
     */
    Long createPlaylist(Playlist playlist);

    /**
     * 更新播放列表
     *
     * @param playlist 播放列表实体
     * @return 是否成功
     */
    boolean updatePlaylist(Playlist playlist);

    /**
     * 根据ID删除播放列表（逻辑删除）
     *
     * @param id 播放列表ID
     * @return 是否成功
     */
    boolean deletePlaylist(Long id);

    /**
     * 根据ID查询播放列表（包含音乐列表）
     *
     * @param id 播放列表ID
     * @return 播放列表视图对象
     */
    PlaylistVO getPlaylistById(Long id);

    /**
     * 分页查询播放列表
     *
     * @param pageParam 分页参数
     * @param userId 用户ID（可选，用于查询特定用户的播放列表）
     * @param isPublic 是否公开（可选）
     * @return 分页结果
     */
    PageResult<PlaylistVO> getPlaylistList(PageParam pageParam, Long userId, Integer isPublic);

    /**
     * 向播放列表添加音乐
     *
     * @param playlistId 播放列表ID
     * @param musicId 音乐ID
     * @return 是否成功
     */
    boolean addMusicToPlaylist(Long playlistId, Long musicId);

    /**
     * 从播放列表移除音乐
     *
     * @param playlistId 播放列表ID
     * @param musicId 音乐ID
     * @return 是否成功
     */
    boolean removeMusicFromPlaylist(Long playlistId, Long musicId);

    /**
     * 更新播放列表中的音乐顺序
     *
     * @param playlistId 播放列表ID
     * @param musicIds 音乐ID列表（按顺序）
     * @return 是否成功
     */
    boolean updatePlaylistMusicOrder(Long playlistId, List<Long> musicIds);

    /**
     * 获取用户的播放列表
     *
     * @param userId 用户ID
     * @return 播放列表列表
     */
    List<PlaylistVO> getUserPlaylists(Long userId);
}
