package com.kuma.boot.webagg.controller;

import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.webagg.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseSaveController<T extends SuperEntity<T, I>, I extends Serializable, SaveDTO> extends BaseController<T, I> {
   @Operation(
      summary = "\u901a\u7528\u5355\u4f53\u65b0\u589e",
      description = "\u901a\u7528\u5355\u4f53\u65b0\u589e"
   )
   @PostMapping
   @RequestLogger("\u901a\u7528\u5355\u4f53\u65b0\u589e")
   default Result<Boolean> save(@Validated @RequestBody SaveDTO saveDTO) {
      if (this.handlerSave(saveDTO) && ReflectionUtils.checkField(saveDTO.getClass(), this.getEntityClass())) {
         T t = (T)(ReflectUtil.newInstanceIfPossible(this.getEntityClass()));
         return this.success(this.service().save((SuperEntity)ReflectionUtils.copyPropertiesIfRecord(t, saveDTO)));
      } else {
         throw new BusinessException("\u901a\u7528\u5355\u4f53\u65b0\u589e\u5931\u8d25");
      }
   }

   default Boolean handlerSave(SaveDTO model) {
      if (Objects.isNull(model)) {
         throw new BusinessException("\u65b0\u589eDTO\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         return true;
      }
   }
}
