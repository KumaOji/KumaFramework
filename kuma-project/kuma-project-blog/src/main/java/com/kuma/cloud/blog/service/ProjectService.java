package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Project;
import com.kuma.cloud.blog.domain.vo.ProjectQueryVO;
import com.kuma.cloud.blog.domain.vo.ProjectVO;

public interface ProjectService {
    Long createProject(Project project);
    boolean updateProject(Project project);
    boolean deleteProject(Long id);
    boolean deleteProjectPhysical(Long id);
    ProjectVO getProjectById(Long id);
    IPage<ProjectVO> getProjectList(PageQuery pageQuery, ProjectQueryVO queryVO);
    boolean incrementViewCount(Long id);
    boolean setFeatured(Long id, boolean featured);
}
