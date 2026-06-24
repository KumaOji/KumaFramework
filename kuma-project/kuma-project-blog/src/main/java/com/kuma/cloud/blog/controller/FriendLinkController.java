package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.entity.FriendLink;
import com.kuma.cloud.blog.domain.query.FriendLinkQuery;
import com.kuma.cloud.blog.domain.vo.FriendLinkVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.FriendLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "友情链接")
@RestController
@RequestMapping("/friend-link")
@RequiredArgsConstructor
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    @Operation(summary = "获取已通过的友链列表（公开）")
    @GetMapping("/list")
    public Result<List<FriendLinkVO>> list() {
        return Result.success(friendLinkService.getApprovedList());
    }

    @Operation(summary = "申请添加友链（公开，提交后待审核）")
    @PostMapping("/apply")
    public Result<Long> apply(@RequestBody FriendLink friendLink) {
        return Result.success(friendLinkService.apply(friendLink));
    }

    @Operation(summary = "记录友链点击（公开）")
    @PostMapping("/{id}/view")
    public Result<Boolean> view(@PathVariable Long id) {
        return Result.success(friendLinkService.incrementViewCount(id));
    }

    @Operation(summary = "管理后台：直接创建友链")
    @PostMapping
    @Authorize(BlogPermissions.FRIEND_LINK_CREATE)
    public Result<Long> create(@RequestBody FriendLink friendLink) {
        return Result.success(friendLinkService.create(friendLink));
    }

    @Operation(summary = "管理后台：更新友链")
    @PutMapping("/{id}")
    @Authorize(BlogPermissions.FRIEND_LINK_UPDATE)
    public Result<Boolean> update(@PathVariable Long id, @RequestBody FriendLink friendLink) {
        friendLink.setId(id);
        return Result.success(friendLinkService.update(friendLink));
    }

    @Operation(summary = "管理后台：删除友链（逻辑）")
    @DeleteMapping("/{id}")
    @Authorize(BlogPermissions.FRIEND_LINK_DELETE)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(friendLinkService.delete(id));
    }

    @Operation(summary = "管理后台：审核通过")
    @PostMapping("/{id}/approve")
    @Authorize(BlogPermissions.FRIEND_LINK_AUDIT)
    public Result<Boolean> approve(@PathVariable Long id) {
        return Result.success(friendLinkService.approve(id));
    }

    @Operation(summary = "管理后台：分页查询友链列表")
    @GetMapping("/admin/list")
    @Authorize(BlogPermissions.FRIEND_LINK_AUDIT)
    public Result<IPage<FriendLinkVO>> adminList(
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer pageSize,
            PageQuery pageQuery,
            FriendLinkQuery queryVO) {
        int current = currentPage != null ? currentPage : (pageQuery != null && pageQuery.getCurrentPage() != null ? pageQuery.getCurrentPage() : 1);
        int size = pageSize != null ? pageSize : (pageQuery != null && pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 20);
        if (pageQuery == null) pageQuery = new PageQuery();
        pageQuery.setCurrentPage(current);
        pageQuery.setPageSize(size);
        return Result.success(friendLinkService.adminList(pageQuery, queryVO));
    }
}
