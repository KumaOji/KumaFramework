package com.kuma.boot.oss.minio.service;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.oss.common.model.UploadFileInfo;
import com.kuma.boot.oss.common.service.AbstractUploadFileService;
import com.kuma.boot.oss.common.util.FileUtil;
import com.kuma.boot.oss.minio.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.web.multipart.MultipartFile;

public class MinioUploadFileServiceImpl extends AbstractUploadFileService {
   private final MinioProperties properties;
   private final MinioClient minioClient;

   public MinioUploadFileServiceImpl(MinioProperties properties, MinioClient minioClient) {
      this.properties = properties;
      this.minioClient = minioClient;
   }

   protected UploadFileInfo uploadFile(MultipartFile file, UploadFileInfo uploadFileInfo) {
      return null;
   }

   protected UploadFileInfo uploadFile(File file, UploadFileInfo uploadFileInfo) {
      return null;
   }

   public UploadFileInfo delete(UploadFileInfo uploadFileInfo) {
      return null;
   }

   public Map getPolicy(String fileName, ZonedDateTime time) {
      PostPolicy postPolicy = new PostPolicy(this.properties.getBucketName(), time);
      postPolicy.addEqualsCondition("key", fileName);

      try {
         Map<String, String> map = this.minioClient.getPresignedPostFormData(postPolicy);
         HashMap<String, String> map1 = new HashMap();
         map.forEach((k, v) -> map1.put(k.replaceAll("-", ""), v));
         String var10002 = this.properties.getUrl();
         map1.put("host", var10002 + "/" + this.properties.getBucketName());
         return map1;
      } catch (InsufficientDataException | InternalException | InvalidResponseException | InvalidKeyException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | ErrorResponseException e) {
         LogUtils.error(e);
         return null;
      }
   }

   public String getPolicyUrl(String objectName, Method method, int time, TimeUnit timeUnit) {
      try {
         return this.minioClient.getPresignedObjectUrl((GetPresignedObjectUrlArgs)((GetPresignedObjectUrlArgs.Builder)((GetPresignedObjectUrlArgs.Builder)GetPresignedObjectUrlArgs.builder().method(method).bucket(this.properties.getBucketName())).object(objectName)).expiry(time, timeUnit).build());
      } catch (InternalException | InsufficientDataException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException | ServerException | ErrorResponseException e) {
         LogUtils.error(e);
         return null;
      }
   }

