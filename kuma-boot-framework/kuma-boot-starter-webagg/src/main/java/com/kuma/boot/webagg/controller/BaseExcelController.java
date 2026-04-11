package com.kuma.boot.webagg.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.ExcelXorHtmlUtil;
import cn.afterturn.easypoi.excel.entity.ExcelToHtmlParams;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.request.BaseQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.query.conditions.query.QueryWrap;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.webagg.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface BaseExcelController<T extends SuperEntity<T, I>, I extends Serializable, QueryDTO, QueryVO> extends BasePageController<T, I, QueryDTO, QueryVO> {
   @Operation(
      summary = "\u901a\u7528\u5bfc\u51faExcel",
      description = "\u901a\u7528\u5bfc\u51faExcel"
   )
   @PostMapping(
      value = {"/excel/export"},
      produces = {"application/octet-stream"}
   )
   @RequestLogger("'\u5bfc\u51faExcel:'.concat([fileName]?:'')")
   default void export(@Validated @RequestBody QueryDTO params, HttpServletRequest request, HttpServletResponse response) {
      ExportParams exportParams = this.getExportParams(params);
      List<T> list = this.findExportList(params);
      Map<String, Object> map = new HashMap(7);
      if (params instanceof BaseQuery baseQuery) {
         String fileName = baseQuery.execlQuery().fileName();
         map.put("fileName", fileName);
      }

      map.put("data", list);
      map.put("entity", this.getExcelClass());
      map.put("params", exportParams);
   }

   @Operation(
      summary = "\u901a\u7528\u9884\u89c8Excel",
      description = "\u901a\u7528\u9884\u89c8Excel"
   )
   @PostMapping({"/excel/preview"})
   @RequestLogger("'\u901a\u7528\u9884\u89c8Excel:' + ([fileName]?:'')")
   default Result<String> preview(@Validated @RequestBody QueryDTO params) {
      ExportParams exportParams = this.getExportParams(params);
      List<T> list = this.findExportList(params);
      Workbook workbook = ExcelExportUtil.exportExcel(exportParams, this.getExcelClass(), list);
      return this.success(ExcelXorHtmlUtil.excelToHtml(new ExcelToHtmlParams(workbook)));
   }

   @Operation(
      summary = "\u901a\u7528\u5bfc\u5165Excel",
      description = "\u901a\u7528\u5bfc\u5165Excel"
   )
   @Parameters({@Parameter(
   name = "file",
   required = true,
   description = "\u6587\u4ef6\u4fe1\u606f",
   schema = @Schema(
   type = "file"
)
)})
   @PostMapping(
      value = {"/excel/import"},
      headers = {"content-type=multipart/form-data"}
   )
   @RequestLogger("\u901a\u7528\u5bfc\u5165Excel")
   default Result<Boolean> importExcel(@RequestPart("file") @NotNull(
   message = "\u6587\u4ef6\u4e0d\u80fd\u4e3a\u7a7a"
) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
      ImportParams params = new ImportParams();
      params.setTitleRows(StrUtil.isEmpty(request.getParameter("titleRows")) ? 0 : Convert.toInt(request.getParameter("titleRows")));
      params.setHeadRows(StrUtil.isEmpty(request.getParameter("headRows")) ? 1 : Convert.toInt(request.getParameter("headRows")));

      try {
         List<Map<String, String>> list = ExcelImportUtil.importExcel(file.getInputStream(), Map.class, params);
         if (list != null && !list.isEmpty()) {
            return this.success(this.handlerImport(list));
         }
      } catch (Exception e) {
         LogUtils.error(e);
      }

      throw new BusinessException("\u5bfc\u5165Excel\u5931\u8d25");
   }

   default Boolean handlerImport(List<Map<String, String>> list) {
      return true;
   }

   default ExportParams getExportParams(QueryDTO params) {
      String title = "title";
      String type = "HSSF";
      String sheetName = "sheetName";
      if (params instanceof BaseQuery baseQuery) {
         if (Objects.isNull(baseQuery.execlQuery())) {
            throw new BusinessException("execl\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a");
         }

         title = baseQuery.execlQuery().title();
         type = baseQuery.execlQuery().type();
         sheetName = baseQuery.execlQuery().sheetName();
      }

      ExcelType excelType = ExcelType.XSSF.name().equals(type) ? ExcelType.XSSF : ExcelType.HSSF;
      ExportParams ep = new ExportParams(title, sheetName, excelType);
      this.enhanceExportParams(ep);
      return ep;
   }

   default void enhanceExportParams(ExportParams params) {
   }

   default List<T> findExportList(QueryDTO params) {
      QueryWrap<T> tQueryWrap = this.handlerWrapper(params);
      return this.service().list(tQueryWrap);
   }

   default Class<T> getExcelClass() {
      return this.getEntityClass();
   }
}
