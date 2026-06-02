package com.kuma.boot.oss.cos.support;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.exception.OssException;
import com.kuma.boot.oss.common.model.DirectoryOssInfo;
import com.kuma.boot.oss.common.model.FileOssInfo;
import com.kuma.boot.oss.common.model.OssInfo;
import com.kuma.boot.oss.common.model.download.DownloadCheckPoint;
import com.kuma.boot.oss.common.model.download.DownloadObjectStat;
import com.kuma.boot.oss.common.service.StandardOssClient;
import com.kuma.boot.oss.common.util.OssPathUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.CopyObjectRequest;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云 COS 客户端
 *
 * @author kuma
 */
public class CosOssClient implements StandardOssClient {
   public static final String COS_OSS_OBJECT_NAME = "cosClient";
   private COSClient cosClient;
   private CosOssConfig cosOssConfig;

   public CosOssClient(COSClient cosClient, CosOssConfig cosOssConfig) {
      this.cosClient = cosClient;
      this.cosOssConfig = cosOssConfig;
   }

   @Override
   public OssInfo upLoad(InputStream is, String targetName, Boolean isOverride) {
      try {
         String bucket = this.getBucket();
         String key = this.getKey(targetName, false);
         ObjectMetadata objectMetadata = new ObjectMetadata();
         objectMetadata.setContentLength(is.available());
         PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, is, objectMetadata);
         this.cosClient.putObject(putObjectRequest);
      } catch (Exception e) {
         throw new OssException(e);
      }

