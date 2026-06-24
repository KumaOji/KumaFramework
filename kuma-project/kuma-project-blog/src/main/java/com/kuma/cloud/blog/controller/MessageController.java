package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.dto.MessageSaveDTO;
import com.kuma.cloud.blog.domain.entity.Message;
import com.kuma.cloud.blog.domain.query.MessageQuery;
import com.kuma.cloud.blog.domain.vo.MessageVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "留言板")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "发表留言或回复")
    @PostMapping
    public Result<Long> post(@Valid @RequestBody MessageSaveDTO dto, HttpServletRequest request) {
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        return Result.success(messageService.postMessage(message, resolveClientIp(request)));
    }

    @Operation(summary = "获取留言列表（含回复，仅已审核）")
    @GetMapping("/list")
    public Result<List<MessageVO>> list() {
        return Result.success(messageService.getApprovedList());
    }

    @Operation(summary = "点赞留言")
    @PostMapping("/{id}/like")
    public Result<Boolean> like(@PathVariable Long id) {
        return Result.success(messageService.incrementLike(id));
    }

    @Operation(summary = "管理后台：分页查询留言（支持按昵称/状态过滤）")
    @GetMapping("/admin/list")
    @Authorize(BlogPermissions.MESSAGE_AUDIT)
    public Result<IPage<MessageVO>> adminList(PageQuery pageQuery, MessageQuery query) {
        if (pageQuery.getCurrentPage() == null) pageQuery.setCurrentPage(1);
        if (pageQuery.getPageSize() == null) pageQuery.setPageSize(20);
        return Result.success(messageService.adminList(pageQuery, query));
    }

    @Operation(summary = "审核通过")
    @PostMapping("/{id}/approve")
    @Authorize(BlogPermissions.MESSAGE_AUDIT)
    public Result<Boolean> approve(@PathVariable Long id) {
        return Result.success(messageService.approve(id));
    }

    @Operation(summary = "删除留言（逻辑，status=2）")
    @DeleteMapping("/{id}")
    @Authorize(BlogPermissions.MESSAGE_DELETE)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(messageService.delete(id));
    }

    @Operation(summary = "物理删除留言（仅限 status=2 已删除的记录）")
    @DeleteMapping("/{id}/purge")
    @Authorize(BlogPermissions.MESSAGE_DELETE)
    public Result<Boolean> purge(@PathVariable Long id) {
        return Result.success(messageService.purge(id));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
