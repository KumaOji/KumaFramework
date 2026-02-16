package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.framework.mybatisplus.toolkit.PageUtil;
import cn.kuma.blog.main.domain.VO.ReadyQueryVO;
import cn.kuma.blog.main.domain.VO.ReadyVO;
import cn.kuma.blog.main.domain.entity.ReadyItem;
import cn.kuma.blog.main.mapper.ReadyMapper;
import cn.kuma.blog.main.service.ReadyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 待处理事项服务实现
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class ReadyServiceImpl implements ReadyService {

    @Autowired
    private ReadyMapper readyMapper;

    private static final int STATUS_DELETED = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ReadyItem item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreateTime(now);
        item.setUpdateTime(now);
        if (item.getStatus() == null) {
            item.setStatus(0);
        }
        if (item.getPriority() == null) {
            item.setPriority(0);
        }
        readyMapper.insert(item);
        return item.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(ReadyItem item) {
        item.setUpdateTime(LocalDateTime.now());
        return readyMapper.updateById(item) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return readyMapper.deleteByIdLogic(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePhysical(Long id) {
        return readyMapper.deleteByIdPhysical(id) > 0;
    }

    @Override
    public ReadyVO getById(Long id) {
        ReadyItem entity = readyMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        ReadyVO vo = new ReadyVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReadyVO toggle(Long id) {
        // 查询当前记录
        ReadyItem entity = readyMapper.selectById(id);
        if (entity == null) {
            return null;
        }

        // 如果已删除，不允许切换
        if (entity.getStatus().equals(STATUS_DELETED)) {
            throw new RuntimeException("已删除的记录不能切换状态");
        }

        // 切换状态逻辑：待处理(0)/进行中(1) <-> 已完成(2)
        Integer currentStatus = entity.getStatus();
        Integer newStatus;

        if (currentStatus == null) {
            // 如果状态为空，默认设置为待处理
            newStatus = 0;
        } else if (currentStatus == 0 || currentStatus == 1) {
            // 待处理或进行中 -> 已完成
            newStatus = 2;
        } else if (currentStatus == 2) {
            // 已完成 -> 待处理
            newStatus = 0;
        } else {
            // 其他状态（如已删除）不允许切换
            throw new RuntimeException("当前状态不支持切换");
        }

        // 更新状态
        entity.setStatus(newStatus);
        entity.setUpdateTime(LocalDateTime.now());
        int updateCount = readyMapper.updateById(entity);

        if (updateCount > 0) {
            // 转换为 VO 返回
            ReadyVO vo = new ReadyVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }

        return null;
    }

    @Override
    public PageResult<ReadyVO> list(PageParam pageParam, ReadyQueryVO queryVO) {
        QueryWrapper<ReadyItem> qw = new QueryWrapper<>();
        qw.ne("status", STATUS_DELETED);

        Optional.ofNullable(queryVO)
                .flatMap(q -> Optional.ofNullable(q.getTitle()))
                .filter(StringUtils::isNotEmpty)
                .ifPresent(t -> qw.like("title", t));

        Optional.ofNullable(queryVO).ifPresent(q -> {
            if (q.getStatus() != null) {
                qw.eq("status", q.getStatus());
            }
            if (q.getPriority() != null) {
                qw.eq("priority", q.getPriority());
            }
        });

        qw.orderByDesc("priority").orderByDesc("create_time");

        IPage<ReadyItem> page = readyMapper.selectPage(PageUtil.prodPage(pageParam), qw);
        List<ReadyVO> records = page.getRecords().stream()
                .map(e -> {
                    ReadyVO v = new ReadyVO();
                    BeanUtils.copyProperties(e, v);
                    return v;
                })
                .collect(Collectors.toList());

        PageResult<ReadyVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotal());
        return result;
    }
}