      return this.getInfo(targetName);
   }

   @Override
   public OssInfo upLoadCheckPoint(File file, String targetName) {
      try (InputStream inputStream = FileUtil.getInputStream(file)) {
         this.upLoad(inputStream, targetName, true);
      } catch (Exception e) {
         throw new OssException(e);
      }

      return this.getInfo(targetName);
   }

   @Override
   public void downLoad(OutputStream os, String targetName) {
      COSObject cosObject = null;

      try {
         cosObject = this.cosClient.getObject(this.getBucket(), this.getKey(targetName, false));
         IoUtil.copy(cosObject.getObjectContent(), os);
      } catch (Exception e) {
         throw new OssException(e);
      } finally {
         IoUtil.close(cosObject);
      }
   }

   @Override
   public void downLoadCheckPoint(File localFile, String targetName) {
      this.downLoadFile(localFile, targetName, this.cosOssConfig.getSliceConfig(), "cos");
   }

   @Override
   public DownloadObjectStat getDownloadObjectStat(String targetName) {
      try {
         ObjectMetadata metadata = this.cosClient.getObjectMetadata(this.getBucket(), this.getKey(targetName, false));
         DownloadObjectStat downloadObjectStat = new DownloadObjectStat();
         downloadObjectStat.setSize(metadata.getContentLength());
         downloadObjectStat.setDigest(metadata.getETag());
         downloadObjectStat.setLastModified(metadata.getLastModified());
         return downloadObjectStat;
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   @Override
   public void prepareDownload(DownloadCheckPoint downloadCheckPoint, File localFile, String targetName, String checkpointFile) {
      downloadCheckPoint.setMagic(DownloadCheckPoint.DOWNLOAD_MAGIC);
      downloadCheckPoint.setDownloadFile(localFile.getPath());
      downloadCheckPoint.setBucketName(this.getBucket());
      downloadCheckPoint.setKey(this.getKey(targetName, false));
      downloadCheckPoint.setCheckPointFile(checkpointFile);
      downloadCheckPoint.setObjectStat(this.getDownloadObjectStat(targetName));
      long downloadSize;
      if (downloadCheckPoint.getObjectStat().getSize() > 0L) {
         Long partSize = this.cosOssConfig.getSliceConfig().getPartSize();
         long[] slice = this.getDownloadSlice(new long[0], downloadCheckPoint.getObjectStat().getSize());
         downloadCheckPoint.setDownloadParts(this.splitDownloadFile(slice[0], slice[1], partSize));
         downloadSize = slice[1];
      } else {
         downloadSize = 0L;
         downloadCheckPoint.setDownloadParts(this.splitDownloadOneFile());
      }

      downloadCheckPoint.setOriginPartSize((long) downloadCheckPoint.getDownloadParts().size());
      this.createDownloadTemp(downloadCheckPoint.getTempDownloadFile(), downloadSize);
   }

   @Override
   public InputStream downloadPart(String key, long start, long end) {
      GetObjectRequest getObjectRequest = new GetObjectRequest(this.getBucket(), key);
      getObjectRequest.setRange(start, end);
      return this.cosClient.getObject(getObjectRequest).getObjectContent();
   }

   @Override
   public void delete(String targetName) {
      try {
         this.cosClient.deleteObject(this.getBucket(), this.getKey(targetName, false));
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   @Override
   public void copy(String sourceName, String targetName, Boolean isOverride) {
      try {
         String bucket = this.getBucket();
         CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, this.getKey(sourceName, false), bucket, this.getKey(targetName, false));
         this.cosClient.copyObject(copyObjectRequest);
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   @Override
   public OssInfo getInfo(String targetName, Boolean isRecursion) {
      try {
         String key = this.getKey(targetName, false);
         OssInfo ossInfo = this.getBaseInfo(targetName);
         if (isRecursion && this.isDirectory(key)) {
            String prefix = OssPathUtil.convertPath(key, false);
            String realPrefix = prefix.isEmpty() || prefix.endsWith("/") ? prefix : prefix + "/";
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(this.getBucket());
            listObjectsRequest.setPrefix(realPrefix);
            listObjectsRequest.setDelimiter("/");
            ObjectListing objectListing = this.cosClient.listObjects(listObjectsRequest);
            List<OssInfo> fileOssInfos = new ArrayList<>();
            List<OssInfo> directoryInfos = new ArrayList<>();

            for (COSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
               String childKey = OssPathUtil.replaceKey(objectSummary.getKey(), this.getBasePath(), true);
               fileOssInfos.add(this.getInfo(childKey, false));
            }

            for (String commonPrefix : objectListing.getCommonPrefixes()) {
               String childKey = OssPathUtil.replaceKey(commonPrefix, this.getBasePath(), true);
               directoryInfos.add(this.getInfo(childKey, true));
            }

            if (ObjUtil.isNotEmpty(fileOssInfos) && fileOssInfos.get(0) instanceof FileOssInfo) {
               ReflectUtil.setFieldValue(ossInfo, "fileInfos", fileOssInfos);
            }

            if (ObjUtil.isNotEmpty(directoryInfos) && directoryInfos.get(0) instanceof DirectoryOssInfo) {
               ReflectUtil.setFieldValue(ossInfo, "directoryInfos", directoryInfos);
            }
         }

         return ossInfo;
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   @Override
   public String getBasePath() {
      return this.cosOssConfig.getBasePath();
   }

   @Override
   public Map<String, Object> getClientObject() {
      Map<String, Object> clientObjectMap = new HashMap<>();
      clientObjectMap.put(COS_OSS_OBJECT_NAME, this.cosClient);
      return clientObjectMap;
   }

   private String getBucket() {
      return this.cosOssConfig.getBucketName();
   }

   public OssInfo getBaseInfo(String targetName) {
      String key = this.getKey(targetName, false);
      String bucketName = this.getBucket();
      OssInfo ossInfo;
      if (this.isFile(key)) {
         ossInfo = new FileOssInfo();

         try {
            ObjectMetadata metadata = this.cosClient.getObjectMetadata(bucketName, key);
            if (metadata.getLastModified() != null) {
               ossInfo.setLastUpdateTime(DateUtil.date(metadata.getLastModified()).toString("yyyy-MM-dd HH:mm:ss"));
            }
            ossInfo.setCreateTime(ossInfo.getLastUpdateTime());
            ossInfo.setLength(metadata.getContentLength());
            ossInfo.setUrl("https://" + bucketName + ".cos." + this.cosOssConfig.getRegion() + ".myqcloud.com/" + key);
         } catch (Exception e) {
            LogUtils.error("获取{}文件属性失败", new Object[]{key, e});
         }
      } else {
         ossInfo = new DirectoryOssInfo();
      }

      ossInfo.setName(StrUtil.equals(targetName, "/") ? targetName : FileNameUtil.getName(targetName));
      ossInfo.setPath(OssPathUtil.replaceKey(targetName, this.cosOssConfig.getBasePath(), true));
      return ossInfo;
   }

   public COSClient getCosClient() {
      return this.cosClient;
   }

   public void setCosClient(COSClient cosClient) {
      this.cosClient = cosClient;
   }

   public CosOssConfig getCosOssConfig() {
      return this.cosOssConfig;
   }

   public void setCosOssConfig(CosOssConfig cosOssConfig) {
      this.cosOssConfig = cosOssConfig;
   }
}
