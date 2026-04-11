package com.kuma.boot.webagg.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.BasePageQuery;
import com.kuma.boot.common.model.request.BaseQuery;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.data.mybatis.mybatisplus.MpUtils;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.Wraps;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.query.QueryWrap;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.webagg.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BasePageController<T extends SuperEntity<T, I>, I extends Serializable, QueryDTO, QueryVO> extends BaseController<T, I> {
   @Operation(
      summary = "\u901a\u7528\u5206\u9875\u67e5\u8be2",
      description = "\u901a\u7528\u5206\u9875\u67e5\u8be2"
   )
   @PostMapping({"/page"})
   @RequestLogger("\u901a\u7528\u5206\u9875\u67e5\u8be2")
   default Result<PageResult<QueryVO>> page(@RequestBody @Validated BasePageQuery<QueryDTO> params) {
      IPage<T> page = this.pageQuery(params);
      PageResult<QueryVO> tPageResult = MpUtils.convertMybatisPage(page, this.getQueryVOClass());
      return Result.success(tPageResult);
   }

   default void handlerQueryParams(BasePageQuery<QueryDTO> params) {
   }

   default IPage<T> pageQuery(BasePageQuery<QueryDTO> params) {
      this.handlerQueryParams(params);
      IPage<T> page = MpUtils.buildMpPage(params);
      QueryWrap<T> wrapper = this.handlerWrapper(params.getEqQuery());
      IPage<T> data = this.service().page(page, wrapper);
      this.handlerResult(data);
      return data;
   }

   default QueryWrap<T> handlerWrapper(QueryDTO params) {
      QueryWrap<T> wrapper = Wraps.q(this.getEntityClass());
      if (params instanceof BaseQuery baseQuery) {
         ((List)Optional.ofNullable(baseQuery.eqQuery()).orElse(new ArrayList())).stream().filter(Objects::nonNull).filter((eqDTO) -> StrUtil.isNotBlank(eqDTO.filed())).filter((eqDTO) -> ReflectionUtils.checkField(eqDTO.filed(), this.getEntityClass())).forEach((eqDTO) -> wrapper.eq(StrUtil.toUnderlineCase(eqDTO.filed()), eqDTO.value()));
         ((List)Optional.ofNullable(baseQuery.dateTimeBetweenQuery()).orElse(new ArrayList())).stream().filter(Objects::nonNull).filter((dateTimeBetweenDTO) -> StrUtil.isNotBlank(dateTimeBetweenDTO.filed())).filter((dateTimeBetweenDTO) -> ReflectionUtils.checkField(dateTimeBetweenDTO.getClass(), this.getEntityClass())).forEach((dateTimeBetweenDTO) -> wrapper.between(StrUtil.toUnderlineCase(dateTimeBetweenDTO.filed()), dateTimeBetweenDTO.startTime(), dateTimeBetweenDTO.endTime()));
         ((List)Optional.ofNullable(baseQuery.likeQuery()).orElse(new ArrayList())).stream().filter(Objects::nonNull).filter((likeDTO) -> StrUtil.isNotBlank(likeDTO.filed())).filter((likeDTO) -> ReflectionUtils.checkField(likeDTO.getClass(), this.getEntityClass())).forEach((likeDTO) -> wrapper.like(StrUtil.toUnderlineCase(likeDTO.filed()), likeDTO.value()));
         ((List)Optional.ofNullable(baseQuery.inQuery()).orElse(new ArrayList())).stream().filter(Objects::nonNull).filter((inDTO) -> StrUtil.isNotBlank(inDTO.filed())).filter((inDTO) -> ReflectionUtils.checkField(inDTO.getClass(), this.getEntityClass())).forEach((inDTO) -> wrapper.in(StrUtil.toUnderlineCase(inDTO.filed()), inDTO.values()));
         ((List)Optional.ofNullable(baseQuery.notInQuery()).orElse(new ArrayList())).stream().filter(Objects::nonNull).filter((notInDTO) -> StrUtil.isNotBlank(notInDTO.filed())).filter((notInDTO) -> ReflectionUtils.checkField(notInDTO.getClass(), this.getEntityClass())).forEach((notInDTO) -> wrapper.notIn(StrUtil.toUnderlineCase(notInDTO.filed()), notInDTO.values()));
      }

      wrapper.isNotNull("id");
      return wrapper;
   }

   default void handlerResult(IPage<T> page) {
   }

   Class<QueryVO> getQueryVOClass();
}
