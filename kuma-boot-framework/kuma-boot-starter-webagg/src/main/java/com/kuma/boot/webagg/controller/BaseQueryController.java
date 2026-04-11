package com.kuma.boot.webagg.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.query.QueryWrap;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.webagg.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseQueryController<T extends SuperEntity<T, I>, I extends Serializable, QueryDTO, QueryVO> extends BasePageController<T, I, QueryDTO, QueryVO> {
   @Operation(
      summary = "\u901a\u7528\u5355\u4f53\u67e5\u8be2",
      description = "\u901a\u7528\u5355\u4f53\u67e5\u8be2"
   )
   @Parameters({@Parameter(
   name = "id",
   description = "id",
   required = true,
   example = "123",
   in = ParameterIn.PATH
)})
   @GetMapping({"/{id:[0-9]*}"})
   @RequestLogger("\u901a\u7528\u5355\u4f53\u67e5\u8be2")
   default Result<QueryVO> get(@PathVariable("id") @NotNull(
   message = "id\u4e0d\u80fd\u4e3a\u7a7a"
) I id) {
      T t = (T)(this.service().getById(id));
      if (Objects.isNull(t)) {
         throw new BusinessException("\u672a\u67e5\u8be2\u5230\u6570\u636e");
      } else {
         return this.success(ReflectionUtils.copyPropertiesIfRecord(this.getQueryVOClass(), t));
      }
   }

   @Operation(
      summary = "\u901a\u7528\u6279\u91cf\u67e5\u8be2",
      description = "\u901a\u7528\u6279\u91cf\u67e5\u8be2"
   )
   @PostMapping({"/query"})
   @RequestLogger("\u901a\u7528\u6279\u91cf\u67e5\u8be2")
   default Result<List<QueryVO>> query(@Validated @RequestBody QueryDTO queryDTO) {
      QueryWrap<T> wrapper = this.handlerWrapper(queryDTO);
      List<T> data = this.service().list(wrapper);
      List<QueryVO> result = ((List)Optional.ofNullable(data).orElse(new ArrayList())).stream().filter(Objects::nonNull).map((t) -> ReflectionUtils.copyPropertiesIfRecord(this.getQueryVOClass(), t)).toList();
      return this.success(result);
   }
}
