package com.kuma.boot.oss.qiniu.support;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.exception.OssException;
import com.kuma.boot.oss.common.model.DirectoryOssInfo;
import com.kuma.boot.oss.common.model.FileOssInfo;
import com.kuma.boot.oss.common.model.OssInfo;
import com.kuma.boot.oss.common.model.download.DownloadCheckPoint;
import com.kuma.boot.oss.common.model.download.DownloadObjectStat;
import com.kuma.boot.oss.common.service.StandardOssClient;
import com.kuma.boot.oss.common.util.OssPathUtil;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 七牛云 OSS 客户端
 *
 * <p>七牛云无服务端直接下载流的接口，下载通过签名后的访问域名以 HTTP 方式获取。</p>
 *
 * @author kuma
 */
public class QiniuOssClient implements StandardOssClient {
   public static final String QINIU_AUTH_NAME = "auth";
   public static final String QINIU_UPLOAD_MANAGER_NAME = "uploadManager";
   public static final String QINIU_BUCKET_MANAGER_NAME = "bucketManager";

   private Auth auth;
   private UploadManager uploadManager;
   private BucketManager bucketManager;
   private QiniuOssConfig qiniuOssConfig;

   public QiniuOssClient(Auth auth, UploadManager uploadManager, BucketManager bucketManager, QiniuOssConfig qiniuOssConfig) {
      this.auth = auth;
      this.uploadManager = uploadManager;
      this.bucketManager = bucketManager;
      this.qiniuOssConfig = qiniuOssConfig;
   }

