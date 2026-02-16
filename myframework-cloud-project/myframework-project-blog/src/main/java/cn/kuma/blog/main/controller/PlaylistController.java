package cn.kuma.blog.main.controller;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import cn.kuma.blog.framework.util.UserDetailUtils;
import cn.kuma.blog.main.domain.VO.PlaylistVO;
import cn.kuma.blog.main.domain.entity.Playlist;
import cn.kuma.blog.main.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 播放列表管理控制器
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "播放列表管理", description = "播放列表的增删改查接口")
@RestController
@RequestMapping("/playlist")
@PreAuthorize("hasRole('ADMIN')")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    /**
     * 创建播放列表
     */
    @Operation(summary = "创建播放列表", description = "创建一个新的播放列表，需要登录")
    @PostMapping
    public ApiResult<Long> createPlaylist(
            @Parameter(description = "播放列表信息", required = true) @RequestBody Playlist playlist) {
        try {
            // 获取当前用户ID
            Long userId = null;
            try {
                String userIdStr = UserDetailUtils.getUserDetail().getUserID();
                if (StringUtils.hasText(userIdStr)) {
                    userId = Long.parseLong(userIdStr);
                }
            } catch (Exception e) {
                return ApiResult.failed(SystemResultCode.UNAUTHORIZED, "请先登录");
            }

            playlist.setUserId(userId);
            Long playlistId = playlistService.createPlaylist(playlist);
            return ApiResult.ok(playlistId, "播放列表创建成功");
        } catch (Exception e) {
            return ApiResult.failed(500, "播放列表创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新播放列表
     */
    @Operation(summary = "更新播放列表", description = "根据ID更新播放列表信息")
    @PutMapping("/{id}")
    public ApiResult<Boolean> updatePlaylist(
            @Parameter(description = "播放列表ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "播放列表信息", required = true) @RequestBody Playlist playlist) {
        try {
            playlist.setId(id);
            boolean success = playlistService.updatePlaylist(playlist);
            if (success) {
                return ApiResult.ok(true, "播放列表更新成功");
            } else {
                return ApiResult.failed(500, "播放列表更新失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "播放列表更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除播放列表
     */
    @Operation(summary = "删除播放列表", description = "根据ID删除播放列表（逻辑删除）")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> deletePlaylist(
            @Parameter(description = "播放列表ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = playlistService.deletePlaylist(id);
            if (success) {
                return ApiResult.ok(true, "播放列表删除成功");
            } else {
                return ApiResult.failed(500, "播放列表删除失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "播放列表删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询播放列表
     */
    @Operation(summary = "查询播放列表详情", description = "根据ID查询播放列表详细信息（包含音乐列表）")
    @GetMapping("/{id}")
    public ApiResult<PlaylistVO> getPlaylistById(
            @Parameter(description = "播放列表ID", required = true) @PathVariable("id") Long id) {
        try {
            PlaylistVO playlist = playlistService.getPlaylistById(id);
            if (playlist != null) {
                return ApiResult.ok(playlist);
            } else {
                return ApiResult.failed(SystemResultCode.NOT_FOUND);
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "查询播放列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询播放列表
     */
    @Operation(summary = "分页查询播放列表", description = "根据条件分页查询播放列表")
    @GetMapping("/list")
    public ApiResult<PageResult<PlaylistVO>> getPlaylistList(
            @Parameter(description = "分页参数") PageParam pageParam,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId,
            @Parameter(description = "是否公开：0-私有，1-公开") @RequestParam(required = false) Integer isPublic) {
        try {
            PageResult<PlaylistVO> pageResult = playlistService.getPlaylistList(pageParam, userId, isPublic);
            return ApiResult.ok(pageResult);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询播放列表失败: " + e.getMessage());
        }
    }

    /**
     * 向播放列表添加音乐
     */
    @Operation(summary = "添加音乐到播放列表", description = "向播放列表添加一首音乐")
    @PostMapping("/{playlistId}/music/{musicId}")
    public ApiResult<Boolean> addMusicToPlaylist(
            @Parameter(description = "播放列表ID", required = true) @PathVariable("playlistId") Long playlistId,
            @Parameter(description = "音乐ID", required = true) @PathVariable("musicId") Long musicId) {
        try {
            boolean success = playlistService.addMusicToPlaylist(playlistId, musicId);
            if (success) {
                return ApiResult.ok(true, "音乐添加成功");
            } else {
                return ApiResult.failed(500, "音乐添加失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "音乐添加失败: " + e.getMessage());
        }
    }

    /**
     * 从播放列表移除音乐
     */
    @Operation(summary = "从播放列表移除音乐", description = "从播放列表移除一首音乐")
    @DeleteMapping("/{playlistId}/music/{musicId}")
    public ApiResult<Boolean> removeMusicFromPlaylist(
            @Parameter(description = "播放列表ID", required = true) @PathVariable("playlistId") Long playlistId,
            @Parameter(description = "音乐ID", required = true) @PathVariable("musicId") Long musicId) {
        try {
            boolean success = playlistService.removeMusicFromPlaylist(playlistId, musicId);
            if (success) {
                return ApiResult.ok(true, "音乐移除成功");
            } else {
                return ApiResult.failed(500, "音乐移除失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "音乐移除失败: " + e.getMessage());
        }
    }

    /**
     * 更新播放列表中的音乐顺序
     */
    @Operation(summary = "更新播放列表音乐顺序", description = "更新播放列表中音乐的播放顺序")
    @PutMapping("/{playlistId}/music/order")
    public ApiResult<Boolean> updatePlaylistMusicOrder(
            @Parameter(description = "播放列表ID", required = true) @PathVariable("playlistId") Long playlistId,
            @Parameter(description = "音乐ID列表（按顺序）", required = true) @RequestBody List<Long> musicIds) {
        try {
            boolean success = playlistService.updatePlaylistMusicOrder(playlistId, musicIds);
            if (success) {
                return ApiResult.ok(true, "音乐顺序更新成功");
            } else {
                return ApiResult.failed(500, "音乐顺序更新失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "音乐顺序更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的播放列表
     */
    @Operation(summary = "获取用户播放列表", description = "获取当前登录用户的所有播放列表")
    @GetMapping("/my")
    public ApiResult<List<PlaylistVO>> getUserPlaylists() {
        try {
            // 获取当前用户ID
            Long userId = null;
            try {
                String userIdStr = UserDetailUtils.getUserDetail().getUserID();
                if (StringUtils.hasText(userIdStr)) {
                    userId = Long.parseLong(userIdStr);
                }
            } catch (Exception e) {
                return ApiResult.failed(SystemResultCode.UNAUTHORIZED, "请先登录");
            }

            List<PlaylistVO> playlists = playlistService.getUserPlaylists(userId);
            return ApiResult.ok(playlists);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询播放列表失败: " + e.getMessage());
        }
    }
}
