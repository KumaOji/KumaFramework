package com.kuma.boot.idgenerator.uid;

import java.util.Map;

public interface Nos {
   String getOrderNo(String businessCode);

   String getOrderNo(String businessCode, String serviceCode);

   String getOrderNo(String businessCode, String serviceCode, String extensionCode);

   String getOrderNo(String businessCode, String serviceCode, String shardCode, String extensionCode);

   String getBizNo();

   String getRequestNo();

   String getNo();

   String getNo(String extensionCode);

   String getNo(String shardCode, String extensionCode);

   Map<String, Object> parseNo(String no);

   String parseSystemCode(String uid);

   String parseDataCenterCode(String uid);

   String parseShardCode(String uid);

   Long parseSequenceCode(String uid);

   String parseBusinessCode(String oid);

   String parseServiceCode(String oid);

   String parseExtensionCode(String oid);

   String parseChannelCode(String oid);

   String parseProductCode(String oid);
}
