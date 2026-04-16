package com.kuma.boot.client.webclient.http;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/remote"})
@ConditionalOnBean(UmsAdminApi.class)
public class HttpInterfaceController {
   private final UmsAdminApi umsAdminApi;
   private final PmsBrandApi pmsBrandApi;
   private final TokenHolder tokenHolder;

   public HttpInterfaceController(UmsAdminApi umsAdminApi, PmsBrandApi pmsBrandApi, TokenHolder tokenHolder) {
      this.umsAdminApi = umsAdminApi;
      this.pmsBrandApi = pmsBrandApi;
      this.tokenHolder = tokenHolder;
   }
}
