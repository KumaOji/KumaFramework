package com.kuma.boot.oss.common.service;

import com.kuma.boot.oss.common.exception.UploadFileException;
import com.kuma.boot.oss.common.model.UploadFileInfo;
import java.io.File;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFileService {
   UploadFileInfo upload(File file) throws UploadFileException;

   UploadFileInfo upload(File file, String fileKey) throws UploadFileException;

   UploadFileInfo upload(MultipartFile file) throws UploadFileException;

   UploadFileInfo upload(MultipartFile file, String fileKey) throws UploadFileException;

   UploadFileInfo delete(UploadFileInfo uploadFileInfo) throws UploadFileException;
}
