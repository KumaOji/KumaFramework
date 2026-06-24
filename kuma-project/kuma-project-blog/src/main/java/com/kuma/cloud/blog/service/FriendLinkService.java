package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.FriendLink;
import com.kuma.cloud.blog.domain.query.FriendLinkQuery;
import com.kuma.cloud.blog.domain.vo.FriendLinkVO;

import java.util.List;

public interface FriendLinkService {
    Long apply(FriendLink friendLink);
    Long create(FriendLink friendLink);
    boolean update(FriendLink friendLink);
    boolean delete(Long id);
    boolean approve(Long id);
    boolean incrementViewCount(Long id);
    List<FriendLinkVO> getApprovedList();
    IPage<FriendLinkVO> adminList(PageQuery pageQuery, FriendLinkQuery queryVO);
}
