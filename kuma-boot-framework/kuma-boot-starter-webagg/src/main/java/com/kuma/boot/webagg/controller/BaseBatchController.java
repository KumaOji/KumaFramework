package com.kuma.boot.webagg.controller;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.webagg.dto.BatchDTO;
import com.kuma.boot.webagg.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseBatchController<T extends SuperEntity<T, I>, I extends Serializable, SaveDTO, UpdateDTO> extends BaseController<T, I> {
   @Operation(
      summary = "\u901a\u7528\u6279\u91cf\u64cd\u4f5c",
      description = "\u901a\u7528\u6279\u91cf\u64cd\u4f5c"
   )
   @PostMapping({"/batch"})
   @RequestLogger("\u901a\u7528\u6279\u91cf\u64cd\u4f5c")
   @PreAuthorize("@pms.hasPermission('batch')")
   default Result<Boolean> batch(@RequestBody @Validated BatchDTO<SaveDTO, UpdateDTO, I> batchDTO) {
      Result var10000;
      switch (batchDTO.method()) {
         case "create" -> var10000 = this.success(this.batchCreate(batchDTO.batchCreate()));
         case "update" -> var10000 = this.success(this.batchUpdate(batchDTO.batchUpdate()));
         case "delete" -> var10000 = this.success(this.batchDelete(batchDTO.batchDelete()));
         default -> throw new BusinessException("\u64cd\u4f5c\u65b9\u5f0f\u9519\u8bef");
      }

      return var10000;
   }

   default Boolean batchCreate(List<SaveDTO> saveDTOList) {
      if (saveDTOList.isEmpty()) {
         throw new BusinessException("\u6dfb\u52a0\u6570\u636e\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         List<T> entityList = saveDTOList.stream().filter((saveDTO) -> ReflectionUtils.checkField(saveDTO.getClass(), this.getEntityClass())).map((saveDTO) -> {
            T t = (T)(ClassUtils.newInstance(this.getEntityClass()));
            return (SuperEntity)ReflectionUtils.copyPropertiesIfRecord(t, saveDTO);
         }).toList();
         return this.service().saveBatch(entityList);
      }
   }

   default Boolean batchUpdate(List<BatchDTO.BatchUpdate<UpdateDTO, I>> updateDTOList) {
      if (updateDTOList.isEmpty()) {
         throw new BusinessException("\u66f4\u65b0\u6570\u636e\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         Map<I, UpdateDTO> updateDTOMap = new HashMap();
         updateDTOList.forEach((updateDTO) -> {
            I id = updateDTO.id();
            UpdateDTO updateDTO1 = (UpdateDTO)updateDTO.updateDTO();
            updateDTOMap.put(id, updateDTO1);
         });
         List<I> ids = updateDTOList.stream().map(BatchDTO.BatchUpdate::id).toList();
         List<T> ts = this.service().listByIds(ids);
         if (ts.isEmpty()) {
            throw new BusinessException("\u672a\u67e5\u8be2\u5230\u6570\u636e");
         } else {
            List<T> entityList = ts.stream().filter((updateDTO) -> ReflectionUtils.checkField(updateDTO.getClass(), this.getEntityClass())).map((t) -> {
               UpdateDTO updateDTO = (UpdateDTO)updateDTOMap.get(t.getId());
               return (SuperEntity)ReflectionUtils.copyPropertiesIfRecord(t, updateDTO);
            }).toList();
            return this.service().updateBatchById(entityList);
         }
      }
   }

   default Boolean batchDelete(List<I> batchDelete) {
      if (batchDelete.isEmpty()) {
         throw new BusinessException("\u5220\u9664\u6570\u636e\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         return this.service().removeByIds(batchDelete);
      }
   }
}
