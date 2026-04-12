/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.web.request.annotation.RequestLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.Objects;

/**
 * UpdateController
 *
 * @param <T>         实体
 * @param <I>         id
 * @param <UpdateDTO> 修改参数
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 21:15:51
 */
public interface BaseUpdateController<T extends SuperEntity<T, I>, I extends Serializable, UpdateDTO>
        extends BaseController<T, I> {

   /**
    * 通用单体更新
    *
    * @param id        id
    * @param updateDTO 更新对象
    * @return 更新结果
    * @since 2021-10-11 17:00:12
    */
   @Operation(summary = "通用单体更新", description = "通用单体更新")
   @Parameters({
           @Parameter(name = "id", description = "id", required = true, example = "123", in = ParameterIn.PATH),
   })
   @PostMapping("/{id:[0-9]*}")
   @RequestLogger("通用单体更新")
   // @PreAuthorize("@pms.hasPermission('update')")
   default Result<Boolean> update(
           @NotNull(message = "id不能为空") @PathVariable(value = "id") I id,
           @Validated @RequestBody UpdateDTO updateDTO) {
      if (handlerUpdate(updateDTO)) {
         T t = service().getById(id);
         if (Objects.isNull(t)) {
            throw new BusinessException("未查询到数据");
         }

         if (ReflectionUtils.checkField(updateDTO.getClass(), getEntityClass())) {
            return success(service().updateById(ReflectionUtils.copyPropertiesIfRecord(t, updateDTO)));
         }
      }
      throw new BusinessException("通用单体更新失败");
   }

   /**
    * 自定义更新
    *
    * @param model 更新对象
    * @return 更新结果
    * @since 2021-09-02 21:16:25
    */
   default Boolean handlerUpdate(UpdateDTO model) {
      if (Objects.isNull(model)) {
         throw new BusinessException("更新DTO不能为空");
      }
      return true;
   }
}