   @Override
   public OssInfo upLoad(InputStream is, String targetName, Boolean isOverride) {
      try {
         String bucket = this.getBucket();
         String key = this.getKey(targetName, false);
         String upToken = this.auth.uploadToken(bucket, key);
         this.uploadManager.put(is, key, upToken, null, null);
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
      HttpResponse response = null;

      try {
         String url = this.buildDownloadUrl(this.getKey(targetName, false));
         response = HttpRequest.get(url).timeout((int) (long) this.qiniuOssConfig.getClientConfig().getReadTimeout()).execute();
         IoUtil.copy(response.bodyStream(), os);
      } catch (Exception e) {
         throw new OssException(e);
      } finally {
         IoUtil.close(response);
      }
   }

   @Override
   public void downLoadCheckPoint(File localFile, String targetName) {
      this.downLoadFile(localFile, targetName, this.qiniuOssConfig.getSliceConfig(), "qiniu");
   }

   @Override
   public DownloadObjectStat getDownloadObjectStat(String targetName) {
      try {
         FileInfo fileInfo = this.bucketManager.stat(this.getBucket(), this.getKey(targetName, false));
         DownloadObjectStat downloadObjectStat = new DownloadObjectStat();
         downloadObjectStat.setSize(fileInfo.fsize);
         downloadObjectStat.setDigest(fileInfo.hash);
         // 七牛 putTime 单位为 100 纳秒，转换为毫秒
         downloadObjectStat.setLastModified(new Date(fileInfo.putTime / 10000L));
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
         Long partSize = this.qiniuOssConfig.getSliceConfig().getPartSize();
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
      String url = this.buildDownloadUrl(key);
      HttpResponse response = HttpRequest.get(url)
         .header("Range", "bytes=" + start + "-" + end)
         .timeout((int) (long) this.qiniuOssConfig.getClientConfig().getReadTimeout())
         .execute();
      return response.bodyStream();
   }

   @Override
   public void delete(String targetName) {
      try {
         this.bucketManager.delete(this.getBucket(), this.getKey(targetName, false));
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   @Override
   public void copy(String sourceName, String targetName, Boolean isOverride) {
      try {
         String bucket = this.getBucket();
         this.bucketManager.copy(bucket, this.getKey(sourceName, false), bucket, this.getKey(targetName, false), isOverride);
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
            FileListing fileListing = this.bucketManager.listFiles(this.getBucket(), realPrefix, "", 1000, "/");
            List<OssInfo> fileOssInfos = new ArrayList<>();
            List<OssInfo> directoryInfos = new ArrayList<>();

            if (ObjUtil.isNotEmpty(fileListing.items)) {
               for (FileInfo item : fileListing.items) {
                  String childKey = OssPathUtil.replaceKey(item.key, this.getBasePath(), true);
                  fileOssInfos.add(this.getInfo(childKey, false));
               }
            }

            if (ObjUtil.isNotEmpty(fileListing.commonPrefixes)) {
               for (String commonPrefix : fileListing.commonPrefixes) {
                  String childKey = OssPathUtil.replaceKey(commonPrefix, this.getBasePath(), true);
                  directoryInfos.add(this.getInfo(childKey, true));
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

   @Override
   public String getBasePath() {
      return this.qiniuOssConfig.getBasePath();
   }

   @Override
   public Map<String, Object> getClientObject() {
      Map<String, Object> clientObjectMap = new HashMap<>();
      clientObjectMap.put(QINIU_AUTH_NAME, this.auth);
      clientObjectMap.put(QINIU_UPLOAD_MANAGER_NAME, this.uploadManager);
      clientObjectMap.put(QINIU_BUCKET_MANAGER_NAME, this.bucketManager);
      return clientObjectMap;
   }

   private String getBucket() {
      return this.qiniuOssConfig.getBucketName();
   }

   /**
    * 构建签名后的下载地址，公开空间签名参数会被忽略，私有空间签名生效。
    */
   private String buildDownloadUrl(String key) {
      String domain = this.qiniuOssConfig.getDomain();
      if (StrUtil.isBlank(domain)) {
         throw new OssException("七牛云下载需要配置 domain（存储空间访问域名）");
      }

      if (!StrUtil.startWithAny(domain, "http://", "https://")) {
         domain = (Boolean.TRUE.equals(this.qiniuOssConfig.getUseHttps()) ? "https://" : "http://") + domain;
      }

      String baseUrl = StrUtil.removeSuffix(domain, "/") + "/" + key;
      return this.auth.privateDownloadUrl(baseUrl);
   }

   public OssInfo getBaseInfo(String targetName) {
      String key = this.getKey(targetName, false);
      OssInfo ossInfo;
      if (this.isFile(key)) {
         ossInfo = new FileOssInfo();

         try {
            FileInfo fileInfo = this.bucketManager.stat(this.getBucket(), key);
            ossInfo.setLength(fileInfo.fsize);
            String lastUpdateTime = DateUtil.date(fileInfo.putTime / 10000L).toString("yyyy-MM-dd HH:mm:ss");
            ossInfo.setLastUpdateTime(lastUpdateTime);
            ossInfo.setCreateTime(lastUpdateTime);
            ossInfo.setUrl(this.buildDownloadUrl(key));
         } catch (Exception e) {
            LogUtils.error("获取{}文件属性失败", new Object[]{key, e});
         }
      } else {
         ossInfo = new DirectoryOssInfo();
      }

      ossInfo.setName(StrUtil.equals(targetName, "/") ? targetName : FileNameUtil.getName(targetName));
      ossInfo.setPath(OssPathUtil.replaceKey(targetName, this.qiniuOssConfig.getBasePath(), true));
      return ossInfo;
   }

   public Auth getAuth() {
      return this.auth;
   }

   public void setAuth(Auth auth) {
      this.auth = auth;
   }

   public UploadManager getUploadManager() {
      return this.uploadManager;
   }

   public void setUploadManager(UploadManager uploadManager) {
      this.uploadManager = uploadManager;
   }

   public BucketManager getBucketManager() {
      return this.bucketManager;
   }

   public void setBucketManager(BucketManager bucketManager) {
      this.bucketManager = bucketManager;
   }

   public QiniuOssConfig getQiniuOssConfig() {
      return this.qiniuOssConfig;
   }

   public void setQiniuOssConfig(QiniuOssConfig qiniuOssConfig) {
      this.qiniuOssConfig = qiniuOssConfig;
   }
}
