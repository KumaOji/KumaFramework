package com.kuma.boot.webagg.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.webagg.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseUpdateController<T extends SuperEntity<T, I>, I extends Serializable, UpdateDTO> extends BaseController<T, I> {
   @Operation(
      summary = "\u901a\u7528\u5355\u4f53\u66f4\u65b0",
      description = "\u901a\u7528\u5355\u4f53\u66f4\u65b0"
   )
   @Parameters({@Parameter(
   name = "id",
   description = "id",
   required = true,
   example = "123",
   in = ParameterIn.PATH
)})
   @PostMapping({"/{id:[0-9]*}"})
   @RequestLogger("\u901a\u7528\u5355\u4f53\u66f4\u65b0")
   default Result<Boolean> update(@PathVariable("id") @NotNull(
   message = "id\u4e0d\u80fd\u4e3a\u7a7a"
) I id, @Validated @RequestBody UpdateDTO updateDTO) {
      if (this.handlerUpdate(updateDTO)) {
         T t = (T)(this.service().getById(id));
         if (Objects.isNull(t)) {
            throw new BusinessException("\u672a\u67e5\u8be2\u5230\u6570\u636e");
         }

         if (ReflectionUtils.checkField(updateDTO.getClass(), this.getEntityClass())) {
            return this.success(this.service().updateById((SuperEntity)ReflectionUtils.copyPropertiesIfRecord(t, updateDTO)));
         }
      }

      throw new BusinessException("\u901a\u7528\u5355\u4f53\u66f4\u65b0\u5931\u8d25");
   }

   default Boolean handlerUpdate(UpdateDTO model) {
      if (Objects.isNull(model)) {
         throw new BusinessException("\u66f4\u65b0DTO\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         return true;
      }
   }
}
