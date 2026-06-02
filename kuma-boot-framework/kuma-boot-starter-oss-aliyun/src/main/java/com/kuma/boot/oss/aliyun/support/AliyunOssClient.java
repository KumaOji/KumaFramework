package com.kuma.boot.oss.aliyun.support;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.exception.OssException;
import com.kuma.boot.oss.common.model.DirectoryOssInfo;
import com.kuma.boot.oss.common.model.FileOssInfo;
import com.kuma.boot.oss.common.model.OssInfo;
import com.kuma.boot.oss.common.model.download.DownloadCheckPoint;
import com.kuma.boot.oss.common.model.download.DownloadObjectStat;
import com.kuma.boot.oss.common.service.StandardOssClient;
import com.kuma.boot.oss.common.util.OssPathUtil;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云 OSS 客户端
 *
 * @author kuma
 */
public class AliyunOssClient implements StandardOssClient {
   public static final String ALIYUN_OSS_OBJECT_NAME = "ossClient";
   private OSS ossClient;
   private AliyunOssConfig aliyunOssConfig;

   public AliyunOssClient(OSS ossClient, AliyunOssConfig aliyunOssConfig) {
      this.ossClient = ossClient;
      this.aliyunOssConfig = aliyunOssConfig;
   }

   @Override
   public OssInfo upLoad(InputStream is, String targetName, Boolean isOverride) {
      try {
         String bucket = this.getBucket();
         String key = this.getKey(targetName, false);
         this.ossClient.putObject(bucket, key, is);
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
      OSSObject ossObject = null;

      try {
         ossObject = this.ossClient.getObject(this.getBucket(), this.getKey(targetName, false));
         IoUtil.copy(ossObject.getObjectContent(), os);
      } catch (Exception e) {
         throw new OssException(e);
      } finally {
         IoUtil.close(ossObject);
      }
   }

   @Override
   public void downLoadCheckPoint(File localFile, String targetName) {
      this.downLoadFile(localFile, targetName, this.aliyunOssConfig.getSliceConfig(), "aliyun");
   }

   @Override
   public DownloadObjectStat getDownloadObjectStat(String targetName) {
      try {
         ObjectMetadata metadata = this.ossClient.getObjectMetadata(this.getBucket(), this.getKey(targetName, false));
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
         Long partSize = this.aliyunOssConfig.getSliceConfig().getPartSize();
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
      return this.ossClient.getObject(getObjectRequest).getObjectContent();
   }

   @Override
   public void delete(String targetName) {
      try {
         this.ossClient.deleteObject(this.getBucket(), this.getKey(targetName, false));
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   @Override
   public void copy(String sourceName, String targetName, Boolean isOverride) {
      try {
         String bucket = this.getBucket();
         CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, this.getKey(sourceName, false), bucket, this.getKey(targetName, false));
         this.ossClient.copyObject(copyObjectRequest);
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
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(this.getBucket());
            listObjectsRequest.setPrefix(realPrefix);
            listObjectsRequest.setDelimiter("/");
            ObjectListing objectListing = this.ossClient.listObjects(listObjectsRequest);
            List<OssInfo> fileOssInfos = new ArrayList<>();
            List<OssInfo> directoryInfos = new ArrayList<>();

            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
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
      return this.aliyunOssConfig.getBasePath();
   }

   @Override
   public Map<String, Object> getClientObject() {
      Map<String, Object> clientObjectMap = new HashMap<>();
      clientObjectMap.put(ALIYUN_OSS_OBJECT_NAME, this.ossClient);
      return clientObjectMap;
   }

   private String getBucket() {
      return this.aliyunOssConfig.getBucketName();
   }

   public OssInfo getBaseInfo(String targetName) {
      String key = this.getKey(targetName, false);
      String bucketName = this.getBucket();
      OssInfo ossInfo;
      if (this.isFile(key)) {
         ossInfo = new FileOssInfo();

         try {
            ObjectMetadata metadata = this.ossClient.getObjectMetadata(bucketName, key);
            if (metadata.getLastModified() != null) {
               ossInfo.setLastUpdateTime(DateUtil.date(metadata.getLastModified()).toString("yyyy-MM-dd HH:mm:ss"));
            }
            ossInfo.setCreateTime(ossInfo.getLastUpdateTime());
            ossInfo.setLength(metadata.getContentLength());
            ossInfo.setUrl(this.aliyunOssConfig.getEndpoint() + "/" + bucketName + "/" + key);
         } catch (Exception e) {
            LogUtils.error("获取{}文件属性失败", new Object[]{key, e});
         }
      } else {
         ossInfo = new DirectoryOssInfo();
      }

      ossInfo.setName(StrUtil.equals(targetName, "/") ? targetName : FileNameUtil.getName(targetName));
      ossInfo.setPath(OssPathUtil.replaceKey(targetName, this.aliyunOssConfig.getBasePath(), true));
      return ossInfo;
   }

   public OSS getOssClient() {
      return this.ossClient;
   }

   public void setOssClient(OSS ossClient) {
      this.ossClient = ossClient;
   }

   public AliyunOssConfig getAliyunOssConfig() {
      return this.aliyunOssConfig;
   }

   public void setAliyunOssConfig(AliyunOssConfig aliyunOssConfig) {
      this.aliyunOssConfig = aliyunOssConfig;
   }
}
