package com.kuma.boot.oss.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.exception.UploadFileException;
import com.kuma.boot.oss.common.exception.UploadFileTypeException;
import com.kuma.boot.oss.common.model.UploadFileInfo;
import jakarta.activation.MimetypesFileTypeMap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
   public static final String UPLOAD_FILE_PARSE_ERROR_MESSAGE = "解析文件失败";
   public static final String UPLOAD_FILE_SAVE_ERROR_MESSAGE = "保存文件失败";
   public static final String UPLOAD_FILE_FORMAT_ERROR_MESSAGE = "文件格式错误";
   public static final String UPLOAD_FILE_DATA_FORMAT = "yyyy/MM/dd/HH/mm";
   public static final String UPLOAD_FILE_TOO_BIG = "文件过大";
   private static final int BUFFER_SIZE = 2048;

   private FileUtil() {
   }

   public static UploadFileInfo getMultipartFileInfo(MultipartFile multipartFile) {
      try {
         UploadFileInfo uploadFileInfo = new UploadFileInfo();
         String md5 = fileMd5(multipartFile.getInputStream());
         uploadFileInfo.setFileMd5(md5);
         String originalFilename = multipartFile.getOriginalFilename();
         uploadFileInfo.setOriginalFileName(originalFilename);

         assert originalFilename != null;

         File file = new File(originalFilename);
         cn.hutool.core.io.FileUtil.copyFile(multipartFile.getInputStream(), file, new StandardCopyOption[0]);
         String extName = cn.hutool.core.io.FileUtil.extName(file);
         uploadFileInfo.setName(extractFilename(originalFilename, extName));
         uploadFileInfo.setContentType(multipartFile.getContentType());
         uploadFileInfo.setImg(isImage(file));
         uploadFileInfo.setSize(multipartFile.getSize());
         uploadFileInfo.setFileType(FileTypeUtil.getType(multipartFile.getInputStream()));
         return uploadFileInfo;
      } catch (IOException var6) {
         throw new UploadFileException("解析文件失败");
      }
   }

   public static UploadFileInfo getFileInfo(File file) {
      try {
         UploadFileInfo uploadFileInfo = new UploadFileInfo();
         String md5 = fileMd5(new FileInputStream(file));
         uploadFileInfo.setOriginalFileName(file.getName());
         uploadFileInfo.setName(file.getName());
         uploadFileInfo.setFileMd5(md5);
         uploadFileInfo.setContentType((new MimetypesFileTypeMap()).getContentType(file));
         uploadFileInfo.setImg(isImage(file));
         uploadFileInfo.setSize(cn.hutool.core.io.FileUtil.size(file));
         uploadFileInfo.setFileType(cn.hutool.core.io.FileUtil.getType(file));
         return uploadFileInfo;
      } catch (Exception var3) {
         throw new UploadFileException("解析文件失败");
      }
   }

   public static String fileMd5(InputStream inputStream) {
      return DigestUtil.md5Hex(inputStream);
   }

   public static String saveFile(MultipartFile file, String path) {
      try {
         File targetFile = new File(path);
         if (targetFile.exists()) {
            return path;
         } else {
            if (!targetFile.getParentFile().exists()) {
               targetFile.getParentFile().mkdirs();
            }

            file.transferTo(targetFile);
            return path;
         }
      } catch (Exception e) {
         LogUtils.error("保存文件失败", new Object[]{e});
         return null;
      }
   }

   public static boolean deleteFile(String path) {
      File file = new File(path);
      if (!file.exists()) {
         return false;
      } else {
         boolean flag = file.delete();
         if (flag) {
            File[] files = file.getParentFile().listFiles();
            if (files == null || files.length == 0) {
               file.getParentFile().delete();
            }
         }

         return flag;
      }
   }

   public static Boolean validType(MultipartFile file, String[] acceptTypes) {
      if (ArrayUtil.isEmpty(acceptTypes)) {
         return Boolean.TRUE;
      } else {
         try {
            String type = FileTypeUtil.getType(file.getInputStream());
            if (StrUtil.isBlank(type)) {
               type = cn.hutool.core.io.FileUtil.extName(file.getOriginalFilename());
            }

            if (ArrayUtil.contains(acceptTypes, type)) {
               return Boolean.TRUE;
            } else {
               throw new UploadFileTypeException("文件格式错误");
            }
         } catch (IOException e) {
            LogUtils.error(e);
            throw new UploadFileTypeException("文件格式错误");
         }
      }
   }

   public static String extractFilename(MultipartFile file) {
      String fileName = file.getOriginalFilename();
      String extension = getExtension(file);
      return extractFilename(fileName, extension);
   }

   public static String extractFilename(String fileName, String extension) {
      String var10000 = DateUtil.format(new Date(), "yyyy/MM/dd/HH/mm");
      return var10000 + "/" + encodingFilename(fileName) + "." + extension;
   }

   private static String encodingFilename(String fileName) {
      fileName = fileName.replace("_", " ");
      fileName = SecureUtil.md5().digestHex(fileName + System.nanoTime() + "tt");
      return fileName;
   }

   public static String getExtension(MultipartFile file) {
      String extension = cn.hutool.core.io.FileUtil.extName(file.getOriginalFilename());
      if (StringUtils.isEmpty(extension)) {
         extension = MimeTypeUtil.getExtension((String)Objects.requireNonNull(file.getContentType()));
      }

      return extension;
   }

   public static boolean isImage(File file) {
      if (!file.exists()) {
         return false;
      } else {
         try {
            BufferedImage image = ImageIO.read(file);
            return image != null && image.getWidth() > 0 && image.getHeight() > 0;
         } catch (Exception var3) {
            return false;
         }
      }
   }

   public static boolean isImage(MultipartFile file) {
      if (file == null) {
         return false;
      } else {
         try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null && image.getWidth() > 0 && image.getHeight() > 0;
         } catch (Exception var3) {
            return false;
         }
      }
   }

   public static byte[] getFileByteArray(File file) {
      long fileSize = file.length();
      if (fileSize > 2147483647L) {
         LogUtils.error("文件过大", new Object[0]);
         return null;
      } else {
         byte[] buffer = null;

         try {
            FileInputStream fi = new FileInputStream(file);

            try {
               buffer = new byte[(int)fileSize];

               int offset;
               int numRead;
               for(offset = 0; offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0; offset += numRead) {
               }

               if (offset != buffer.length) {
                  throw new IOException("Could not completely read file " + file.getName());
               }
            } catch (Throwable var8) {
               try {
                  fi.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            fi.close();
         } catch (Exception e) {
            LogUtils.error(e);
         }

         return buffer;
      }
   }

   public static MultipartFile fileToMultipartFile(File file) {
      FileItemFactory factory = new DiskFileItemFactory(16, (File)null);
      FileItem item = factory.createItem("textField", "text/plain", true, file.getName());
      byte[] buffer = new byte[8192];

      try {
         FileInputStream fis = new FileInputStream(file);

         try {
            OutputStream os = item.getOutputStream();

            int bytesRead;
            try {
               while((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                  os.write(buffer, 0, bytesRead);
               }
            } catch (Throwable var11) {
               if (os != null) {
                  try {
                     os.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (os != null) {
               os.close();
            }
         } catch (Throwable var12) {
            try {
               fis.close();
            } catch (Throwable var9) {
               var12.addSuppressed(var9);
            }

            throw var12;
         }

         fis.close();
      } catch (IOException e) {
         LogUtils.error(e);
      }

      return null;
   }

   public static MultipartFile fileToMultipartFileByMock(File file) {
      try {
         FileInputStream input = new FileInputStream(file);
         MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
         return multipartFile;
      } catch (IOException e) {
         LogUtils.error(e);
         throw new RuntimeException(e);
      }
   }

   public static File multipartFileToFile(String path, MultipartFile multipartFile) {
      File file = new File(path);

      try {
         FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
         return file;
      } catch (IOException e) {
         LogUtils.error(e);
         throw new RuntimeException(e);
      }
   }

   public static boolean fileIsExists(String filePath) {
      File f = new File(filePath);
      return f.exists();
   }

   public static void toZip(String outDir, boolean keepDirStructure, String... srcDir) {
      try {
         OutputStream out = new FileOutputStream(new File(outDir));
         ZipOutputStream zos = null;

         try {
            zos = new ZipOutputStream(out);
            List<File> sourceFileList = new ArrayList();

            for(String dir : srcDir) {
               File sourceFile = new File(dir);
               sourceFileList.add(sourceFile);
            }

            compress(sourceFileList, zos, keepDirStructure);
         } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
         } finally {
            if (zos != null) {
               try {
                  zos.close();
               } catch (IOException e) {
                  LogUtils.error(e);
               }
            }

         }
      } catch (Exception e) {
         LogUtils.error("压缩失败:{}", new Object[]{e.getMessage()});
      }

   }

   @SuppressWarnings("unchecked")
   private static void compress(List sourceFileList, ZipOutputStream zos, boolean keepDirStructure) throws Exception {
      byte[] buf = new byte[2048];

      for(File sourceFile : (List<File>) sourceFileList) {
         String name = sourceFile.getName();
         if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            FileInputStream in = new FileInputStream(sourceFile);

            int len;
            while((len = in.read(buf)) != -1) {
               zos.write(buf, 0, len);
            }

            zos.closeEntry();
            in.close();
         } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles != null && listFiles.length != 0) {
               for(File file : listFiles) {
                  if (keepDirStructure) {
                     compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                  } else {
                     compress(file, zos, file.getName(), keepDirStructure);
                  }
               }
            } else if (keepDirStructure) {
               zos.putNextEntry(new ZipEntry(name + "/"));
               zos.closeEntry();
            }
         }
      }

   }

   private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws Exception {
      byte[] buf = new byte[2048];
      if (sourceFile.isFile()) {
         zos.putNextEntry(new ZipEntry(name));
         FileInputStream in = new FileInputStream(sourceFile);

         int len;
         while((len = in.read(buf)) != -1) {
            zos.write(buf, 0, len);
         }

         zos.closeEntry();
         in.close();
      } else {
         File[] listFiles = sourceFile.listFiles();
         if (listFiles != null && listFiles.length != 0) {
            for(File file : listFiles) {
               if (keepDirStructure) {
                  compress(file, zos, name + "/" + file.getName(), keepDirStructure);
               } else {
                  compress(file, zos, file.getName(), keepDirStructure);
               }
            }
         } else if (keepDirStructure) {
            zos.putNextEntry(new ZipEntry(name + "/"));
            zos.closeEntry();
         }
      }

   }

   public static void writeFile(InputStream inputStream, String path, String fileName) {
      OutputStream os = null;

      try {
         byte[] bs = new byte[1024];
         File tempFile = new File(path);
         if (!tempFile.exists()) {
            tempFile.mkdirs();
         }

         String var10000 = tempFile.getPath();
         String newFileName = var10000 + File.separator + fileName;
         LogUtils.info("保存文件：" + newFileName, new Object[0]);
         os = new FileOutputStream(newFileName);

         int len;
         while((len = inputStream.read(bs)) != -1) {
            os.write(bs, 0, len);
         }
      } catch (Exception var16) {
         LogUtils.error("生成excel失败", new Object[0]);
      } finally {
         try {
            if (os != null) {
               os.close();
            }

            inputStream.close();
         } catch (IOException e) {
            LogUtils.error("关闭链接失败" + e.getMessage(), new Object[0]);
         }

      }

   }

   public static void write(InputStream inputStream, String path, String fileName) {
      OutputStream os = null;
      long dateStr = System.currentTimeMillis();

      try {
         byte[] bs = new byte[1024];
         File tempFile = new File(path);
         if (!tempFile.exists()) {
            tempFile.mkdirs();
         }

         String var10000 = tempFile.getPath();
         String newFileName = var10000 + File.separator + fileName;
         LogUtils.info("保存文件：" + newFileName, new Object[0]);
         os = new FileOutputStream(newFileName);

         int len;
         while((len = inputStream.read(bs)) != -1) {
            os.write(bs, 0, len);
         }
      } catch (Exception var18) {
         LogUtils.error("生成excel失败", new Object[0]);
      } finally {
         try {
            if (os != null) {
               os.close();
            }

            inputStream.close();
         } catch (IOException e) {
            LogUtils.error("关闭链接失败" + e.getMessage(), new Object[0]);
         }

      }

   }
}
