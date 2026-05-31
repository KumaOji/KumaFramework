package com.kuma.boot.oss.common.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.constant.OssConstant;
import com.kuma.boot.oss.common.exception.OssException;
import com.kuma.boot.oss.common.model.OssInfo;
import com.kuma.boot.oss.common.model.SliceConfig;
import com.kuma.boot.oss.common.model.UploadFileInfo;
import com.kuma.boot.oss.common.model.download.DownloadCheckPoint;
import com.kuma.boot.oss.common.model.download.DownloadObjectStat;
import com.kuma.boot.oss.common.model.download.DownloadPart;
import com.kuma.boot.oss.common.model.download.DownloadPartResult;
import com.kuma.boot.oss.common.model.upload.UpLoadCheckPoint;
import com.kuma.boot.oss.common.model.upload.UpLoadPartEntityTag;
import com.kuma.boot.oss.common.model.upload.UpLoadPartResult;
import com.kuma.boot.oss.common.model.upload.UploadPart;
import com.kuma.boot.oss.common.util.FileUtil;
import com.kuma.boot.oss.common.util.OssPathUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.springframework.web.multipart.MultipartFile;

public interface StandardOssClient {
   default OssInfo upLoadWithMultipartFile(MultipartFile multipartFile) {
      try {
         UploadFileInfo uploadFileInfo = FileUtil.getMultipartFileInfo(multipartFile);
         OssInfo ossInfo = this.upLoadWithInputStream(multipartFile.getInputStream(), OssPathUtil.getTargetName(multipartFile));
         ossInfo.setUploadFileInfo(uploadFileInfo);
         return ossInfo;
      } catch (IOException e) {
         LogUtils.error(e);
         throw new RuntimeException(e);
      }
   }

   default OssInfo upLoadWithPath(String path) {
      return this.upLoadWithFile(new File(path));
   }

   default OssInfo upLoadWithFile(File file) {
      try {
         UploadFileInfo uploadFileInfo = FileUtil.getFileInfo(file);
         OssInfo ossInfo = this.upLoadWithInputStream(new FileInputStream(file), OssPathUtil.getTargetName(file));
         ossInfo.setUploadFileInfo(uploadFileInfo);
         return ossInfo;
      } catch (IOException e) {
         LogUtils.error(e);
         throw new RuntimeException(e);
      }
   }

   default OssInfo upLoadWithInputStream(InputStream is, String targetName) {
      return this.upLoad(is, targetName, true);
   }

   OssInfo upLoad(InputStream is, String targetName, Boolean isOverride);

   default OssInfo upLoadCheckPoint(String file, String targetName) {
      return this.upLoadCheckPoint(new File(file), targetName);
   }

   default OssInfo upLoadCheckPoint(String filePath) {
      File file = new File(filePath);
      return this.upLoadCheckPoint(file, OssPathUtil.getTargetName(file));
   }

   OssInfo upLoadCheckPoint(File file, String targetName);

   default OssInfo uploadFile(File upLoadFile, String targetName, SliceConfig slice, String ossType) {
      String var10000 = upLoadFile.getPath();
      String checkpointFile = var10000 + "." + ossType;
      UpLoadCheckPoint upLoadCheckPoint = new UpLoadCheckPoint();

      try {
         upLoadCheckPoint.load(checkpointFile);
      } catch (Exception var13) {
         FileUtil.deleteFile(checkpointFile);
      }

      if (!upLoadCheckPoint.isValid()) {
         this.prepareUpload(upLoadCheckPoint, upLoadFile, targetName, checkpointFile, slice);
         FileUtil.deleteFile(checkpointFile);
      }

      ExecutorService executorService = Executors.newFixedThreadPool(slice.getTaskNum());
      @SuppressWarnings("unchecked")
      List<Future<UpLoadPartResult>> futures = new ArrayList<>();

      for(int i = 0; i < upLoadCheckPoint.getUploadParts().size(); ++i) {
         if (!((UploadPart)upLoadCheckPoint.getUploadParts().get(i)).isCompleted()) {
            futures.add(executorService.submit(new UploadPartTask(this, upLoadCheckPoint, i)));
         }
      }

      executorService.shutdown();

      for(Future future : futures) {
         try {
            UpLoadPartResult partResult = (UpLoadPartResult)future.get();
            if (partResult.isFailed()) {
               throw partResult.getException();
            }
         } catch (Exception e) {
            throw new OssException(e);
         }
      }

      try {
         if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
         }
      } catch (InterruptedException e) {
         throw new OssException("关闭线程池失败", e);
      }

