package com.kuma.boot.data.jpa.bean;

import com.kuma.boot.common.model.BaseSecurityUser;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorBean implements AuditorAware<Long> {
   public AuditorBean() {
   }

   public Optional<Long> getCurrentAuditor() {
      try {
         BaseSecurityUser user = (BaseSecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         return Optional.ofNullable(user.getUserId());
      } catch (Exception var3) {
         return Optional.empty();
      }
   }
}
