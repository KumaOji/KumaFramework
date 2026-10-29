package com.kuma.boot.idgenerator.uid;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.core.enums.KmcEnvEnum;
import com.kuma.boot.idgenerator.uid.config.UidGenProperties;
import com.kuma.boot.idgenerator.uid.exception.UidGenerateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NosGenerator implements Nos {
   private static final int SEQ_LEN = 16;
   private static final int OID_LEN = 46;
   private static final int RID_LEN = 30;
   private static final int EXTENSION_CODE_LEN = 3;
   private static final int SHARD_CODE_LEN = 2;
   private static final int SERVICE_CODE_LEN = 4;
   private static final int BUSINESS_CODE_LEN = 12;
   private static final String EMPTY_SERVICE_CODE = "0000";
   private static final String EMPTY_SHARD_CODE = "00";
   private static final String EMPTY_EXTENSION_CODE = "000";
   private UidGenerator uidGenerator;
   private UidGenProperties uidGenProperties;

   public NosGenerator() {
   }

   public void setUidGenerator(UidGenerator uidGenerator) {
      this.uidGenerator = uidGenerator;
   }

   public void setUidGenProperties(UidGenProperties uidGenProperties) {
      this.uidGenProperties = uidGenProperties;
   }

   public String getOrderNo(String businessCode) {
      return this.getOrderNo(businessCode, "0000", "00", "000");
   }

   public String getOrderNo(String businessCode, String serviceCode) {
      return this.getOrderNo(businessCode, serviceCode, "00", "000");
   }

   public String getOrderNo(String businessCode, String serviceCode, String extensionCode) {
      return this.getOrderNo(businessCode, serviceCode, "00", extensionCode);
   }

   public String getOrderNo(String businessCode, String serviceCode, String shardCode, String extensionCode) {
      this.assertLen(businessCode, 12, "businessCode长度必须是12位");
      this.assertLen(serviceCode, 4, "businessCode长度必须是4位");
      String id = this.getNo(shardCode, extensionCode);
      StringBuilder sb = new StringBuilder(46);
      sb.append(businessCode).append(serviceCode).append(id);
      return sb.toString();
   }

   public String getBizNo() {
      return this.getNo();
   }

   public String getRequestNo() {
      return this.getNo();
   }

   public String getNo() {
      return this.getNo("00", "000");
   }

   public String getNo(String extensionCode) {
      return this.getNo("00", extensionCode);
   }

   public String getNo(String shardCode, String extensionCode) {
      this.assertLen(shardCode, 2, "shardCode长度必须是2位");
      this.assertLen(extensionCode, 3, "extensionCode长度必须是3位");
      StringBuilder sb = new StringBuilder(30);
      sb.append(this.uidGenProperties.getSystemCode()).append(KmcEnvEnum.getCurrEnv().getCode()).append(this.uidGenProperties.getDataCenterCode()).append(shardCode).append(extensionCode);

      try {
         sb.append(this.longToHex(this.uidGenerator.getUID()));
      } catch (Exception e) {
         throw new UidGenerateException(e);
      }

      return sb.toString().toUpperCase();
   }

   public Map<String, Object> parseNo(String id) {
      if (id != null && id.length() == 30) {
         Map<String, Object> map = new HashMap<>(6);
         map.put("systemCode", UidParser.parse(id, UidParser.ID_SYSTEM_CODE));
         map.put("envCode", UidParser.parse(id, UidParser.ID_ENV_CODE));
         map.put("dataCenterCode", UidParser.parse(id, UidParser.ID_DATA_CENTER_CODE));
         map.put("shardCode", UidParser.parse(id, UidParser.ID_SHARD_CODE));
         map.put("extensionCode", UidParser.parse(id, UidParser.ID_EXTENSION_CODE));
         map.putAll(this.uidGenerator.parseUid(Long.parseLong(UidParser.parse(id, UidParser.ID_SEQ_CODE), 16)));
         return Collections.unmodifiableMap(map);
      } else {
         throw new BusinessException("id 长度必须是30位");
      }
   }

   public String parseSystemCode(String uid) {
      if (uid != null && uid.length() == 30) {
         return UidParser.parse(uid, UidParser.ID_SYSTEM_CODE);
      } else if (uid != null && uid.length() == 46) {
         return UidParser.parse(uid, UidParser.OID_SYSTEM_CODE);
      } else {
         throw new BusinessException("id 长度必须是30位或者46位");
      }
   }

   public String parseDataCenterCode(String uid) {
      if (uid != null && uid.length() == 30) {
         return UidParser.parse(uid, UidParser.ID_DATA_CENTER_CODE);
      } else if (uid != null && uid.length() == 46) {
         return UidParser.parse(uid, UidParser.ID_DATA_CENTER_CODE);
      } else {
         throw new BusinessException("id 长度必须是30位或者46位");
      }
   }

   public String parseShardCode(String uid) {
      if (uid != null && uid.length() == 30) {
         return UidParser.parse(uid, UidParser.ID_SHARD_CODE);
      } else if (uid != null && uid.length() == 46) {
         return UidParser.parse(uid, UidParser.ID_SHARD_CODE);
      } else {
         throw new BusinessException("id 长度必须是30位或者46位");
      }
   }

   public Long parseSequenceCode(String uid) {
      if (uid != null && uid.length() == 30) {
         return Long.parseLong(UidParser.parse(uid, UidParser.ID_SEQ_CODE), 16);
      } else if (uid != null && uid.length() == 46) {
         return Long.parseLong(UidParser.parse(uid, UidParser.OID_SEQ_CODE), 16);
      } else {
         throw new BusinessException("id 长度必须是30位或者46位");
      }
   }

   public String parseExtensionCode(String uid) {
      if (uid != null && uid.length() == 30) {
         return UidParser.parse(uid, UidParser.ID_EXTENSION_CODE);
      } else if (uid != null && uid.length() == 46) {
         return UidParser.parse(uid, UidParser.OID_EXTENSION_CODE);
      } else {
         throw new BusinessException("id 长度必须是30位或者46位");
      }
   }

   public String parseBusinessCode(String oid) {
      if (oid != null && oid.length() == 46) {
         return UidParser.parse(oid, UidParser.OID_BUSINESS_CODE);
      } else {
         throw new BusinessException("id 长度必须46位");
      }
   }

   public String parseServiceCode(String oid) {
      if (oid != null && oid.length() == 46) {
         return UidParser.parse(oid, UidParser.OID_SERVICE_CODE);
      } else {
         throw new BusinessException("id 长度必须46位");
      }
   }

   public String parseChannelCode(String oid) {
      if (oid != null && oid.length() == 46) {
         return UidParser.parse(oid, UidParser.OID_CHANNEL_CODE);
      } else {
         throw new BusinessException("id 长度必须46位");
      }
   }

   public String parseProductCode(String oid) {
      if (oid != null && oid.length() == 46) {
         return UidParser.parse(oid, UidParser.OID_PRODUCT_CODE);
      } else {
         throw new BusinessException("id 长度必须46位");
      }
   }

   private void assertLen(String str, int permitLen, String msg) {
      if (StrUtil.isBlank(str) || str.length() != permitLen) {
         throw new BusinessException(msg);
      }
   }

   private String longToHex(long l) {
      String hexString = Long.toHexString(l);
      StringBuilder sb = new StringBuilder(16);
      int m = 16 - hexString.length();
      if (m <= 0) {
         return hexString;
      } else {
         while(m > 0) {
            sb.append("0");
            --m;
         }

         sb.append(hexString);
         return sb.toString();
      }
   }
}
