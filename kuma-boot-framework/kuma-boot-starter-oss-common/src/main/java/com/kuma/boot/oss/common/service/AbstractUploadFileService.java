package com.kuma.boot.oss.common.service;

import com.kuma.boot.oss.common.exception.UploadFileException;
import com.kuma.boot.oss.common.model.UploadFileInfo;
import com.kuma.boot.oss.common.util.FileUtil;
import java.io.File;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractUploadFileService implements UploadFileService {
   public static final String UPLOAD_FILE_SPLIT = ".";
   public static final String UPLOAD_FILE_ERROR_MESSAGE = "缺少后缀名";

   public UploadFileInfo upload(File file) throws UploadFileException {
      UploadFileInfo uploadFileInfo = FileUtil.getFileInfo(file);
      if (!uploadFileInfo.getName().contains(".")) {
         throw new UploadFileException("缺少后缀名");
      } else {
         return this.uploadFile(file, uploadFileInfo);
      }
   }

   public UploadFileInfo upload(File file, String fileName) throws UploadFileException {
      UploadFileInfo uploadFileInfo = FileUtil.getFileInfo(file);
      if (!uploadFileInfo.getName().contains(".")) {
         throw new UploadFileException("缺少后缀名");
      } else {
         uploadFileInfo.setName(fileName);
         return this.uploadFile(file, uploadFileInfo);
      }
   }

   public UploadFileInfo upload(MultipartFile file) throws UploadFileException {
      UploadFileInfo uploadFileInfo = FileUtil.getMultipartFileInfo(file);
      if (!uploadFileInfo.getName().contains(".")) {
         throw new UploadFileException("缺少后缀名");
      } else {
         return this.uploadFile(file, uploadFileInfo);
      }
   }

   public UploadFileInfo upload(MultipartFile file, String fileName) throws UploadFileException {
      UploadFileInfo uploadFileInfo = FileUtil.getMultipartFileInfo(file);
      if (!uploadFileInfo.getName().contains(".")) {
         throw new UploadFileException("缺少后缀名");
      } else {
         uploadFileInfo.setName(fileName);
         return this.uploadFile(file, uploadFileInfo);
      }
   }

   protected abstract UploadFileInfo uploadFile(MultipartFile file, UploadFileInfo uploadFileInfo) throws UploadFileException;

   protected abstract UploadFileInfo uploadFile(File file, UploadFileInfo uploadFileInfo) throws UploadFileException;
}
