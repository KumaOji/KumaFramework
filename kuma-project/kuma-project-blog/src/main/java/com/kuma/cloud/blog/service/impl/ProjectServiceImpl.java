package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Project;
import com.kuma.cloud.blog.domain.vo.ProjectQueryVO;
import com.kuma.cloud.blog.domain.vo.ProjectVO;
import com.kuma.cloud.blog.mapper.ProjectMapper;
import com.kuma.cloud.blog.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(Project project) {
        LocalDateTime now = LocalDateTime.now();
        project.setCreateTime(now);
        project.setUpdateTime(now);
        if (project.getViewCount() == null) project.setViewCount(0);
        if (project.getIsFeatured() == null) project.setIsFeatured(0);
        if (project.getSortOrder() == null) project.setSortOrder(0);
        if (project.getStatus() == null) project.setStatus(0);
        if (project.getCategory() == null) project.setCategory(0);
        projectMapper.insert(project);
        return project.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProject(Project project) {
        project.setUpdateTime(LocalDateTime.now());
        return projectMapper.updateById(project) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProject(Long id) {
        return projectMapper.deleteByIdLogic(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProjectPhysical(Long id) {
        return projectMapper.deleteById(id) > 0;
    }

    @Override
    public ProjectVO getProjectById(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) return null;
        return toVO(project);
    }

    @Override
    public IPage<ProjectVO> getProjectList(PageQuery pageQuery, ProjectQueryVO queryVO) {
        QueryWrapper<Project> qw = new QueryWrapper<>();
        if (queryVO == null) queryVO = new ProjectQueryVO();
        qw.ne("status", 2);

        if (StringUtils.isNotEmpty(queryVO.getName())) {
            qw.like("name", queryVO.getName());
        }
        if (queryVO.getCategory() != null) qw.eq("category", queryVO.getCategory());
        if (queryVO.getStatus() != null) qw.eq("status", queryVO.getStatus());
        if (queryVO.getIsFeatured() != null) qw.eq("is_featured", queryVO.getIsFeatured());
        if (StringUtils.isNotEmpty(queryVO.getTech())) qw.like("tech_stack", queryVO.getTech());

        qw.orderByAsc("sort_order").orderByDesc("is_featured").orderByDesc("create_time");

        int current = pageQuery != null && pageQuery.getCurrentPage() != null ? pageQuery.getCurrentPage() : 1;
        int size = pageQuery != null && pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 10;

        IPage<Project> page = projectMapper.selectPage(new Page<>(current, size), qw);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementViewCount(Long id) {
        return projectMapper.incrementViewCount(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setFeatured(Long id, boolean featured) {
        Project project = new Project();
        project.setId(id);
        project.setIsFeatured(featured ? 1 : 0);
        project.setUpdateTime(LocalDateTime.now());
        return projectMapper.updateById(project) > 0;
    }

    private static final String[] CATEGORY_NAMES = {"个人项目", "工作项目", "开源项目", "学习项目"};

    private ProjectVO toVO(Project project) {
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        Integer cat = project.getCategory();
        if (cat != null && cat >= 0 && cat < CATEGORY_NAMES.length) {
            vo.setCategoryName(CATEGORY_NAMES[cat]);
        }
        return vo;
    }
}