      List<UpLoadPartEntityTag> partEntityTags = upLoadCheckPoint.getPartEntityTags();
      this.completeUpload(upLoadCheckPoint, partEntityTags);
      return this.getInfo(targetName);
   }

   default void completeUpload(UpLoadCheckPoint upLoadCheckPoint, List partEntityTags) {
      FileUtil.deleteFile(upLoadCheckPoint.getCheckpointFile());
   }

   default void prepareUpload(UpLoadCheckPoint uploadCheckPoint, File upLoadFile, String targetName, String checkpointFile, SliceConfig slice) {
      throw new OssException("初始化断点续传对象未实现，默认不支持此方法");
   }

   default ArrayList splitUploadFile(long fileSize, long partSize) {
      ArrayList<UploadPart> parts = new ArrayList<>();
      long partNum = fileSize / partSize;
      if (partNum >= OssConstant.DEFAULT_PART_NUM) {
         partSize = fileSize / (OssConstant.DEFAULT_PART_NUM - 1L);
         partNum = fileSize / partSize;
      }

      for(long i = 0L; i < partNum; ++i) {
         UploadPart part = new UploadPart();
         part.setNumber((int)(i + 1L));
         part.setOffset(i * partSize);
         part.setSize(partSize);
         part.setCompleted(false);
         parts.add(part);
      }

      if (fileSize % partSize > 0L) {
         UploadPart part = new UploadPart();
         part.setNumber(parts.size() + 1);
         part.setOffset((long)parts.size() * partSize);
         part.setSize(fileSize % partSize);
         part.setCompleted(false);
         parts.add(part);
      }

      return parts;
   }

   default UpLoadPartResult uploadPart(UpLoadCheckPoint upLoadCheckPoint, int partNum, InputStream inputStream) {
      UploadPart uploadPart = (UploadPart)upLoadCheckPoint.getUploadParts().get(partNum);
      long partSize = uploadPart.getSize();
      UpLoadPartResult partResult = new UpLoadPartResult(partNum + 1, uploadPart.getOffset(), partSize);
      partResult.setFailed(true);
      return partResult;
   }

   void downLoad(OutputStream os, String targetName);

   default void downLoadCheckPoint(String localFile, String targetName) {
      this.downLoadCheckPoint(new File(localFile), targetName);
   }

   void downLoadCheckPoint(File localFile, String targetName);

   default void downLoadFile(File localFile, String targetName, SliceConfig slice, String ossType) {
      String var10000 = localFile.getPath();
      String checkpointFile = var10000 + "." + ossType;
      DownloadCheckPoint downloadCheckPoint = new DownloadCheckPoint();

      try {
         downloadCheckPoint.load(checkpointFile);
      } catch (Exception var14) {
         FileUtil.deleteFile(checkpointFile);
      }

      DownloadObjectStat downloadObjectStat = this.getDownloadObjectStat(targetName);
      if (!downloadCheckPoint.isValid(downloadObjectStat)) {
         this.prepareDownload(downloadCheckPoint, localFile, targetName, checkpointFile);
         FileUtil.deleteFile(checkpointFile);
      }

      ExecutorService executorService = Executors.newFixedThreadPool(slice.getTaskNum());
      @SuppressWarnings("unchecked")
      List<Future<DownloadPartResult>> futures = new ArrayList<>();

      for(int i = 0; i < downloadCheckPoint.getDownloadParts().size(); ++i) {
         if (!((DownloadPart)downloadCheckPoint.getDownloadParts().get(i)).isCompleted()) {
            futures.add(executorService.submit(new DownloadPartTask(this, downloadCheckPoint, i)));
         }
      }

      executorService.shutdown();

      for(Future future : futures) {
         try {
            DownloadPartResult partResult = (DownloadPartResult)future.get();
            if (partResult.isFailed()) {
               throw partResult.getException();
            }
         } catch (Exception e) {
            throw new OssException(e);
         }
      }

      try {
         if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
         }
      } catch (InterruptedException e) {
         throw new OssException("关闭线程池失败", e);
      }

      cn.hutool.core.io.FileUtil.rename(new File(downloadCheckPoint.getTempDownloadFile()), downloadCheckPoint.getDownloadFile(), true);
      cn.hutool.core.io.FileUtil.del(downloadCheckPoint.getCheckPointFile());
   }

   default DownloadObjectStat getDownloadObjectStat(String targetName) {
      throw new OssException("获取下载对象状态方法未实现，默认不支持此方法");
   }

   default void prepareDownload(DownloadCheckPoint downloadCheckPoint, File localFile, String targetName, String checkpointFile) {
      throw new OssException("初始化断点续传下载对象方法未实现，默认不支持此方法");
   }

   default ArrayList splitDownloadFile(long start, long objectSize, long partSize) {
      ArrayList<DownloadPart> parts = new ArrayList<>();
      long partNum = objectSize / partSize;
      if (partNum >= OssConstant.DEFAULT_PART_NUM) {
         partSize = objectSize / (OssConstant.DEFAULT_PART_NUM - 1L);
      }

      long offset = 0L;

      for(int i = 0; offset < objectSize; ++i) {
         DownloadPart part = new DownloadPart();
         part.setIndex(i);
         part.setStart(offset + start);
         part.setEnd(this.getPartEnd(offset, objectSize, partSize) + start);
         part.setFileStart(offset);
         parts.add(part);
         offset += partSize;
      }

      return parts;
   }

   default long getPartEnd(long begin, long total, long per) {
      return begin + per > total ? total - 1L : begin + per - 1L;
   }

   default ArrayList splitDownloadOneFile() {
      ArrayList<DownloadPart> parts = new ArrayList<>();
      DownloadPart part = new DownloadPart();
      part.setIndex(0);
      part.setStart(0L);
      part.setEnd(-1L);
      part.setFileStart(0L);
      parts.add(part);
      return parts;
   }

   default long[] getDownloadSlice(long[] range, long totalSize) {
      long start = 0L;
      long size = totalSize;
      if (range != null && range.length == 2 && totalSize >= 1L && (range[0] >= 0L || range[1] >= 0L) && (range[0] <= 0L || range[1] <= 0L || range[0] <= range[1]) && range[0] < totalSize) {
         long begin = range[0];
         long end = range[1];
         if (range[0] < 0L) {
            begin = 0L;
         }

         if (range[1] < 0L || range[1] >= totalSize) {
            end = totalSize - 1L;
         }

         start = begin;
         size = end - begin + 1L;
      }

      return new long[]{start, size};
   }

   default void createDownloadTemp(String downloadTempFile, long length) {
      File file = new File(downloadTempFile);
      RandomAccessFile rf = null;

      try {
         rf = new RandomAccessFile(file, "rw");
         rf.setLength(length);
      } catch (Exception var10) {
         throw new OssException("创建下载缓存文件失败");
      } finally {
         IoUtil.close(rf);
      }

   }

   default InputStream downloadPart(String key, long start, long end) throws Exception {
      throw new OssException("下载文件分片方法未实现，默认不支持此方法");
   }

   void delete(String targetName);

   default void copy(String sourceName, String targetName) {
      this.copy(sourceName, targetName, true);
   }

   void copy(String sourceName, String targetName, Boolean isOverride);

   default void move(String sourceName, String targetName) {
      this.move(sourceName, targetName, true);
   }

   default void move(String sourceName, String targetName, Boolean isOverride) {
      this.copy(sourceName, targetName, isOverride);
      this.delete(sourceName);
   }

   default void rename(String sourceName, String targetName) {
      this.rename(sourceName, targetName, true);
   }

   default void rename(String sourceName, String targetName, Boolean isOverride) {
      this.move(sourceName, targetName, isOverride);
   }

   default OssInfo getInfo(String targetName) {
      return this.getInfo(targetName, false);
   }

   OssInfo getInfo(String targetName, Boolean isRecursion);

   default Boolean isExist(String targetName) {
      OssInfo info = this.getInfo(targetName);
      return ObjUtil.isNotEmpty(info) && ObjUtil.isNotEmpty(info.getName());
   }

   default Boolean isFile(String targetName) {
      String name = FileNameUtil.getName(targetName);
      return StrUtil.indexOf(name, '.') > 0;
   }

   default Boolean isDirectory(String targetName) {
      return !this.isFile(targetName);
   }

   Map getClientObject();

   default String getKey(String targetName, Boolean isAbsolute) {
      String key = OssPathUtil.convertPath(this.getBasePath() + targetName, isAbsolute);
      if (cn.hutool.core.io.FileUtil.isWindows() && isAbsolute && key.contains(":") && key.startsWith("/")) {
         key = key.substring(1);
      }

      return key;
   }

   String getBasePath();

   public static class UploadPartTask implements Callable {
      private StandardOssClient ossClient;
      private UpLoadCheckPoint upLoadCheckPoint;
      private int partNum;

      public UploadPartTask(StandardOssClient ossClient, UpLoadCheckPoint upLoadCheckPoint, int partNum) {
         this.ossClient = ossClient;
         this.upLoadCheckPoint = upLoadCheckPoint;
         this.partNum = partNum;
      }

      public UpLoadPartResult call() {
         InputStream inputStream = cn.hutool.core.io.FileUtil.getInputStream(this.upLoadCheckPoint.getUploadFile());
         UpLoadPartResult upLoadPartResult = this.ossClient.uploadPart(this.upLoadCheckPoint, this.partNum, inputStream);
         if (!upLoadPartResult.isFailed()) {
            this.upLoadCheckPoint.update(this.partNum, upLoadPartResult.getEntityTag(), true);
            this.upLoadCheckPoint.dump();
         }

         return upLoadPartResult;
      }

      public StandardOssClient getOssClient() {
         return this.ossClient;
      }

      public void setOssClient(StandardOssClient ossClient) {
         this.ossClient = ossClient;
      }

      public UpLoadCheckPoint getUpLoadCheckPoint() {
         return this.upLoadCheckPoint;
      }

      public void setUpLoadCheckPoint(UpLoadCheckPoint upLoadCheckPoint) {
         this.upLoadCheckPoint = upLoadCheckPoint;
      }

      public int getPartNum() {
         return this.partNum;
      }

      public void setPartNum(int partNum) {
         this.partNum = partNum;
      }
   }

   public static class DownloadPartTask implements Callable {
      StandardOssClient ossClient;
      DownloadCheckPoint downloadCheckPoint;
      int partNum;

      public DownloadPartTask(StandardOssClient ossClient, DownloadCheckPoint downloadCheckPoint, int partNum) {
         this.ossClient = ossClient;
         this.downloadCheckPoint = downloadCheckPoint;
         this.partNum = partNum;
      }

      public DownloadPartResult call() {
         DownloadPartResult partResult = null;
         RandomAccessFile output = null;
         InputStream content = null;

         try {
            DownloadPart downloadPart = (DownloadPart)this.downloadCheckPoint.getDownloadParts().get(this.partNum);
            partResult = new DownloadPartResult(this.partNum + 1, downloadPart.getStart(), downloadPart.getEnd());
            output = new RandomAccessFile(this.downloadCheckPoint.getTempDownloadFile(), "rw");
            output.seek(downloadPart.getFileStart());
            content = this.ossClient.downloadPart(this.downloadCheckPoint.getKey(), downloadPart.getStart(), downloadPart.getEnd());
            long partSize = downloadPart.getEnd() - downloadPart.getStart();
            byte[] buffer = new byte[Convert.toInt(partSize)];
            int bytesRead = 0;

            while((bytesRead = content.read(buffer)) != -1) {
               output.write(buffer, 0, bytesRead);
            }

            partResult.setLength(downloadPart.getLength());
            this.downloadCheckPoint.update(this.partNum, true);
            this.downloadCheckPoint.dump();
         } catch (Exception e) {
            partResult.setException(e);
            partResult.setFailed(true);
         } finally {
            IoUtil.close(output);
            IoUtil.close(content);
         }

         return partResult;
      }

      public StandardOssClient getOssClient() {
         return this.ossClient;
      }

      public void setOssClient(StandardOssClient ossClient) {
         this.ossClient = ossClient;
      }

      public DownloadCheckPoint getDownloadCheckPoint() {
         return this.downloadCheckPoint;
      }

      public void setDownloadCheckPoint(DownloadCheckPoint downloadCheckPoint) {
         this.downloadCheckPoint = downloadCheckPoint;
      }

      public int getPartNum() {
         return this.partNum;
      }

      public void setPartNum(int partNum) {
         this.partNum = partNum;
      }
   }
}
