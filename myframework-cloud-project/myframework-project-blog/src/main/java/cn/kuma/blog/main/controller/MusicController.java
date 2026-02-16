package cn.kuma.blog.main.controller;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import cn.kuma.blog.framework.util.UserDetailUtils;
import cn.kuma.blog.main.domain.VO.MusicQueryVO;
import cn.kuma.blog.main.domain.VO.MusicVO;
import cn.kuma.blog.main.domain.entity.Music;
import cn.kuma.blog.main.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 音乐管理控制器
 *
 * @author Kuma
 * @version 1.0
 */
@Slf4j
@Tag(name = "音乐管理", description = "音乐上传、查询、播放等接口")
@RestController
@RequestMapping("/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 上传音乐文件
     */
    @Operation(summary = "上传音乐", description = "上传音乐文件")
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Long> uploadMusic(
            @Parameter(description = "音乐文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "音乐名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "艺术家/歌手") @RequestParam(value = "artist", required = false) String artist,
            @Parameter(description = "专辑名称") @RequestParam(value = "album", required = false) String album,
            @Parameter(description = "音乐分类/风格") @RequestParam(value = "genre", required = false) String genre,
            @Parameter(description = "封面图片URL") @RequestParam(value = "coverImage", required = false) String coverImage,
            @Parameter(description = "歌词内容") @RequestParam(value = "lyrics", required = false) String lyrics) {
        try {
            // 获取当前用户ID
            Long userId = null;
            try {
                String userIdStr = UserDetailUtils.getUserDetail().getUserID();
                if (StringUtils.hasText(userIdStr)) {
                    userId = Long.parseLong(userIdStr);
                }
            } catch (Exception e) {
                // 如果获取用户信息失败，可以设置为null或抛出异常
            }

            // 构建音乐实体
            Music music = new Music();
            music.setName(name);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setGenre(genre);
            music.setCoverImage(coverImage);
            music.setLyrics(lyrics);

            Long musicId = musicService.uploadMusic(file, music, userId);
            return ApiResult.ok(musicId, "音乐上传成功");
        } catch (Exception e) {
            return ApiResult.failed(500, "音乐上传失败: " + e.getMessage());
        }
    }

    /**
     * 更新音乐信息
     */
    @Operation(summary = "更新音乐信息", description = "更新音乐的基本信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> updateMusic(
            @Parameter(description = "音乐ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "音乐信息", required = true) @RequestBody Music music) {
        try {
            music.setId(id);
            boolean success = musicService.updateMusic(music);
            if (success) {
                return ApiResult.ok(true, "音乐信息更新成功");
            } else {
                return ApiResult.failed(500, "音乐信息更新失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "音乐信息更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除音乐
     */
    @Operation(summary = "删除音乐", description = "根据ID删除音乐（逻辑删除）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteMusic(
            @Parameter(description = "音乐ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = musicService.deleteMusic(id);
            if (success) {
                return ApiResult.ok(true, "音乐删除成功");
            } else {
                return ApiResult.failed(500, "音乐删除失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "音乐删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询音乐
     */
    @Operation(summary = "查询音乐详情", description = "根据ID查询音乐详细信息")
    @GetMapping("/{id}")
    public ApiResult<MusicVO> getMusicById(
            @Parameter(description = "音乐ID", required = true) @PathVariable("id") Long id) {
        try {
            MusicVO music = musicService.getMusicById(id);
            if (music != null) {
                return ApiResult.ok(music);
            } else {
                return ApiResult.failed(SystemResultCode.NOT_FOUND);
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "查询音乐失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询音乐列表
     */
    @Operation(summary = "分页查询音乐列表", description = "根据条件分页查询音乐列表")
    @GetMapping("/list")
    public ApiResult<PageResult<MusicVO>> getMusicList(
            @Parameter(description = "分页参数") PageParam pageParam,
            @Parameter(description = "音乐名称（模糊查询）") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "艺术家（模糊查询）") @RequestParam(value = "artist", required = false) String artist,
            @Parameter(description = "专辑名称（模糊查询）") @RequestParam(value = "album", required = false) String album,
            @Parameter(description = "音乐分类/风格") @RequestParam(value = "genre", required = false) String genre,
            @Parameter(description = "状态：0-待审核，1-已发布，2-已删除") @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "是否推荐：0-否，1-是") @RequestParam(value = "isRecommend", required = false) Integer isRecommend,
            @Parameter(description = "排序字段：playCount-播放次数，likeCount-点赞数，createTime-创建时间") @RequestParam(value = "orderBy", required = false) String orderBy,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(value = "orderDirection", required = false) String orderDirection) {
        try {
            MusicQueryVO queryVO = new MusicQueryVO();
            queryVO.setName(name);
            queryVO.setArtist(artist);
            queryVO.setAlbum(album);
            queryVO.setGenre(genre);
            queryVO.setStatus(status);
            queryVO.setIsRecommend(isRecommend);
            queryVO.setOrderBy(orderBy);
            queryVO.setOrderDirection(orderDirection);

            PageResult<MusicVO> pageResult = musicService.getMusicList(pageParam, queryVO);
            return ApiResult.ok(pageResult);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询音乐列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐音乐列表
     */
    @Operation(summary = "获取推荐音乐", description = "获取推荐音乐列表")
    @GetMapping("/recommend")
    public ApiResult<List<MusicVO>> getRecommendMusic(
            @Parameter(description = "数量限制，默认10") @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        try {
            List<MusicVO> musicList = musicService.getRecommendMusicList(limit);
            return ApiResult.ok(musicList);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询推荐音乐失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门音乐列表
     */
    @Operation(summary = "获取热门音乐", description = "获取热门音乐列表（按播放次数排序）")
    @GetMapping("/hot")
    public ApiResult<List<MusicVO>> getHotMusic(
            @Parameter(description = "数量限制，默认10") @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        try {
            List<MusicVO> musicList = musicService.getHotMusicList(limit);
            return ApiResult.ok(musicList);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询热门音乐失败: " + e.getMessage());
        }
    }

    /**
     * 搜索音乐
     */
    @Operation(summary = "搜索音乐", description = "根据关键词搜索音乐（搜索名称、艺术家、专辑）")
    @GetMapping("/search")
    public ApiResult<List<MusicVO>> searchMusic(
            @Parameter(description = "搜索关键词", required = true) @RequestParam("keyword") String keyword,
            @Parameter(description = "数量限制，默认20") @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        try {
            List<MusicVO> musicList = musicService.searchMusic(keyword, limit);
            return ApiResult.ok(musicList);
        } catch (Exception e) {
            return ApiResult.failed(500, "搜索音乐失败: " + e.getMessage());
        }
    }

    /**
     * 增加播放次数
     */
    @Operation(summary = "增加播放次数", description = "播放音乐时调用此接口增加播放次数")
    @PostMapping("/{id}/play")
    public ApiResult<Boolean> incrementPlayCount(
            @Parameter(description = "音乐ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = musicService.incrementPlayCount(id);
            if (success) {
                return ApiResult.ok(true, "播放次数更新成功");
            } else {
                return ApiResult.failed(500, "播放次数更新失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "播放次数更新失败: " + e.getMessage());
        }
    }

    /**
     * 点赞音乐
     */
    @Operation(summary = "点赞音乐", description = "为音乐点赞")
    @PostMapping("/{id}/like")
    public ApiResult<Boolean> incrementLikeCount(
            @Parameter(description = "音乐ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = musicService.incrementLikeCount(id);
            if (success) {
                return ApiResult.ok(true, "点赞成功");
            } else {
                return ApiResult.failed(500, "点赞失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "点赞失败: " + e.getMessage());
        }
    }

    /**
     * 流式播放音乐（支持Range请求，实现断点续传）
     * 使用 HTTP Range 请求，支持随机跳转、断点续传；通过 StreamingResponseBody 手动拷贝流，
     * 客户端断开时仅结束写入，不向上抛异常。
     */
    @Operation(summary = "播放音乐", description = "流式播放音乐文件，支持HTTP Range请求（断点续传），自动识别文件类型（MP3/FLAC/WAV等）")
    @GetMapping("/{id}/stream")
    public ResponseEntity<StreamingResponseBody> streamMusic(
            @Parameter(description = "音乐ID", required = true) @PathVariable("id") Long id,
            HttpServletRequest request) {
        try {
            Resource resource = musicService.getMusicFile(id);
            if (resource == null || !resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            long fileLength = resource.contentLength();
            if (fileLength <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            String contentType = getContentType(resource);
            String rangeHeader = request.getHeader(HttpHeaders.RANGE);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, contentType);
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.CACHE_CONTROL, "public, max-age=3600");

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                long start = 0;
                long end = fileLength - 1;

                if (ranges.length > 0 && !ranges[0].isEmpty()) {
                    start = Long.parseLong(ranges[0]);
                }
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }

                if (start > end || start < 0 || end >= fileLength) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                            .build();
                }

                long contentLength = end - start + 1;
                headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
                headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);

                Resource rangeResource = new RangeResource(resource, start, end);
                StreamingResponseBody body = outputStream -> copyStream(rangeResource, outputStream);

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(body);
            } else {
                headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength));
                StreamingResponseBody body = outputStream -> copyStream(resource, outputStream);
                return ResponseEntity.ok().headers(headers).body(body);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 将 Resource 拷贝到输出流；任何写入异常均静默结束不向上抛，避免响应已提交后全局处理器再写 ApiResult 触发 HttpMessageNotWritableException。
     */
    private void copyStream(Resource resource, OutputStream outputStream) {
        byte[] buffer = new byte[8192];
        try (InputStream in = resource.getInputStream()) {
            int n;
            while ((n = in.read(buffer)) != -1) {
                try {
                    outputStream.write(buffer, 0, n);
                } catch (IOException e) {
                    if (!isClientDisconnect(e)) {
                        log.warn("流式写入中断: {}", e.getMessage());
                    }
                    return;
                }
            }
        } catch (IOException e) {
            if (!isClientDisconnect(e)) {
                log.warn("流式读取/写入异常: {}", e.getMessage());
            }
            return;
        }
    }

    /**
     * 视为客户端断开、静默处理的异常：连接被对端重置、响应已不可用、或 I/O 被中断（如超时/取消）。
     */
    private static boolean isClientDisconnect(IOException e) {
        if (e == null) {
            return false;
        }
        String msg = e.getMessage();
        if (msg != null && isStreamAbortMessage(msg)) {
            return true;
        }
        Throwable cause = e.getCause();
        if (cause != null) {
            String causeMsg = cause.getMessage();
            if (causeMsg != null && isStreamAbortMessage(causeMsg)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStreamAbortMessage(String msg) {
        return msg.contains("Connection reset by peer")
                || msg.contains("Response not usable after response errors")
                || msg.contains("I/O operation was interrupted")
                || msg.contains("XNIO000808");
    }

    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            return "audio/mpeg"; // 默认MP3
        }

        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".flac")) {
            return "audio/flac";
        } else if (lowerFilename.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (lowerFilename.endsWith(".wav")) {
            return "audio/wav";
        } else if (lowerFilename.endsWith(".ogg")) {
            return "audio/ogg";
        } else if (lowerFilename.endsWith(".m4a")) {
            return "audio/mp4";
        } else if (lowerFilename.endsWith(".aac")) {
            return "audio/aac";
        } else if (lowerFilename.endsWith(".wma")) {
            return "audio/x-ms-wma";
        } else {
            return "audio/mpeg"; // 默认
        }
    }

    /**
     * Range资源包装类，用于支持部分内容传输
     */
    private static class RangeResource implements Resource {
        private final Resource delegate;
        private final long start;
        private final long end;

        public RangeResource(Resource delegate, long start, long end) {
            this.delegate = delegate;
            this.start = start;
            this.end = end;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            InputStream inputStream = delegate.getInputStream();
            // 使用skip方法跳过前面的字节，对于大文件更高效
            long skipped = inputStream.skip(start);
            if (skipped < start) {
                // 如果skip没有跳过足够的字节，使用read方法继续跳过
                long remaining = start - skipped;
                byte[] buffer = new byte[8192];
                while (remaining > 0) {
                    long toRead = Math.min(remaining, buffer.length);
                    long read = inputStream.read(buffer, 0, (int) toRead);
                    if (read == -1) {
                        break;
                    }
                    remaining -= read;
                }
            }
            return new RangeInputStream(inputStream, end - start + 1);
        }

        @Override
        public boolean exists() {
            return delegate.exists();
        }

        @Override
        public boolean isReadable() {
            return delegate.isReadable();
        }

        @Override
        public boolean isOpen() {
            return delegate.isOpen();
        }

        @Override
        public boolean isFile() {
            return delegate.isFile();
        }

        @Override
        public java.net.URL getURL() throws IOException {
            return delegate.getURL();
        }

        @Override
        public java.net.URI getURI() throws IOException {
            return delegate.getURI();
        }

        @Override
        public java.io.File getFile() throws IOException {
            return delegate.getFile();
        }

        @Override
        public long contentLength() throws IOException {
            return end - start + 1;
        }

        @Override
        public long lastModified() throws IOException {
            return delegate.lastModified();
        }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            return delegate.createRelative(relativePath);
        }

        @Override
        public String getFilename() {
            return delegate.getFilename();
        }

        @Override
        public String getDescription() {
            return delegate.getDescription();
        }
    }

    /**
     * Range输入流包装类
     */
    private static class RangeInputStream extends InputStream {
        private final InputStream delegate;
        private final long length;
        private long read = 0;

        public RangeInputStream(InputStream delegate, long length) {
            this.delegate = delegate;
            this.length = length;
        }

        @Override
        public int read() throws IOException {
            if (read >= length) {
                return -1;
            }
            int result = delegate.read();
            if (result != -1) {
                read++;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (read >= length) {
                return -1;
            }
            long remaining = length - read;
            int toRead = (int) Math.min(len, remaining);
            int result = delegate.read(b, off, toRead);
            if (result != -1) {
                read += result;
            }
            return result;
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }
}
