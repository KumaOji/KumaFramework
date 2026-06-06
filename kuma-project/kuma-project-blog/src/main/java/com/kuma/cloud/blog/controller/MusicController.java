package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.vo.MusicQueryVO;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import com.kuma.cloud.blog.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "音乐管理")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;
    private final MusicStreamHandler musicStreamHandler;

    @Operation(summary = "创建音乐")
    @PostMapping
    @Authorize(BlogPermissions.MUSIC_UPLOAD)
    public Result<Long> create(@RequestBody Music music) {
        return Result.success(musicService.createMusic(music));
    }

    @Operation(summary = "更新音乐")
    @PutMapping("/{id}")
    @Authorize(BlogPermissions.MUSIC_UPLOAD)
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Music music) {
        music.setId(id);
        return Result.success(musicService.updateMusic(music));
    }

    @Operation(summary = "删除音乐（逻辑）")
    @DeleteMapping("/{id}")
    @Authorize(BlogPermissions.MUSIC_DELETE)
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

    @Operation(summary = "上传音乐文件", description = "支持 mp3/flac/ogg/wav/aac/m4a/opus，返回存储文件名，再通过创建/更新接口写入 filePath")
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Authorize(BlogPermissions.MUSIC_UPLOAD)
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return Result.success(musicService.uploadMusicFile(file));
    }

    @Operation(summary = "删除音乐物理文件（不删除数据库记录）")
    @DeleteMapping("/{id}/file")
    @Authorize(BlogPermissions.MUSIC_DELETE)
    public Result<Boolean> deleteFile(@PathVariable Long id) {
        return Result.success(musicService.deleteMusicFile(id));
    }

    /**
     * 流式播放音乐（支持 Range 请求，实现断点续传）
     */
    @Operation(summary = "播放音乐", description = "流式播放音乐文件，支持 HTTP Range 请求（断点续传）")
    @GetMapping("/{id}/stream")
    public ResponseEntity<StreamingResponseBody> stream(
            @Parameter(description = "音乐ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        Resource resource = musicService.getMusicFile(id);
        return musicStreamHandler.handleStreamRequest(resource, request);
    }
}
