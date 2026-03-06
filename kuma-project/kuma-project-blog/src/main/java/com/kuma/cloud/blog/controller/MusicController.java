package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.vo.MusicQueryVO;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import com.kuma.cloud.blog.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "音乐管理")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @Operation(summary = "创建音乐")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> create(@RequestBody Music music) {
        return Result.success(musicService.createMusic(music));
    }

    @Operation(summary = "更新音乐")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Music music) {
        music.setId(id);
        return Result.success(musicService.updateMusic(music));
    }

    @Operation(summary = "删除音乐（逻辑）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(musicService.deleteMusic(id));
    }

    @Operation(summary = "查询音乐详情")
    @GetMapping("/{id}")
    public Result<MusicVO> getById(@PathVariable Long id) {
        return Result.success(musicService.getMusicById(id));
    }

    @Operation(summary = "分页查询音乐列表")
    @GetMapping("/list")
    public Result<IPage<MusicVO>> list(PageQuery pageQuery, MusicQueryVO queryVO) {
        return Result.success(musicService.getMusicList(pageQuery, queryVO));
    }

    @Operation(summary = "推荐音乐")
    @GetMapping("/recommend")
    public Result<List<MusicVO>> recommend(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(musicService.getRecommendMusic(limit));
    }

    @Operation(summary = "热门音乐")
    @GetMapping("/hot")
    public Result<List<MusicVO>> hot(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(musicService.getHotMusic(limit));
    }

    @Operation(summary = "增加播放次数")
    @PostMapping("/{id}/play")
    public Result<Boolean> incrementPlay(@PathVariable Long id) {
        return Result.success(musicService.incrementPlayCount(id));
    }

    @Operation(summary = "增加点赞数")
    @PostMapping("/{id}/like")
    public Result<Boolean> incrementLike(@PathVariable Long id) {
        return Result.success(musicService.incrementLikeCount(id));
    }
}
