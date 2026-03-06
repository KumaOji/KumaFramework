package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Source;
import java.util.List;

public interface SourceService {
    Long createSource(Source source);
    boolean updateSource(Source source);
    boolean deleteSource(Long id);
    Source getSourceById(Long id);
    IPage<Source> getSourceList(PageQuery pageQuery);
    List<Source> getAllSources();
}
