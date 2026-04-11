package com.kuma.boot.oss.minio.support;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.io.ByteStreams;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.exception.OssException;
import com.kuma.boot.oss.common.model.DirectoryOssInfo;
import com.kuma.boot.oss.common.model.FileOssInfo;
import com.kuma.boot.oss.common.model.OssInfo;
import com.kuma.boot.oss.common.model.download.DownloadCheckPoint;
import com.kuma.boot.oss.common.model.download.DownloadObjectStat;
import com.kuma.boot.oss.common.service.StandardOssClient;
import com.kuma.boot.oss.common.util.OssPathUtil;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import okhttp3.Headers;

public class MinioOssClient implements StandardOssClient {
   public static final String MINIO_OBJECT_NAME = "minioClient";
   public static final String ACCEPT = "Accept";
   public static final String ACCEPT_CHARSET = "Accept-Charset";
   private MinioClient minioClient;
   private MinioOssConfig minioOssConfig;

   public MinioOssClient(MinioClient minioClient, MinioOssConfig minioOssConfig) {
      this.minioClient = minioClient;
      this.minioOssConfig = minioOssConfig;
   }

   public OssInfo upLoad(InputStream is, String targetName, Boolean isOverride) {
      try {
         String bucket = this.getBucket();
         String key = this.getKey(targetName, false);
         ObjectWriteResponse objectWriteResponse = this.minioClient.putObject((PutObjectArgs)((PutObjectArgs.Builder)((PutObjectArgs.Builder)PutObjectArgs.builder().bucket(bucket)).object(key)).stream(is, (long)is.available(), -1L).build());
         LogUtils.info("minio objectWriteResponse ----- etag: {}, object: {}, versionId:{}, headers: {}", new Object[]{objectWriteResponse.etag(), objectWriteResponse.object(), objectWriteResponse.versionId(), objectWriteResponse.headers()});
      } catch (Exception e) {
         throw new OssException(e);
      }

      return this.getInfo(targetName);
   }

   public OssInfo upLoadCheckPoint(File file, String targetName) {
      try {
         InputStream inputStream = FileUtil.getInputStream(file);

         try {
            this.upLoad(inputStream, targetName, true);
         } catch (Throwable var7) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } catch (Exception e) {
         throw new OssException(e);
      }

      return this.getInfo(targetName);
   }

