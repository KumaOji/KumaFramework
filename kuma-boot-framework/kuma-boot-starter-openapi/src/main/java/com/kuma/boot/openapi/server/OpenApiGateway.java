package com.kuma.boot.openapi.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.openapi.common.enums.AsymmetricCryEnum;
import com.kuma.boot.openapi.common.enums.CryModeEnum;
import com.kuma.boot.openapi.common.enums.DataType;
import com.kuma.boot.openapi.common.enums.SymmetricCryEnum;
import com.kuma.boot.openapi.common.exception.OpenApiServerException;
import com.kuma.boot.openapi.common.handler.AsymmetricCryHandler;
import com.kuma.boot.openapi.common.handler.SymmetricCryHandler;
import com.kuma.boot.openapi.common.model.Binary;
import com.kuma.boot.openapi.common.model.InParams;
import com.kuma.boot.openapi.common.model.OutParams;
import com.kuma.boot.openapi.common.util.Base64Util;
import com.kuma.boot.openapi.common.util.BinaryUtil;
import com.kuma.boot.openapi.common.util.CommonUtil;
import com.kuma.boot.openapi.common.util.CompressUtil;
import com.kuma.boot.openapi.common.util.StrObjectConvert;
import com.kuma.boot.openapi.common.util.SymmetricCryUtil;
import com.kuma.boot.openapi.common.util.TruncateUtil;
import com.kuma.boot.openapi.common.util.TypeUtil;
import com.kuma.boot.openapi.server.annotation.OpenApi;
import com.kuma.boot.openapi.server.annotation.OpenApiMethod;
import com.kuma.boot.openapi.server.config.OpenApiServerConfig;
import com.kuma.boot.openapi.server.model.ApiHandler;
import com.kuma.boot.openapi.server.model.Context;
import com.kuma.boot.openapi.server.model.OpenApiRequest;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenApiGateway {
   private final Map apiHandlerMap = new HashMap();
   private final Context context;
   private final ApplicationContext applicationContext;
   private final OpenApiServerConfig config;
   private final ThreadLocal logPrefix = new ThreadLocal();
   private AsymmetricCryEnum asymmetricCryEnum;
   private String selfPrivateKey;
   private boolean retEncrypt;
   private CryModeEnum cryModeEnum;
   private SymmetricCryEnum symmetricCryEnum;
   private boolean enableCompress;
   private AsymmetricCryHandler asymmetricCryHandler;
   private SymmetricCryHandler symmetricCryHandler;

   public OpenApiGateway(Context context, ApplicationContext applicationContext, OpenApiServerConfig config) {
      this.context = context;
      this.applicationContext = applicationContext;
      this.config = config;
   }

   @PostConstruct
   public void init() {
      this.initConfig();
      this.initApiHandlers();
      if (LogUtils.isDebugEnabled()) {
         String handlersStr = this.getHandlersStr();
         LogUtils.debug("OpenApiGateway Init: \nSelfPrivateKey:{},\nAsymmetricCry:{},\nretEncrypt:{},\ncryModeEnum:{},\nSymmetricCry:{},\nenableCompress:{},\nApiHandlers:\n{}", new Object[]{this.selfPrivateKey, this.asymmetricCryEnum, this.retEncrypt, this.cryModeEnum, this.symmetricCryEnum, this.enableCompress, handlersStr});
         this.logCryMode(this.cryModeEnum, (OpenApiMethod)null);
         this.logEnableCompress(this.enableCompress, (OpenApiMethod)null);
      }

      LogUtils.info("OpenApiGateway init succeed. path={}", new Object[]{"/openapi/call"});
   }

   private String getHandlersStr() {
      Collection<ApiHandler> handlers = this.apiHandlerMap.values();
      StringBuilder handlersStr = new StringBuilder("");
      if (CollUtil.isEmpty(handlers)) {
         handlersStr = new StringBuilder("未找到ApiHandler,请确保注解@OpenApi所声明的bean可被spring扫描到");
      } else {
         for(ApiHandler handler : handlers) {
            handlersStr.append(handler).append("\n");
         }
      }

      return handlersStr.toString();
   }

   private void initConfig() {
      if (this.config == null) {
         throw new RuntimeException("请注册OpenApiServerConfig一个bean");
      } else {
         this.selfPrivateKey = this.config.getSelfPrivateKey();
         this.asymmetricCryEnum = this.config.getAsymmetricCry();
         this.retEncrypt = this.config.retEncrypt();
         this.cryModeEnum = this.config.getCryMode();
         this.symmetricCryEnum = this.config.getSymmetricCry();
         this.enableCompress = this.config.enableCompress();
         this.asymmetricCryHandler = (AsymmetricCryHandler)AsymmetricCryHandler.handlerMap.get(this.asymmetricCryEnum);
         this.symmetricCryHandler = (SymmetricCryHandler)SymmetricCryHandler.handlerMap.get(this.symmetricCryEnum);
      }
   }

   private void initApiHandlers() {
      Map<String, Object> beanMap = this.applicationContext.getBeansWithAnnotation(OpenApi.class);

      for(Map.Entry entry : beanMap.entrySet()) {
         String beanName = (String)entry.getKey();
         Object bean = entry.getValue();
         Class<?> c = bean.getClass();
         OpenApi openApi = (OpenApi)c.getAnnotation(OpenApi.class);
         String openApiName = openApi.value();
         Method[] methods = c.getDeclaredMethods();
         if (ArrayUtil.isNotEmpty(methods)) {
            for(Method method : methods) {
               if (method.isAnnotationPresent(OpenApiMethod.class)) {
                  OpenApiMethod openApiMethod = (OpenApiMethod)method.getAnnotation(OpenApiMethod.class);
                  String openApiMethodName = openApiMethod.value();
                  Type[] types = method.getGenericParameterTypes();
                  Parameter[] parameters = method.getParameters();
                  String handlerKey = this.getHandlerKey(openApiName, openApiMethodName);
                  ApiHandler apiHandler = new ApiHandler();
                  apiHandler.setOpenApiName(openApiName);
                  apiHandler.setOpenApiMethodName(openApiMethodName);
                  apiHandler.setBeanName(beanName);
                  apiHandler.setBean(bean);
                  apiHandler.setMethod(method);
                  apiHandler.setParamTypes(types);
                  apiHandler.setParameters(parameters);
                  apiHandler.setOpenApiMethod(openApiMethod);
                  this.apiHandlerMap.put(handlerKey, apiHandler);
               }
            }
         }
      }

      this.context.setApiHandlers(this.apiHandlerMap.values());
   }

   @PostMapping(
      value = {"/openapi/call"},
      consumes = {"application/octet-stream"},
      produces = {"application/octet-stream"}
   )
   public void callMethod(HttpServletRequest request, HttpServletResponse response) {
      OutParams outParams = null;
      InParams inParams = null;

      try {
         inParams = this.getInParams(request);
         LogUtils.debug("{}接收到请求：{}", new Object[]{this.logPrefix.get(), inParams});
         LogUtils.debug("{}请求体的数据类型为：{}", new Object[]{this.logPrefix.get(), inParams.getDataType()});
         ApiHandler apiHandler = this.getApiHandler(inParams);
         List<Object> params = this.getParam(inParams, apiHandler);
         outParams = this.doCall(apiHandler, params, inParams);
      } catch (OpenApiServerException be) {
         String var10000 = (String)this.logPrefix.get();
         LogUtils.error(var10000 + be.getMessage(), new Object[0]);
         outParams = OutParams.error(be.getMessage());
      } catch (Exception ex) {
         LogUtils.error((String)this.logPrefix.get() + "系统异常：", new Object[]{ex});
         outParams = OutParams.error("系统异常");
      } finally {
         if (outParams == null) {
            outParams = OutParams.error("系统异常");
         }

         outParams.setUuid(inParams != null ? inParams.getUuid() : null);
         if (outParams.getDataType() == null) {
            outParams.setDataType(DataType.TEXT);
         }

         LogUtils.debug("{}响应体的数据类型为：{}", new Object[]{this.logPrefix.get(), outParams.getDataType()});
         this.writeOutParams(response, outParams);
         LogUtils.debug("{}调用完毕：{}", new Object[]{this.logPrefix.get(), outParams});
      }

   }

   private InParams getInParams(HttpServletRequest request) {
      InParams inParams = new InParams();

      try {
         inParams.setUuid(request.getHeader("openapi-uuid"));
         this.logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));
         inParams.setCallerId(request.getHeader("openapi-callerId"));
         inParams.setApi(request.getHeader("openapi-api"));
         inParams.setMethod(request.getHeader("openapi-method"));
         inParams.setSign(request.getHeader("openapi-sign"));
         inParams.setSymmetricCryKey(request.getHeader("openapi-symmetricCryKey"));
         inParams.setMultiParam(Boolean.parseBoolean(request.getHeader("openapi-multiParam")));
         inParams.setDataType((DataType)Enum.valueOf(DataType.class, request.getHeader("openapi-dataType")));
         InputStream inputStream = request.getInputStream();
         byte[] inputBytes = IoUtil.readBytes(inputStream);
         inParams.setBodyBytes(inputBytes);
         return inParams;
      } catch (Exception ex) {
         LogUtils.error((String)this.logPrefix.get() + "从请求流读取数据异常", new Object[]{ex});
         throw new OpenApiServerException("从请求流读取数据异常:" + ex.getMessage());
      }
   }

   private void writeOutParams(HttpServletResponse response, OutParams outParams) {
      try {
         response.setContentType("application/octet-stream");
         response.addHeader("openapi-uuid", outParams.getUuid());
         response.addHeader("openapi-code", String.valueOf(outParams.getCode()));
         response.addHeader("openapi-message", Base64Util.strToBase64(outParams.getMessage()));
         response.addHeader("openapi-symmetricCryKey", outParams.getSymmetricCryKey());
         response.addHeader("openapi-dataType", outParams.getDataType().name());
         if (ArrayUtil.isEmpty(outParams.getDataBytes())) {
            return;
         }

         IoUtil.write(response.getOutputStream(), true, outParams.getDataBytes());
      } catch (Exception ex) {
         LogUtils.error((String)this.logPrefix.get() + "写返回值到响应流异常", new Object[]{ex});
      }

   }

   private ApiHandler getApiHandler(InParams inParams) {
      String handlerKey = this.getHandlerKey(inParams.getApi(), inParams.getMethod());
      ApiHandler apiHandler = (ApiHandler)this.apiHandlerMap.get(handlerKey);
      if (apiHandler == null) {
         throw new OpenApiServerException("找不到指定的openapi处理器");
      } else {
         return apiHandler;
      }
   }

   private List getParam(InParams inParams, ApiHandler apiHandler) {
      this.verifySign(inParams);
      if (ArrayUtil.isEmpty(inParams.getBodyBytes())) {
         return new ArrayList();
      } else {
         byte[] bodyBytes = this.decryptBody(inParams, apiHandler);

         try {
            Type[] paramTypes = apiHandler.getParamTypes();
            List<Type> paramTypeList = this.getNormalParamTypeList(paramTypes);
            int paramLength = 0;
            long binaryLengthStartIndex = 0L;
            String paramStr;
            if (inParams.getDataType() == DataType.BINARY) {
               bodyBytes = this.isEnableCompress(apiHandler) ? CompressUtil.decompress(bodyBytes) : bodyBytes;
               paramLength = BinaryUtil.getParamLength(bodyBytes);
               binaryLengthStartIndex = BinaryUtil.getBinaryLengthStartIndex(paramLength);
               paramStr = BinaryUtil.getParamStr(bodyBytes, paramLength);
            } else {
               paramStr = this.isEnableCompress(apiHandler) ? CompressUtil.decompressToText(bodyBytes) : new String(bodyBytes, StandardCharsets.UTF_8);
            }

            List<Object> normalParams = new ArrayList();
            if (inParams.isMultiParam()) {
               List<String> list = JSONUtil.toList(paramStr, String.class);
               if (list.size() != paramTypeList.size()) {
                  throw new OpenApiServerException("参数个数不匹配");
               }

               for(int i = 0; i < list.size(); ++i) {
                  Object obj = StrObjectConvert.strToObj((String)list.get(i), (Type)paramTypeList.get(i));
                  if (BinaryUtil.isBinaryParam(obj)) {
                     binaryLengthStartIndex = this.fillBinaryParam(obj, bodyBytes, binaryLengthStartIndex);
                  }

                  normalParams.add(obj);
               }
            } else if (paramTypeList.size() == 1) {
               Type paramType = (Type)paramTypeList.get(0);
               Object obj = StrObjectConvert.strToObj(paramStr, paramType);
               if (BinaryUtil.isBinaryParam(obj)) {
                  this.fillBinaryParam(obj, bodyBytes, binaryLengthStartIndex);
               }

               normalParams.add(obj);
            }

            return this.getAllParams(paramTypes, normalParams, inParams);
         } catch (OpenApiServerException be) {
            throw new OpenApiServerException("入参转换异常：" + be.getMessage());
         } catch (Exception ex) {
            LogUtils.error((String)this.logPrefix.get() + "入参转换异常", new Object[]{ex});
            throw new OpenApiServerException("入参转换异常:" + ex.getMessage());
         }
      }
   }

   private List getNormalParamTypeList(Type[] paramTypes) {
      List<Type> paramTypeList = new ArrayList();

      for(Type type : paramTypes) {
         if (!type.getTypeName().equals(OpenApiRequest.class.getName())) {
            paramTypeList.add(type);
         }
      }

      return paramTypeList;
   }

   private List getAllParams(Type[] paramTypes, List normalParams, InParams inParams) {
      List<Object> allParams = new ArrayList();
      OpenApiRequest request = null;
      int normalParamIndex = 0;

      for(Type type : paramTypes) {
         Object param;
         if (type.getTypeName().equals(OpenApiRequest.class.getName())) {
            if (request == null) {
               request = new OpenApiRequest();
               request.setUuid(inParams.getUuid());
               request.setCallerId(inParams.getCallerId());
               request.setApi(inParams.getApi());
               request.setMethod(inParams.getMethod());
               request.setDataType(inParams.getDataType());
            }

            param = request;
         } else {
            param = normalParams.get(normalParamIndex++);
         }

         allParams.add(param);
      }

      return allParams;
   }

   private long fillBinaryParam(Object obj, byte[] bodyBytes, long binaryLengthStartIndex) {
      if (obj instanceof Binary) {
         binaryLengthStartIndex = this.fillBinaryData((Binary)obj, bodyBytes, binaryLengthStartIndex);
      } else if (TypeUtil.isBinaryArray(obj.getClass())) {
         Binary[] binaries = (Binary[])obj;

         for(Binary binary : binaries) {
            binaryLengthStartIndex = this.fillBinaryData(binary, bodyBytes, binaryLengthStartIndex);
         }
      } else if (TypeUtil.isBinaryCollection(obj)) {
         for(Object element : (Collection)obj) {
            Binary binary = (Binary)element;
            binaryLengthStartIndex = this.fillBinaryData(binary, bodyBytes, binaryLengthStartIndex);
         }
      }

      return binaryLengthStartIndex;
   }

   private long fillBinaryData(Binary binary, byte[] bodyBytes, long binaryLengthStartIndex) {
      byte[] binaryDataBytes = BinaryUtil.getBinaryDataBytes(bodyBytes, binaryLengthStartIndex);
      binary.setData(binaryDataBytes);
      return BinaryUtil.getNextBinaryLengthStartIndex(binaryLengthStartIndex, binaryDataBytes);
   }

   private void verifySign(InParams inParams) {
      long startTime = System.nanoTime();
      String callerPublicKey = this.config.getCallerPublicKey(inParams.getCallerId());
      LogUtils.debug("{}caller({}) publicKey:{}", new Object[]{this.logPrefix.get(), inParams.getCallerId(), callerPublicKey});
      byte[] signContent = CommonUtil.getSignContent(inParams);
      boolean verify = this.asymmetricCryHandler.verifySign(callerPublicKey, signContent, inParams.getSign());
      this.logCostTime("验签", startTime);
      if (!verify) {
         throw new OpenApiServerException("验签失败");
      }
   }

   private byte[] decryptBody(InParams inParams, ApiHandler apiHandler) {
      long startTime = System.nanoTime();
      byte[] bodyBytes = inParams.getBodyBytes();

      try {
         CryModeEnum cryModeEnum = this.getCryModeEnum(apiHandler);
         if (cryModeEnum == CryModeEnum.SYMMETRIC_CRY) {
            String key = this.asymmetricCryHandler.deCry(this.selfPrivateKey, inParams.getSymmetricCryKey());
            byte[] keyBytes = Base64Util.base64ToBytes(key);
            bodyBytes = this.symmetricCryHandler.deCry(bodyBytes, keyBytes);
         } else if (cryModeEnum == CryModeEnum.ASYMMETRIC_CRY) {
            bodyBytes = this.asymmetricCryHandler.deCry(this.selfPrivateKey, bodyBytes);
         }
      } catch (OpenApiServerException be) {
         throw new OpenApiServerException("解密失败：" + be.getMessage());
      } catch (Exception ex) {
         LogUtils.error((String)this.logPrefix.get() + "解密失败", new Object[]{ex});
         throw new OpenApiServerException("解密失败");
      }

      this.logCostTime("解密", startTime);
      return bodyBytes;
   }

   private OutParams doCall(ApiHandler apiHandler, List paramList, InParams inParams) {
      try {
         OutParams outParams = OutParams.success();
         long startTime = System.nanoTime();
         Object[] params = paramList.toArray();
         LogUtils.debug("{}调用API:{},入参：{}", new Object[]{this.logPrefix.get(), apiHandler, TruncateUtil.truncate((Collection)paramList)});
         Object ret = apiHandler.getMethod().invoke(apiHandler.getBean(), params);
         LogUtils.debug("{}调用API:{},出参：{}", new Object[]{this.logPrefix.get(), apiHandler, TruncateUtil.truncate(ret)});
         this.logCostTime("调用API", startTime);
         String retStr = "";
         byte[] retBytes = null;
         outParams.setDataType(DataType.TEXT);
         if (ret != null) {
            if (ret instanceof Binary) {
               Binary binary = (Binary)ret;
               retStr = BinaryUtil.getBinaryString(binary);
               retBytes = BinaryUtil.buildSingleBinaryBytes(binary, retStr);
               retBytes = this.isEnableCompress(apiHandler) ? CompressUtil.compress(retBytes) : retBytes;
               outParams.setDataType(DataType.BINARY);
            } else {
               retStr = StrObjectConvert.objToStr(ret, ret.getClass());
               if (StrUtil.isNotBlank(retStr)) {
                  retBytes = this.isEnableCompress(apiHandler) ? CompressUtil.compressText(retStr) : retStr.getBytes(StandardCharsets.UTF_8);
               }
            }
         }

         if (ArrayUtil.isNotEmpty(retBytes)) {
            boolean retEncrypt = this.isRetEncrypt(apiHandler);
            if (retEncrypt) {
               retBytes = this.encryptRet(inParams, retBytes, outParams, apiHandler);
            }
         }

         outParams.setData(retStr);
         outParams.setDataBytes(retBytes);
         return outParams;
      } catch (OpenApiServerException be) {
         throw new OpenApiServerException("调用opeapi处理器异常:" + be.getMessage());
      } catch (Exception ex) {
         LogUtils.error((String)this.logPrefix.get() + "调用opeapi处理器异常", new Object[]{ex});
         throw new OpenApiServerException("调用opeapi处理器异常");
      }
   }

   private byte[] encryptRet(InParams inParams, byte[] retBytes, OutParams outParams, ApiHandler apiHandler) {
      try {
         long startTime = System.nanoTime();
         String callerPublicKey = this.config.getCallerPublicKey(inParams.getCallerId());
         LogUtils.debug("{}caller({}) publicKey:{}", new Object[]{this.logPrefix.get(), inParams.getCallerId(), callerPublicKey});
         CryModeEnum cryModeEnum = this.getCryModeEnum(apiHandler);
         if (cryModeEnum == CryModeEnum.SYMMETRIC_CRY) {
            byte[] keyBytes = SymmetricCryUtil.getKey(this.symmetricCryEnum);
            String key = Base64Util.bytesToBase64(keyBytes);
            String cryKey = this.asymmetricCryHandler.cry(callerPublicKey, key);
            outParams.setSymmetricCryKey(cryKey);
            retBytes = this.symmetricCryHandler.cry(retBytes, keyBytes);
         } else if (cryModeEnum == CryModeEnum.ASYMMETRIC_CRY) {
            retBytes = this.asymmetricCryHandler.cry(callerPublicKey, retBytes);
         }

         this.logCostTime("加密", startTime);
         return retBytes;
      } catch (OpenApiServerException be) {
         throw new OpenApiServerException("返回值加密异常:" + be.getMessage());
      } catch (Exception ex) {
         throw new OpenApiServerException("返回值加密异常", ex);
      }
   }

   private CryModeEnum getCryModeEnum(ApiHandler apiHandler) {
      CryModeEnum cryModeEnum = this.cryModeEnum;
      OpenApiMethod apiMethod = apiHandler.getOpenApiMethod();
      if (CryModeEnum.UNKNOWN != apiMethod.cryModeEnum()) {
         cryModeEnum = apiMethod.cryModeEnum();
         this.logCryMode(cryModeEnum, apiMethod);
      }

      return cryModeEnum;
   }

   private boolean isRetEncrypt(ApiHandler apiHandler) {
      boolean retEncrypt = this.retEncrypt;
      if (StrUtil.isNotBlank(apiHandler.getOpenApiMethod().retEncrypt())) {
         retEncrypt = Boolean.parseBoolean(apiHandler.getOpenApiMethod().retEncrypt());
      }

      LogUtils.debug("{}返回值是否需要加密：{}", new Object[]{this.logPrefix.get(), retEncrypt});
      return retEncrypt;
   }

   private boolean isEnableCompress(ApiHandler apiHandler) {
      boolean enableCompress = this.enableCompress;
      OpenApiMethod apiMethod = apiHandler.getOpenApiMethod();
      if (StrUtil.isNotBlank(apiMethod.enableCompress())) {
         enableCompress = Boolean.parseBoolean(apiMethod.enableCompress());
         this.logEnableCompress(enableCompress, apiMethod);
      }

      return enableCompress;
   }

   private String getHandlerKey(String openApiName, String openApiMethodName) {
      return openApiName + "_" + openApiMethodName;
   }

   private void logCryMode(CryModeEnum cryModeEnum, OpenApiMethod apiMethod) {
      String methodId = "";
      if (apiMethod != null) {
         methodId = apiMethod.value();
      }

      if (cryModeEnum == CryModeEnum.SYMMETRIC_CRY) {
         LogUtils.debug("{}采用非对称加密{}+对称加密{}模式", new Object[]{methodId, this.asymmetricCryEnum, this.symmetricCryEnum});
      } else if (cryModeEnum == CryModeEnum.ASYMMETRIC_CRY) {
         LogUtils.debug("{}仅采用非对称加密{}模式", new Object[]{methodId, this.asymmetricCryEnum});
      } else {
         LogUtils.debug("{}采用不加密模式,签名用的非对称加密{}", new Object[]{methodId, this.asymmetricCryEnum});
      }

   }

   private void logEnableCompress(boolean enableCompress, OpenApiMethod apiMethod) {
      String methodId = "";
      if (apiMethod != null) {
         methodId = apiMethod.value();
      }

      LogUtils.debug("{}HTTP传输数据{}压缩功能", new Object[]{methodId, enableCompress ? "启用" : "未启用"});
   }

   private void logCostTime(String operate, long startTime) {
      LogUtils.debug("{}{}耗时:{}ms", new Object[]{this.logPrefix.get(), operate, (System.nanoTime() - startTime) / 1000000L});
   }
}
