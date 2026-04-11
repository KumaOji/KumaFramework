package com.kuma.boot.webagg.controller;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.Wraps;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.query.QueryWrap;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.webagg.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface BaseDeleteController<T extends SuperEntity<T, I>, I extends Serializable> extends BaseController<T, I> {
   @Operation(
      summary = "\u901a\u7528\u5355\u4f53id\u5220\u9664",
      description = "\u901a\u7528\u5355\u4f53id\u5220\u9664"
   )
   @Parameters({@Parameter(
   name = "id",
   description = "id",
   required = true,
   example = "123",
   in = ParameterIn.PATH
)})
   @PostMapping({"/{id:[0-9]*}"})
   @RequestLogger("\u901a\u7528\u5355\u4f53id\u5220\u9664")
   default Result<Boolean> deleteById(@PathVariable("id") @NotNull(
   message = "id\u4e0d\u80fd\u4e3a\u7a7a"
) I id) {
      if (this.handlerDeleteById(id)) {
         return this.success(this.service().removeById(id));
      } else {
         throw new BusinessException(ResultEnum.FAILED);
      }
   }

   default Boolean handlerDeleteById(I id) {
      if (Objects.isNull(id)) {
         throw new BusinessException("id\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         return true;
      }
   }

   @Operation(
      summary = "\u901a\u7528\u5355\u4f53\u5b57\u6bb5\u5220\u9664",
      description = "\u901a\u7528\u5355\u4f53\u5b57\u6bb5\u5220\u9664"
   )
   @Parameters({@Parameter(
   name = "filedName",
   description = "\u5b57\u6bb5\u540d\u79f0",
   required = true,
   example = "123",
   in = ParameterIn.PATH
), @Parameter(
   name = "filedValue",
   description = "\u5b57\u6bb5\u503c",
   required = true,
   example = "123",
   in = ParameterIn.PATH
)})
   @PostMapping({"/{filedName}/{filedValue}"})
   @RequestLogger("\u901a\u7528\u5355\u4f53\u5b57\u6bb5\u5220\u9664")
   default Result<Boolean> deleteByFiled(@PathVariable("filedName") @NotEmpty(
   message = "\u5b57\u6bb5\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a"
) String filedName, @PathVariable("filedValue") @NotNull(
   message = "\u5b57\u6bb5\u503c\u4e0d\u80fd\u4e3a\u7a7a"
) Object filedValue) {
      if (this.handlerDeleteByFiled(filedName, filedValue) && ReflectionUtils.checkField(filedName, this.getEntityClass())) {
         QueryWrap<T> wrapper = Wraps.q();
         wrapper.eq(StrUtil.toUnderlineCase(filedName), filedValue);
         return this.success(this.service().remove(wrapper));
      } else {
         throw new BusinessException(ResultEnum.FAILED);
      }
   }

   default Boolean handlerDeleteByFiled(String filedName, Object filedValue) {
      if (!Objects.isNull(filedName) && !Objects.isNull(filedValue)) {
         return true;
      } else {
         throw new BusinessException("\u5b57\u6bb5\u6570\u636e\u4e0d\u80fd\u4e3a\u7a7a");
      }
   }
}
