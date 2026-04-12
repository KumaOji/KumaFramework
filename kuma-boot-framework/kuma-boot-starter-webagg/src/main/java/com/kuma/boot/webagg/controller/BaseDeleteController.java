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

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.Wraps;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.query.QueryWrap;
import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.web.request.annotation.RequestLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.Serializable;
import java.util.Objects;

/**
 * DeleteController
 *
 * @param <T> 实体
 * @param <I> id
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 21:05:45
 */
public interface BaseDeleteController<T extends SuperEntity<T, I>, I extends Serializable>
        extends BaseController<T, I> {

    /**
     * 通用根据单体id删除
     *
     * @param id id
     * @return 删除结果
     * @since 2021-09-02 21:06:18
     */
    @Operation(summary = "通用单体id删除", description = "通用单体id删除")
    @Parameters({@Parameter(name = "id", description = "id", required = true, example = "123", in = ParameterIn.PATH)})
    @PostMapping("/{id:[0-9]*}")
    @RequestLogger("通用单体id删除")
    // @PreAuthorize("@pms.hasPermission('delete')")
    default Result<Boolean> deleteById(@NotNull(message = "id不能为空") @PathVariable(value = "id") I id) {
        if (handlerDeleteById(id)) {
            return success(service().removeById(id));
        }

        throw new BusinessException(ResultEnum.FAILED);
    }

    /**
     * 删除
     *
     * @param id id
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     * @since 2021-09-02 21:06:27
     */
    default Boolean handlerDeleteById(I id) {
        if (Objects.isNull(id)) {
            throw new BusinessException("id不能为空");
        }
        return true;
    }

    /**
     * 通用单体字段删除
     *
     * @param filedName 字段名称
     * @param filedValue 字段值
     * @return 单体字段删除结果
     * @since 2021-10-11 15:04:58
     */
    @Operation(summary = "通用单体字段删除", description = "通用单体字段删除")
    @Parameters({
        @Parameter(name = "filedName", description = "字段名称", required = true, example = "123", in = ParameterIn.PATH),
        @Parameter(name = "filedValue", description = "字段值", required = true, example = "123", in = ParameterIn.PATH)
    })
    @PostMapping("/{filedName}/{filedValue}")
    @RequestLogger("通用单体字段删除")
    // @PreAuthorize("@pms.hasPermission('delete')")
    default Result<Boolean> deleteByFiled(
            @NotEmpty(message = "字段名称不能为空") @PathVariable(value = "filedName") String filedName,
            @NotNull(message = "字段值不能为空") @PathVariable(value = "filedValue") Object filedValue) {
        if (handlerDeleteByFiled(filedName, filedValue)) {
            if (ReflectionUtils.checkField(filedName, getEntityClass())) {
                QueryWrap<T> wrapper = Wraps.q();
                wrapper.eq(StrUtil.toUnderlineCase(filedName), filedValue);
                return success(service().remove(wrapper));
            }
        }

        throw new BusinessException(ResultEnum.FAILED);
    }

    /**
     * 通用单体字段删除
     *
     * @param filedName 字段名称
     * @param filedValue 字段值
     * @return 单体字段删除
     * @since 2021-10-11 15:03:39
     */
    default Boolean handlerDeleteByFiled(String filedName, Object filedValue) {
        if (Objects.isNull(filedName) || Objects.isNull(filedValue)) {
            throw new BusinessException("字段数据不能为空");
        }
        return true;
    }
}
