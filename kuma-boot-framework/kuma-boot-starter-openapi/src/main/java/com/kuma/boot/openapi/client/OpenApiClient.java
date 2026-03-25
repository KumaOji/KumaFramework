package com.kuma.boot.openapi.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.openapi.common.enums.AsymmetricCryEnum;
import com.kuma.boot.openapi.common.enums.CryModeEnum;
import com.kuma.boot.openapi.common.enums.DataType;
import com.kuma.boot.openapi.common.enums.SymmetricCryEnum;
import com.kuma.boot.openapi.common.exception.OpenApiClientException;
import com.kuma.boot.openapi.common.handler.AsymmetricCryHandler;
import com.kuma.boot.openapi.common.handler.SymmetricCryHandler;
import com.kuma.boot.openapi.common.model.Binary;
import com.kuma.boot.openapi.common.model.BinaryParam;
import com.kuma.boot.openapi.common.model.InParams;
import com.kuma.boot.openapi.common.model.OutParams;
import com.kuma.boot.openapi.common.util.Base64Util;
import com.kuma.boot.openapi.common.util.BinaryUtil;
import com.kuma.boot.openapi.common.util.CommonUtil;
import com.kuma.boot.openapi.common.util.CompressUtil;
import com.kuma.boot.openapi.common.util.StrObjectConvert;
import com.kuma.boot.openapi.common.util.SymmetricCryUtil;
import com.kuma.boot.openapi.common.util.TypeUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenApiClient {
   private final String baseUrl;
   private final String selfPrivateKey;
   private final String remotePublicKey;
   private final AsymmetricCryEnum asymmetricCryEnum;
   private final boolean retDecrypt;
   private final CryModeEnum cryModeEnum;
   private final SymmetricCryEnum symmetricCryEnum;
   private final String callerId;
   private final String api;
   private final AsymmetricCryHandler asymmetricCryHandler;
   private final SymmetricCryHandler symmetricCryHandler;
   private final int httpConnectionTimeout;
   private final int httpReadTimeout;
   private final String httpProxyHost;
   private final Integer httpProxyPort;
   private boolean enableCompress;
   private final ThreadLocal logPrefix = new ThreadLocal();

   public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum, boolean retDecrypt, CryModeEnum cryModeEnum, SymmetricCryEnum symmetricCryEnum, String callerId, String api, int httpConnectionTimeout, int httpReadTimeout, String httpProxyHost, Integer httpProxyPort, boolean enableCompress) {
      this.baseUrl = baseUrl;
      this.selfPrivateKey = selfPrivateKey;
      this.remotePublicKey = remotePublicKey;
      this.asymmetricCryEnum = asymmetricCryEnum;
      this.retDecrypt = retDecrypt;
      this.cryModeEnum = cryModeEnum;
      this.symmetricCryEnum = symmetricCryEnum;
      this.callerId = callerId;
      this.api = api;
      this.asymmetricCryHandler = (AsymmetricCryHandler)AsymmetricCryHandler.handlerMap.get(asymmetricCryEnum);
      this.symmetricCryHandler = (SymmetricCryHandler)SymmetricCryHandler.handlerMap.get(symmetricCryEnum);
      this.httpConnectionTimeout = httpConnectionTimeout;
      this.httpReadTimeout = httpReadTimeout;
      this.httpProxyHost = httpProxyHost;
      this.httpProxyPort = httpProxyPort;
      this.enableCompress = enableCompress;
      if (LogUtils.isDebugEnabled()) {
         LogUtils.debug("OpenApiClient init:{}", new Object[]{this});
         this.logCryModel(this.cryModeEnum);
      }

      LogUtils.info("OpenApiClient init succeed. hashcode={}", new Object[]{this.hashCode()});
   }

   public OutParams callOpenApi(InParams inParams) {
      this.checkInParams(inParams.getCallerId(), inParams.getApi(), inParams.getMethod());
      if (StrUtil.isBlank(inParams.getUuid())) {
         inParams.setUuid(IdUtil.simpleUUID());
         this.logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));
      }

      LogUtils.debug("{}入参：{}", new Object[]{this.logPrefix.get(), inParams});
      this.encryptAndSign(inParams);
      OutParams outParams = this.doCall(inParams);
      LogUtils.debug("{}出参：{}", new Object[]{this.logPrefix.get(), outParams});
      return outParams;
   }

   public OutParams callOpenApi(String method, Object... params) {
      this.checkInParams(this.callerId, this.api, method);
      InParams inParams = new InParams();
      inParams.setUuid(IdUtil.simpleUUID());
      inParams.setCallerId(this.callerId);
      inParams.setApi(this.api);
      inParams.setMethod(method);
      this.logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));
      this.setInParamsBody(inParams, params);
      return this.callOpenApi(inParams);
   }

   public OutParams callOpenApi(String api, String method, Object... params) {
      this.checkInParams(this.callerId, api, method);
      InParams inParams = new InParams();
      inParams.setUuid(IdUtil.simpleUUID());
      inParams.setCallerId(this.callerId);
      inParams.setApi(api);
      inParams.setMethod(method);
      this.logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));
      this.setInParamsBody(inParams, params);
      return this.callOpenApi(inParams);
   }

   public OutParams callOpenApi(String callerId, String api, String method, Object... params) {
      this.checkInParams(callerId, api, method);
      InParams inParams = new InParams();
      inParams.setUuid(IdUtil.simpleUUID());
      inParams.setCallerId(callerId);
      inParams.setApi(api);
      inParams.setMethod(method);
      this.logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));
      this.setInParamsBody(inParams, params);
      return this.callOpenApi(inParams);
   }

   private void setInParamsBody(InParams inParams, Object[] params) {
      String body = null;
      byte[] bodyBytes = null;
      boolean multiParam;
      if (params != null && params.length != 0) {
         if (params.length == 1) {
            Object param = params[0];
            Class paramClass = param.getClass();
            if (BinaryUtil.isBinaryParam(param)) {
               BinaryParam binaryParam = this.getBinaryParam(param);
               body = binaryParam.getBinariesStr();
               bodyBytes = BinaryUtil.buildMultiBinaryBytes(binaryParam.getBinaries(), body);
            } else {
               body = StrObjectConvert.objToStr(param, paramClass);
            }

            multiParam = false;
         } else {
            List<String> paramStrList = new ArrayList();
            List<Binary> binaryList = new ArrayList();

            for(Object param : params) {
               if (BinaryUtil.isBinaryParam(param)) {
                  BinaryParam binaryParam = this.getBinaryParam(param);
                  binaryList.addAll(binaryParam.getBinaries());
                  paramStrList.add(binaryParam.getBinariesStr());
               } else {
                  paramStrList.add(StrObjectConvert.objToStr(param, param.getClass()));
               }
            }

            body = JSONUtil.toJsonStr(paramStrList);
            if (CollUtil.isNotEmpty(binaryList)) {
               bodyBytes = BinaryUtil.buildMultiBinaryBytes(binaryList, body);
            }

            multiParam = true;
         }
      } else {
         body = "";
         multiParam = false;
      }

      inParams.setBody(body);
      if (bodyBytes != null) {
         inParams.setBodyBytes(this.enableCompress ? CompressUtil.compress(bodyBytes) : bodyBytes);
         inParams.setDataType(DataType.BINARY);
      } else {
         inParams.setBodyBytes(this.enableCompress ? CompressUtil.compressText(body) : body.getBytes(StandardCharsets.UTF_8));
         inParams.setDataType(DataType.TEXT);
      }

      LogUtils.debug("{}请求体的数据类型为：{}", new Object[]{this.logPrefix.get(), inParams.getDataType()});
      inParams.setMultiParam(multiParam);
   }

   private BinaryParam getBinaryParam(Object obj) {
      BinaryParam binaryParam = new BinaryParam();
      List<Binary> binaryList = new ArrayList();
      if (obj instanceof Binary) {
         binaryList.add((Binary)obj);
         binaryParam.setBinariesStr(BinaryUtil.getBinaryString((Binary)obj));
      } else if (TypeUtil.isBinaryArray(obj.getClass())) {
         Binary[] binaries = (Binary[])obj;
         List<Binary> arrayBinaries = new ArrayList();

         for(Binary binary : binaries) {
            arrayBinaries.add(binary);
            binaryList.add(binary);
         }

         binaryParam.setBinariesStr(BinaryUtil.getBinariesString(arrayBinaries));
      } else if (TypeUtil.isBinaryCollection(obj)) {
         Collection coll = (Collection)obj;
         List<Binary> listBinaries = new ArrayList();

         for(Object element : coll) {
            Binary binary = (Binary)element;
            listBinaries.add(binary);
            binaryList.add(binary);
         }

         binaryParam.setBinariesStr(BinaryUtil.getBinariesString(listBinaries));
      }

      binaryParam.setBinaries(binaryList);
      return binaryParam;
   }

   private void encryptAndSign(InParams inParams) {
      long startTime = System.nanoTime();
      byte[] bodyBytes = inParams.getBodyBytes();
      if (ArrayUtil.isNotEmpty(bodyBytes)) {
         if (this.cryModeEnum == CryModeEnum.SYMMETRIC_CRY) {
            byte[] keyBytes = SymmetricCryUtil.getKey(this.symmetricCryEnum);
            String key = Base64Util.bytesToBase64(keyBytes);
            String cryKey = this.asymmetricCryHandler.cry(this.remotePublicKey, key);
            inParams.setSymmetricCryKey(cryKey);
            bodyBytes = this.symmetricCryHandler.cry(bodyBytes, keyBytes);
         } else if (this.cryModeEnum == CryModeEnum.ASYMMETRIC_CRY) {
            bodyBytes = this.asymmetricCryHandler.cry(this.remotePublicKey, bodyBytes);
         }

         inParams.setBodyBytes(bodyBytes);
      }

      this.logCostTime("加密", startTime);
      startTime = System.nanoTime();
      byte[] signContent = CommonUtil.getSignContent(inParams);
      String sign = this.asymmetricCryHandler.sign(this.selfPrivateKey, signContent);
      inParams.setSign(sign);
      this.logCostTime("加签", startTime);
   }

   private OutParams doCall(InParams inParams) {
      long startTime = System.nanoTime();
      String url = CommonUtil.completeUrl(this.baseUrl, "/openapi/call");
      this.getHeaders(inParams);
      byte[] bodyBytes = inParams.getBodyBytes();
      LogUtils.debug("{}调用openapi入参:{}", new Object[]{this.logPrefix.get(), inParams});
      return null;
   }

   private Map getHeaders(InParams inParams) {
      Map<String, String> headers = new HashMap();
      headers.put("openapi-uuid", inParams.getUuid());
      headers.put("openapi-callerId", inParams.getCallerId());
      headers.put("openapi-api", inParams.getApi());
      headers.put("openapi-method", inParams.getMethod());
      headers.put("openapi-sign", inParams.getSign());
      headers.put("openapi-symmetricCryKey", inParams.getSymmetricCryKey());
      headers.put("openapi-multiParam", String.valueOf(inParams.isMultiParam()));
      headers.put("openapi-dataType", inParams.getDataType().name());
      return headers;
   }

   private void decryptData(OutParams outParams) {
      try {
         long startTime = System.nanoTime();
         byte[] dataBytes = outParams.getDataBytes();
         if (ArrayUtil.isNotEmpty(dataBytes)) {
            if (this.cryModeEnum == CryModeEnum.SYMMETRIC_CRY) {
               String key = this.asymmetricCryHandler.deCry(this.selfPrivateKey, outParams.getSymmetricCryKey());
               byte[] keyBytes = Base64Util.base64ToBytes(key);
               dataBytes = this.symmetricCryHandler.deCry(dataBytes, keyBytes);
            } else if (this.cryModeEnum == CryModeEnum.ASYMMETRIC_CRY) {
               dataBytes = this.asymmetricCryHandler.deCry(this.selfPrivateKey, dataBytes);
            }

            outParams.setDataBytes(dataBytes);
         }

         this.logCostTime("解密", startTime);
      } catch (OpenApiClientException be) {
         String errorMsg = "解密失败：" + be.getMessage();
         LogUtils.error((String)this.logPrefix.get() + errorMsg, new Object[]{be});
         throw new OpenApiClientException(errorMsg);
      } catch (Exception ex) {
         LogUtils.error((String)this.logPrefix.get() + "解密失败", new Object[]{ex});
         throw new OpenApiClientException("解密失败");
      }
   }

   private void checkInParams(String callerId, String api, String method) {
      if (StrUtil.isBlank(callerId)) {
         throw new OpenApiClientException("调用者ID不能为空");
      } else if (StrUtil.isBlank(api)) {
         throw new OpenApiClientException("API接口名不能为空");
      } else if (StrUtil.isBlank(method)) {
         throw new OpenApiClientException("API方法名不能为空");
      }
   }

   private void logCryModel(CryModeEnum cryModeEnum) {
      if (cryModeEnum == CryModeEnum.SYMMETRIC_CRY) {
         LogUtils.debug("采用非对称加密{}+对称加密{}模式", new Object[]{this.asymmetricCryEnum, this.symmetricCryEnum});
      } else if (cryModeEnum == CryModeEnum.ASYMMETRIC_CRY) {
         LogUtils.debug("仅采用非对称加密{}模式", new Object[]{this.asymmetricCryEnum});
      } else if (cryModeEnum == CryModeEnum.NONE) {
         LogUtils.debug("采用不加密模式,签名用的非对称加密{}", new Object[]{this.asymmetricCryEnum});
      }

   }

   private void logCostTime(String operate, long startTime) {
      LogUtils.debug("{}{}耗时:{}ms", new Object[]{this.logPrefix.get(), operate, (System.nanoTime() - startTime) / 1000000L});
   }

   public String toString() {
      return String.format("\nopenApiClient hashCode:%x,\nbaseUrl:%s,\nselfPrivateKey:%s,\nremotePublicKey:%s,\nasymmetricCryEnum:%s,\nretDecrypt:%s;\ncryModeEnum:%s,\nsymmetricCryEnum:%s,\ncallerId:%s,\napi:%s,\nhttpConnectionTimeout:%s,\nhttpReadTimeout:%s,\nenableCompress:%s", this.hashCode(), this.baseUrl, this.selfPrivateKey, this.remotePublicKey, this.asymmetricCryEnum, this.retDecrypt, this.cryModeEnum, this.symmetricCryEnum, this.callerId, this.api, this.httpConnectionTimeout, this.httpReadTimeout, this.enableCompress);
   }
}
