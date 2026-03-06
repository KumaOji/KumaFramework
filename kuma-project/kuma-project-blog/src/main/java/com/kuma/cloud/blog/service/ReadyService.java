package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.ReadyItem;
import com.kuma.cloud.blog.domain.vo.ReadyQueryVO;
import com.kuma.cloud.blog.domain.vo.ReadyVO;

public interface ReadyService {
    Long createReady(ReadyItem item);
    boolean updateReady(ReadyItem item);
    boolean deleteReady(Long id);
    boolean deleteReadyPhysical(Long id);
    boolean toggleStatus(Long id);
    ReadyVO getReadyById(Long id);
    IPage<ReadyVO> getReadyList(PageQuery pageQuery, ReadyQueryVO queryVO);
}
