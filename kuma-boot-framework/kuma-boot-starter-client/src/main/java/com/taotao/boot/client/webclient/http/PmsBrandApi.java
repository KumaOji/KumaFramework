package com.taotao.boot.client.webclient.http;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface PmsBrandApi {
   @GetExchange("brand/list")
   UmsAdminApi.CommonResult<CommonPage<PmsBrand>> list(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);

   @GetExchange("brand/{id}")
   UmsAdminApi.CommonResult<PmsBrand> detail(@PathVariable("id") Long id);

   @PostExchange("brand/create")
   UmsAdminApi.CommonResult create(@RequestBody PmsBrand pmsBrand);

   @PostExchange("brand/update/{id}")
   UmsAdminApi.CommonResult update(@PathVariable("id") Long id, @RequestBody PmsBrand pmsBrand);

   @GetExchange("brand/delete/{id}")
   UmsAdminApi.CommonResult delete(@PathVariable("id") Long id);

   public static class PmsBrand {
      public PmsBrand() {
      }
   }

   public static class CommonPage<T> {
      public CommonPage() {
      }
   }
}