   public void downLoad(OutputStream os, String targetName) {
      GetObjectResponse is = null;

      try {
         GetObjectArgs getObjectArgs = (GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(this.getBucket())).object(this.getKey(targetName, true))).build();
         is = this.minioClient.getObject(getObjectArgs);
         ByteStreams.copy(is, os);
      } catch (Exception e) {
         throw new OssException(e);
      } finally {
         IoUtil.close(is);
      }

   }

   public void downLoadCheckPoint(File localFile, String targetName) {
      this.downLoadFile(localFile, targetName, this.minioOssConfig.getSliceConfig(), "minio");
   }

   public DownloadObjectStat getDownloadObjectStat(String targetName) {
      try {
         StatObjectArgs statObjectArgs = (StatObjectArgs)((StatObjectArgs.Builder)((StatObjectArgs.Builder)StatObjectArgs.builder().bucket(this.getBucket())).object(this.getKey(targetName, true))).build();
         StatObjectResponse statObjectResponse = this.minioClient.statObject(statObjectArgs);
         long contentLength = statObjectResponse.size();
         String eTag = statObjectResponse.etag();
         DownloadObjectStat downloadObjectStat = new DownloadObjectStat();
         downloadObjectStat.setSize(contentLength);
         downloadObjectStat.setDigest(eTag);
         downloadObjectStat.setLastModified(Date.from(statObjectResponse.lastModified().toInstant()));
         return downloadObjectStat;
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   public void prepareDownload(DownloadCheckPoint downloadCheckPoint, File localFile, String targetName, String checkpointFile) {
      downloadCheckPoint.setMagic("92611BED-89E2-46B6-89E5-72F273D4B0A3");
      downloadCheckPoint.setDownloadFile(localFile.getPath());
      downloadCheckPoint.setBucketName(this.getBucket());
      downloadCheckPoint.setKey(this.getKey(targetName, false));
      downloadCheckPoint.setCheckPointFile(checkpointFile);
      downloadCheckPoint.setObjectStat(this.getDownloadObjectStat(targetName));
      long downloadSize;
      if (downloadCheckPoint.getObjectStat().getSize() > 0L) {
         Long partSize = this.minioOssConfig.getSliceConfig().getPartSize();
         long[] slice = this.getDownloadSlice(new long[0], downloadCheckPoint.getObjectStat().getSize());
         downloadCheckPoint.setDownloadParts(this.splitDownloadFile(slice[0], slice[1], partSize));
         downloadSize = slice[1];
      } else {
         downloadSize = 0L;
         downloadCheckPoint.setDownloadParts(this.splitDownloadOneFile());
      }

      downloadCheckPoint.setOriginPartSize((long)downloadCheckPoint.getDownloadParts().size());
      this.createDownloadTemp(downloadCheckPoint.getTempDownloadFile(), downloadSize);
   }

   public InputStream downloadPart(String key, long start, long end) throws Exception {
      GetObjectArgs getObjectArgs = (GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(this.getBucket())).object(key)).offset(start)).length(end)).build();
      return this.minioClient.getObject(getObjectArgs);
   }

   public void delete(String targetName) {
      try {
         RemoveObjectArgs removeObjectArgs = (RemoveObjectArgs)((RemoveObjectArgs.Builder)((RemoveObjectArgs.Builder)RemoveObjectArgs.builder().bucket(this.getBucket())).object(this.getKey(targetName, true))).build();
         this.minioClient.removeObject(removeObjectArgs);
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   public void copy(String sourceName, String targetName, Boolean isOverride) {
      try {
         CopyObjectArgs copyObjectArgs = (CopyObjectArgs)((CopyObjectArgs.Builder)((CopyObjectArgs.Builder)CopyObjectArgs.builder().bucket(this.getBucket())).object(this.getKey(targetName, true))).source((CopySource)((CopySource.Builder)((CopySource.Builder)CopySource.builder().bucket(this.getBucket())).object(this.getKey(sourceName, true))).build()).build();
         this.minioClient.copyObject(copyObjectArgs);
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   public OssInfo getInfo(String targetName, Boolean isRecursion) {
      try {
         String key = this.getKey(targetName, false);
         OssInfo ossInfo = this.getBaseInfo(targetName);
         if (isRecursion && this.isDirectory(key)) {
            String prefix = OssPathUtil.convertPath(key, true);
            ListObjectsArgs listObjectsArgs = (ListObjectsArgs)((ListObjectsArgs.Builder)ListObjectsArgs.builder().bucket(this.getBucket())).delimiter("/").prefix(prefix.endsWith("/") ? prefix : prefix + "/").build();
            Iterable<Result<Item>> results = this.minioClient.listObjects(listObjectsArgs);
            List<OssInfo> fileOssInfos = new ArrayList();
            List<OssInfo> directoryInfos = new ArrayList();

            for(Result<Item> result : results) {
               Item item = (Item)result.get();
               String childKey = OssPathUtil.replaceKey(item.objectName(), this.getBasePath(), true);
               if (item.isDir()) {
                  directoryInfos.add(this.getInfo(childKey, true));
               } else {
                  fileOssInfos.add(this.getInfo(childKey, false));
               }
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

   public String getBasePath() {
      return this.minioOssConfig.getBasePath();
   }

   public Map<String, Object> getClientObject() {
      return new HashMap<String, Object>() {
         {
            Objects.requireNonNull(MinioOssClient.this);
            this.put("minioClient", MinioOssClient.this.getMinioClient());
         }
      };
   }

   private String getBucket() {
      return this.minioOssConfig.getBucketName();
   }

   public OssInfo getBaseInfo(String targetName) {
      String key = this.getKey(targetName, false);
      String bucketName = this.getBucket();
      OssInfo ossInfo;
      if (this.isFile(key)) {
         ossInfo = new FileOssInfo();

         try {
            GetObjectArgs getObjectArgs = (GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(bucketName)).object(key)).build();
            GetObjectResponse objectResponse = this.minioClient.getObject(getObjectArgs);
            Headers headers = objectResponse.headers();
            ossInfo.setCreateTime(DateUtil.date(headers.getDate("Date")).toString("yyyy-MM-dd HH:mm:ss"));
            ossInfo.setLastUpdateTime(DateUtil.date(headers.getDate("Last-Modified")).toString("yyyy-MM-dd HH:mm:ss"));
            ossInfo.setLength(Long.valueOf((String)Objects.requireNonNull(headers.get("Content-Length"))));
            ossInfo.setUrl(this.minioOssConfig.getEndpoint() + "/" + bucketName + key);
         } catch (Exception e) {
            LogUtils.error("\u83b7\u53d6{}\u6587\u4ef6\u5c5e\u6027\u5931\u8d25", new Object[]{key, e});
         }
      } else {
         ossInfo = new DirectoryOssInfo();
      }

      ossInfo.setName(StrUtil.equals(targetName, "/") ? targetName : FileNameUtil.getName(targetName));
      ossInfo.setPath(OssPathUtil.replaceKey(targetName, this.minioOssConfig.getBasePath(), true));
      return ossInfo;
   }

   public MinioClient getMinioClient() {
      return this.minioClient;
   }

   public void setMinioClient(MinioClient minioClient) {
      this.minioClient = minioClient;
   }

   public MinioOssConfig getMinioOssConfig() {
      return this.minioOssConfig;
   }

   public void setMinioOssConfig(MinioOssConfig minioOssConfig) {
      this.minioOssConfig = minioOssConfig;
   }
}
