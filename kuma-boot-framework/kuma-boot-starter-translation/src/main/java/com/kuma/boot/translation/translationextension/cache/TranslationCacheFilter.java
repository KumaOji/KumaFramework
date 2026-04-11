package com.kuma.boot.translation.translationextension.cache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(-2147483647)
@Component
@ConditionalOnWebApplication(
   type = Type.SERVLET
)
public class TranslationCacheFilter extends OncePerRequestFilter {
   public TranslationCacheFilter() {
   }

   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      try {
         filterChain.doFilter(request, response);
         TranslationCacheLocal.put(new TranslationCacheLocal.Cache());
      } finally {
         TranslationCacheLocal.clear();
      }

   }
}