   public void upload1(MultipartFile file, String fileName) {
      try {
         InputStream inputStream = file.getInputStream();
         this.minioClient.putObject((PutObjectArgs)((PutObjectArgs.Builder)((PutObjectArgs.Builder)PutObjectArgs.builder().bucket(this.properties.getBucketName())).object(fileName)).stream(inputStream, file.getSize(), -1L).contentType(file.getContentType()).build());
      } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | ErrorResponseException e) {
         LogUtils.error(e);
      }

   }

   public String getUrl(String objectName, int time, TimeUnit timeUnit) {
      String url = null;

      try {
         url = this.minioClient.getPresignedObjectUrl((GetPresignedObjectUrlArgs)((GetPresignedObjectUrlArgs.Builder)((GetPresignedObjectUrlArgs.Builder)GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(this.properties.getBucketName())).object(objectName)).expiry(time, timeUnit).build());
      } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException | ServerException | ErrorResponseException e) {
         LogUtils.error(e);
      }

      return url;
   }

   public void uploadFile(MultipartFile file, String bucketName, String fileName) {
      if (null == file || 0L == file.getSize()) {
         LogUtils.error("\u6587\u4ef6\u4e0d\u80fd\u4e3a\u7a7a", new Object[0]);
      }

      this.bucketExists(bucketName);
      if (file != null) {
         String originalFilename = file.getOriginalFilename();

         assert originalFilename != null;

         try {
            InputStream inputStream = file.getInputStream();
            this.minioClient.putObject((PutObjectArgs)((PutObjectArgs.Builder)((PutObjectArgs.Builder)PutObjectArgs.builder().bucket(bucketName)).object(fileName)).stream(inputStream, file.getSize(), -1L).contentType(file.getContentType()).build());
         } catch (Exception e) {
            LogUtils.error(e.getMessage(), new Object[0]);
         }
      }

   }

   public void uploadFiles(String filePath, String bucketName, String fileName) throws IOException {
      MultipartFile file = FileUtil.fileToMultipartFile(new File(filePath));

      try {
         InputStream inputStream = file.getInputStream();
         this.minioClient.putObject((PutObjectArgs)((PutObjectArgs.Builder)((PutObjectArgs.Builder)PutObjectArgs.builder().bucket(bucketName)).object(fileName)).stream(inputStream, file.getSize(), -1L).contentType(file.getContentType()).build());
      } catch (Exception e) {
         LogUtils.error(e.getMessage(), new Object[0]);
      }

   }

   public void downFile(String fileName, String bucketName) {
      if (!fileName.contains("jpg") && !fileName.contains("png")) {
         InputStream inputStream = null;

         try {
            inputStream = this.minioClient.getObject((GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(bucketName)).object(fileName)).build());
            HttpServletResponse response = RequestUtils.getResponse();
            HttpServletRequest request = RequestUtils.getRequest();

            try {
               BufferedInputStream bis = new BufferedInputStream(inputStream);

               assert response != null;

               response.setCharacterEncoding("UTF-8");
               response.setContentType("text/plain");
               if (fileName.contains(".svg")) {
                  response.setContentType("image/svg+xml");
               }

               String codeFileName = "";

               assert request != null;

               String agent = request.getHeader("USER-AGENT").toLowerCase();
               if (!agent.contains("msie") && !agent.contains("trident")) {
                  if (agent.contains("mozilla")) {
                     codeFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
                  } else {
                     codeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                  }
               } else {
                  codeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
               }

               String var10002 = new String(codeFileName.getBytes(), StandardCharsets.UTF_8);
               response.setHeader("Content-Disposition", "attachment;filename=" + var10002);
               OutputStream os = response.getOutputStream();
               byte[] buff = new byte[8192];

               int i;
               while((i = bis.read(buff)) != -1) {
                  os.write(buff, 0, i);
               }

               os.flush();
            } catch (Exception e) {
               LogUtils.error(e);
            } finally {
               try {
                  inputStream.close();
               } catch (IOException e) {
                  LogUtils.error(e);
               }

            }
         } catch (Exception e) {
            LogUtils.error(e.getMessage(), new Object[0]);
         } finally {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (IOException e) {
                  LogUtils.error(e);
               }
            }

         }

      } else {
         this.dowloadMinioFile(fileName, bucketName);
      }
   }

   public void dowloadMinioFile(String fileName, String bucketName) {
      try {
         InputStream inputStream = this.minioClient.getObject((GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(bucketName)).object(fileName)).build());
         ServletOutputStream outputStream1 = ((HttpServletResponse)Objects.requireNonNull(RequestUtils.getResponse())).getOutputStream();
         OutputStream outputStream = new BufferedOutputStream(outputStream1);
         byte[] buff = new byte[1024];

         int n;
         while((n = inputStream.read(buff)) != -1) {
            outputStream.write(buff, 0, n);
         }

         outputStream.flush();
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }

   public String getFile(String fileName, String bucketName) {
      String objectUrl = null;

      try {
         objectUrl = this.minioClient.getPresignedObjectUrl((GetPresignedObjectUrlArgs)((GetPresignedObjectUrlArgs.Builder)((GetPresignedObjectUrlArgs.Builder)GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName)).object(fileName)).build());
      } catch (Exception e) {
         LogUtils.error(e.getMessage(), new Object[0]);
      }

      return objectUrl;
   }

   public InputStream downloadMinio(String fileName, String bucketName) {
      try {
         return this.minioClient.getObject((GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(bucketName)).object(fileName)).build());
      } catch (Exception e) {
         LogUtils.error(e);
         LogUtils.info(e.getMessage(), new Object[0]);
         return null;
      }
   }

   public List<String> getAllBuckets() throws Exception {
      return this.minioClient.listBuckets().stream().map(Bucket::name).toList();
   }

   public void removeBucket(String bucketName) throws Exception {
      this.minioClient.removeBucket((RemoveBucketArgs)((RemoveBucketArgs.Builder)RemoveBucketArgs.builder().bucket(bucketName)).build());
   }

   public boolean removeFile(String bucketName, String name) {
      boolean isOK = true;

      try {
         this.minioClient.removeObject((RemoveObjectArgs)((RemoveObjectArgs.Builder)((RemoveObjectArgs.Builder)RemoveObjectArgs.builder().bucket(bucketName)).object(name)).build());
      } catch (Exception e) {
         LogUtils.error(e);
         isOK = false;
      }

      return isOK;
   }

   public boolean bucketExists(String name) {
      boolean isExist = false;

      try {
         isExist = this.minioClient.bucketExists(getBucketExistsArgs(name));
      } catch (Exception e) {
         LogUtils.error(e);
      }

      return isExist;
   }

   public void bucketExistsCreate(String name) {
      try {
         this.minioClient.bucketExists(getBucketExistsArgs(name));
         this.minioClient.makeBucket(getMakeBucketArgs(name));
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }

   public boolean putFolder(String filePath, String bucketName, String objectName) {
      boolean flag = false;

      try {
         if (!FileUtil.fileIsExists(filePath)) {
            return false;
         }

         FileUtil.toZip(filePath + ".zip", true, new String[]{filePath});
         MultipartFile multipartFile = FileUtil.fileToMultipartFile(new File(filePath + ".zip"));
         this.uploadFile(multipartFile, bucketName, objectName + ".zip");
         flag = true;
      } catch (Exception e) {
         LogUtils.error(e);
      }

      return flag;
   }

   public void streamToDown(String bucketName, String filePath, String objectName) {
      try {
         InputStream stream = this.minioClient.getObject((GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(bucketName)).object(objectName)).build());
         FileUtil.writeFile(stream, filePath, objectName);
      } catch (Exception e) {
         LogUtils.error(e);
         LogUtils.info(e.getMessage(), new Object[0]);
      }

   }

   public List<Item> getFileList(String bucketName) {
      List<Item> list = new ArrayList();

      try {
         for(Result<Item> result : this.minioClient.listObjects((ListObjectsArgs)((ListObjectsArgs.Builder)ListObjectsArgs.builder().bucket(bucketName)).build())) {
            Item item = (Item)result.get();
            list.add(item);
         }
      } catch (Exception e) {
         LogUtils.error(e.getMessage(), new Object[0]);
      }

      return list;
   }

   public List getFileList(String bucketName, String type) {
      List<Item> list = new ArrayList();

      try {
         for(Result<Item> result : this.minioClient.listObjects((ListObjectsArgs)((ListObjectsArgs.Builder)ListObjectsArgs.builder().bucket(bucketName)).prefix(type).recursive(true).build())) {
            Item item = (Item)result.get();
            list.add(item);
         }
      } catch (Exception e) {
         LogUtils.error(e.getMessage(), new Object[0]);
      }

      return list;
   }

   public void copyObject(String bucketName, String objectName, String copyToBucketName, String copyToObjectName) {
      try {
         this.minioClient.copyObject((CopyObjectArgs)((CopyObjectArgs.Builder)((CopyObjectArgs.Builder)CopyObjectArgs.builder().source((CopySource)((CopySource.Builder)((CopySource.Builder)CopySource.builder().bucket(bucketName)).object(objectName)).build()).bucket(copyToBucketName)).object(copyToObjectName)).build());
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }

   public static MakeBucketArgs getMakeBucketArgs(String name) {
      return (MakeBucketArgs)((MakeBucketArgs.Builder)MakeBucketArgs.builder().bucket(name)).build();
   }

   public static BucketExistsArgs getBucketExistsArgs(String name) {
      return (BucketExistsArgs)((BucketExistsArgs.Builder)BucketExistsArgs.builder().bucket(name)).build();
   }

   public static SetBucketPolicyArgs getSetBucketPolicyArgs(String name) {
      return (SetBucketPolicyArgs)((SetBucketPolicyArgs.Builder)SetBucketPolicyArgs.builder().bucket(name)).build();
   }

   public void downToLocal(String bucketName, String filePath, String objectName) {
      try {
         InputStream stream = this.minioClient.getObject((GetObjectArgs)((GetObjectArgs.Builder)((GetObjectArgs.Builder)GetObjectArgs.builder().bucket(bucketName)).object(objectName)).build());
         FileUtil.write(stream, filePath, objectName);
      } catch (Exception e) {
         LogUtils.error(e);
         LogUtils.info(e.getMessage(), new Object[0]);
      }

   }
}
