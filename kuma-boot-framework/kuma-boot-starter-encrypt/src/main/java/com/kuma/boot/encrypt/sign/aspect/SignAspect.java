package com.kuma.boot.encrypt.sign.aspect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.sign.autoconfigure.SignProperties;
import com.kuma.boot.encrypt.sign.exception.SignDtguaiException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Aspect
@Order(1)
public class SignAspect {
   private final SignProperties signProperties;
   public static final String TOKEN_HEADER = "token";
   public static final String SIGN_HEADER = "sign";
   public static final String DATA_SECRET_HEADER = "dataSecret";

   public SignAspect(SignProperties signProperties) {
      this.signProperties = signProperties;
   }

   @Pointcut("@annotation(com.kuma.boot.encrypt.sign.annotation.Sign)")
   public void signPointCut() {
   }

   @Around("signPointCut()")
   public Object around(ProceedingJoinPoint point) throws Throwable {
      Object[] args = point.getArgs();
      TreeMap<String, Object> reqm = (TreeMap)Optional.ofNullable(args[0]).map((x) -> JSON.toJSONString(x, "yyyy-MM-dd HH:mm:ss", new JSONWriter.Feature[0])).map((x) -> (TreeMap)JSON.parseObject(x, TreeMap.class)).orElseThrow(() -> new SignDtguaiException("sing注解中加密数据为空"));
      String timestamp = (String)Optional.ofNullable(reqm.get("timestamp")).map(Object::toString).orElseThrow(() -> new SignDtguaiException("数字证书timestamp不能为空"));
      LogUtils.info("sign的TreeMap默认key升序排序timestamp:{} ---- json:{}", new Object[]{timestamp, JSON.toJSONString(reqm)});
      Optional.of(reqm).ifPresent(this::validSign);
      return point.proceed();
   }

   private void validSign(Map reqm) {
      StringBuilder paramBuilder = new StringBuilder();

      String md5Sign;
      String sign;
      try {
         reqm = (Map)Optional.ofNullable(reqm).orElseThrow(() -> new SignDtguaiException("sign的map不能为空"));
         sign = (String)Optional.ofNullable(reqm).map((x) -> x.get("sign")).map(Object::toString).orElseThrow(() -> new SignDtguaiException("sign不能为空"));
         reqm.forEach((k, v) -> {
            List<String> ignore = this.signProperties.getIgnore();
            if (v != null && !ignore.contains(k)) {
               paramBuilder.append(k).append("=").append(v).append("&");
            }

         });
         String dataSing = paramBuilder.append("signKey=").append(this.signProperties.getKey()).toString();
         LogUtils.info("sing之前的拼装数据:{}", new Object[]{dataSing});
         md5Sign = DigestUtils.md5Hex(dataSing);
      } catch (Exception e) {
         LogUtils.error("sign数据签名校验出错{}", new Object[]{reqm, e});
         throw new SignDtguaiException("sign数据签名校验出错");
      }

      if (!md5Sign.equals(sign)) {
         LogUtils.error("验证失败:{}  传入的sign:{}  当前生成的md5Sign:{}", new Object[]{paramBuilder, sign, md5Sign});
         throw new SignDtguaiException("数字证书校验失败");
      }
   }
}
