package com.taotao.boot.client.webclient.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/remote"})
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
