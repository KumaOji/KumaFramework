package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.Playlist;
import com.kuma.cloud.blog.domain.vo.PlaylistVO;
import com.kuma.cloud.blog.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "播放列表管理")
@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @Operation(summary = "创建播放列表")
    @PostMapping
    public Result<Long> create(@RequestBody Playlist playlist) {
        return Result.success(playlistService.createPlaylist(playlist));
    }

    @Operation(summary = "更新播放列表")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Playlist playlist) {
        playlist.setId(id);
        return Result.success(playlistService.updatePlaylist(playlist));
    }

    @Operation(summary = "删除播放列表")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(playlistService.deletePlaylist(id));
    }

    @Operation(summary = "查询播放列表详情")
    @GetMapping("/{id}")
    public Result<PlaylistVO> getById(@PathVariable Long id) {
        return Result.success(playlistService.getPlaylistById(id));
    }

    @Operation(summary = "分页查询播放列表")
    @GetMapping("/list")
    public Result<IPage<PlaylistVO>> list(PageQuery pageQuery) {
        return Result.success(playlistService.getPlaylistList(pageQuery));
    }

    @Operation(summary = "添加音乐到播放列表")
    @PostMapping("/{playlistId}/music/{musicId}")
    public Result<Boolean> addMusic(@PathVariable Long playlistId, @PathVariable Long musicId) {
        return Result.success(playlistService.addMusic(playlistId, musicId));
    }

    @Operation(summary = "从播放列表移除音乐")
    @DeleteMapping("/{playlistId}/music/{musicId}")
    public Result<Boolean> removeMusic(@PathVariable Long playlistId, @PathVariable Long musicId) {
        return Result.success(playlistService.removeMusic(playlistId, musicId));
    }

    @Operation(summary = "获取用户的播放列表")
    @GetMapping("/user/{userId}")
    public Result<List<PlaylistVO>> userPlaylists(@PathVariable Long userId) {
        return Result.success(playlistService.getUserPlaylists(userId));
    }
}
