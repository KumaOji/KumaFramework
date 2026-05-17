/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.webagg.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.query.QueryWrap;
import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.web.request.annotation.RequestLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * QueryController
 *
 * @param <T> 实体
 * @param <I> id
 * @param <QueryDTO> 查询参数
 * @param <QueryVO> 查询返回参数
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:11:18
 */
public interface BaseQueryController<T extends SuperEntity<T, I>, I extends Serializable, QueryDTO, QueryVO>
        extends BasePageController<T, I, QueryDTO, QueryVO> {

   /**
    * 通用单体查询
    *
    * @param id 主键id
    * @return 单体查询对象
    * @since 2021-09-02 21:11:48
    */
   @Operation(summary = "通用单体查询", description = "通用单体查询")
   @Parameters({
           @Parameter(name = "id", description = "id", required = true, example = "123", in = ParameterIn.PATH),
   })
   @GetMapping("/{id:[0-9]*}")
   @RequestLogger("通用单体查询")
   // @PreAuthorize("@pms.hasPermission('get')")
   default Result<QueryVO> get(@NotNull(message = "id不能为空") @PathVariable(value = "id") I id) {
      T t = service().getById(id);
      if (Objects.isNull(t)) {
         throw new BusinessException("未查询到数据");
      }

      return success(ReflectionUtils.copyPropertiesIfRecord(getQueryVOClass(), t));
   }

   /**
    * 通用批量查询
    *
    * @param queryDTO 通用批量查询参数
    * @return 批量查询结果
    * @since 2021-09-02 21:12:04
    */
   @Operation(summary = "通用批量查询", description = "通用批量查询")
   @PostMapping("/query")
   @RequestLogger("通用批量查询")
   // @PreAuthorize("@pms.hasPermission('query')")
   default Result<List<QueryVO>> query(@Validated @RequestBody QueryDTO queryDTO) {
      QueryWrap<T> wrapper = handlerWrapper(queryDTO);
      List<T> data = service().list(wrapper);
      List<QueryVO> result = Optional.ofNullable(data).orElse(new ArrayList<>()).stream()
              .filter(Objects::nonNull)
              .map(t -> ReflectionUtils.copyPropertiesIfRecord(getQueryVOClass(), t))
              .toList();
      return success(result);
   }
}
