package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Message;
import com.kuma.cloud.blog.domain.vo.MessageQueryVO;
import com.kuma.cloud.blog.domain.vo.MessageVO;

import java.util.List;

public interface MessageService {
    Long postMessage(Message message, String ip);
    List<MessageVO> getApprovedList();
    IPage<MessageVO> adminList(PageQuery pageQuery, MessageQueryVO queryVO);
    boolean approve(Long id);
    boolean delete(Long id);
    boolean incrementLike(Long id);

    /** 物理删除，仅允许对 status=2 的记录操作 */
    boolean purge(Long id);
}
