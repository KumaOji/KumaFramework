package com.kuma.boot.oss.common.util;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpClientUtil {
   public static final String FTP_CLIENT_CONNECTION_ERROR_MESSAGE = "FTPClient连接失败";
   public static final String FTP_CLIENT_INIT_ERROR_MESSAGE = "FTP初始化异常: ";
   public static final String FTP_CLIENT_DESTORY_ERROR_MESSAGE = "FTP注销异常: ";
   private final String host;
   private final String port;
   private final String username;
   private final String passwd;
   private FTPClient client;
   private boolean isLogin;
   private InputStream input;
   private String remoteDir;
   private String ftpHome;
   private String controlEncoding;

   public FtpClientUtil(String host, String port, String username, String passwd, String remoteDir) {
      this.client = null;
      this.isLogin = false;
      this.input = null;
      this.ftpHome = null;
      this.controlEncoding = null;
      this.host = host;
      this.port = port;
      this.username = username;
      this.passwd = passwd;
      this.remoteDir = remoteDir;
      this.init();
   }

   public FtpClientUtil(String host, String port, String username, String passwd) {
      this(host, port, username, passwd, (String)null);
   }

   private void init() {
      try {
         this.client = new FTPClient();
         this.controlEncoding = this.client.getControlEncoding();
         this.client.connect(this.host, Integer.parseInt(this.port));
         this.isLogin = this.client.login(this.username, this.passwd);
         int reply = this.client.getReplyCode();
         if (!FTPReply.isPositiveCompletion(reply)) {
            this.isLogin = false;
            this.destroy();
            LogUtils.error("FTPClient连接失败", new Object[0]);
         } else {
            this.client.setFileType(2);
            this.ftpHome = this.client.printWorkingDirectory();
            this.client.makeDirectory(this.remoteDir);
            this.client.changeWorkingDirectory(this.remoteDir);
         }
      } catch (Exception e) {
         LogUtils.error("FTP初始化异常: " + e.getMessage(), new Object[]{e});
      }

   }

   public void destroy() {
      if (this.client != null) {
         try {
            this.client.logout();
         } catch (IOException e) {
            LogUtils.error("FTP注销异常: " + e.getMessage(), new Object[]{e});
         } finally {
            try {
               if (this.client.isConnected()) {
                  this.client.disconnect();
               }
            } catch (IOException e) {
               LogUtils.error(e.getMessage(), new Object[0]);
            } finally {
               this.client = null;
            }

         }

      }
   }

   public boolean upload(String remoteDir, String rName, File lFile) {
      String sremoteDir = this.ConvertEncoding(remoteDir);
      String srName = this.ConvertEncoding(rName);
      boolean result = false;
      if (!this.isLogin) {
         this.reInit();
         LogUtils.warn("FTP未登录，重新初始化。。。", new Object[0]);
      }

      if (this.isLogin) {
         try {
            this.input = new FileInputStream(lFile);
            if (sremoteDir != null && sremoteDir.length() > 0) {
               boolean bb = this.client.changeWorkingDirectory(sremoteDir);
               LogUtils.info("远程地址:" + sremoteDir + ",切换结果:" + bb, new Object[0]);
            }

            this.client.enterLocalPassiveMode();
            result = this.client.storeFile(srName, this.input);
            LogUtils.info("文件[" + lFile.getPath() + "]上传结果：" + result, new Object[0]);
         } catch (Exception e) {
            result = false;
            LogUtils.error(e.getMessage(), new Object[0]);
         } finally {
            try {
               this.input.close();
               this.input = null;
            } catch (IOException e) {
               LogUtils.error(e.getMessage(), new Object[0]);
            }

         }
      }

      return result;
   }

   public boolean uploadBigFiles(String remoteDir, String rName, File lFile) {
      String sremoteDir = this.ConvertEncoding(remoteDir);
      String srName = this.ConvertEncoding(rName);
      boolean result = false;
      if (!this.isLogin) {
         this.reInit();
         LogUtils.info("FTP未登录，重新初始化。。。", new Object[0]);
      }

      if (this.isLogin) {
         RandomAccessFile inputRAF = null;
         OutputStream out = null;

         try {
            if (sremoteDir != null && sremoteDir.length() > 0) {
               this.client.changeWorkingDirectory(sremoteDir);
            }

            this.client.enterLocalPassiveMode();
            inputRAF = new RandomAccessFile(lFile, "r");
            out = this.client.storeFileStream(srName);
            byte[] bytes = new byte[1024];

            int c;
            while((c = inputRAF.read(bytes)) != -1) {
               out.write(bytes, 0, c);
               out.flush();
            }

            out.flush();
         } catch (Exception e) {
            LogUtils.error(e.getMessage(), new Object[0]);
         } finally {
            try {
               inputRAF.close();
            } catch (IOException ex) {
               LogUtils.info("" + String.valueOf(ex), new Object[0]);
            }

            try {
               out.close();
               result = this.client.completePendingCommand();
               LogUtils.info("文件[" + lFile.getPath() + "]上传结果：" + result, new Object[0]);
            } catch (IOException ex) {
               LogUtils.error(ex.getMessage(), new Object[0]);
            }

         }
      }

      return result;
   }

   public boolean upload(String remoteFile, String localFile) {
      String sremoteFile = this.ConvertEncoding(remoteFile);
      String slocalFile = this.ConvertEncoding(localFile);

      try {
         InputStream in = new FileInputStream(slocalFile);
         boolean result = this.upload(sremoteFile, in);
         in.close();
         return result;
      } catch (FileNotFoundException e) {
         LogUtils.error("本地文件未找到，上传失败", new Object[]{e});
      } catch (IOException e) {
         LogUtils.error("关闭本地文件流出错", new Object[]{e});
      }

      return false;
   }

   public boolean upload(String remoteFile, InputStream in) {
      String sremoteFile = this.ConvertEncoding(remoteFile);
      LogUtils.info("开始上传文件：" + sremoteFile, new Object[0]);
      sremoteFile = sremoteFile.replaceAll("\\\\", "/");
      if (sremoteFile.startsWith("/") && this.ftpHome != null) {
         sremoteFile = this.ftpHome + sremoteFile;
      }

      OutputStream out = null;

      try {
         this.client.enterLocalPassiveMode();
         String[] pathInfo = sremoteFile.split("/");
         if (pathInfo.length <= 0) {
            return false;
         } else {
            for(int i = 0; i < pathInfo.length - 1; ++i) {
               String path = pathInfo[i];
               if (!path.trim().equals("")) {
                  this.client.makeDirectory(path);
                  boolean changeRes = this.client.changeWorkingDirectory(path);
                  if (!changeRes) {
                     LogUtils.info("不能打开目录：" + this.client.printWorkingDirectory() + "/" + path, new Object[0]);
                     LogUtils.error("上传文件失败：" + sremoteFile, new Object[0]);
                     boolean var9 = false;
                     return var9;
                  }
               }
            }

            this.client.enterLocalPassiveMode();
            out = this.client.storeFileStream(pathInfo[pathInfo.length - 1]);
            byte[] cache = new byte[4096];

            int read;
            while((read = in.read(cache)) != -1) {
               out.write(cache, 0, read);
            }

            out.flush();
            out.close();
            boolean res = this.client.completePendingCommand();
            if (res) {
               LogUtils.info("上传文件成功：" + sremoteFile, new Object[0]);
            } else {
               LogUtils.info("上传文件失败：" + sremoteFile, new Object[0]);
            }

            boolean var27 = res;
            return var27;
         }
      } catch (IOException e) {
         LogUtils.error("上传文件失败：" + sremoteFile, new Object[0]);
         LogUtils.error("上传文件错误", new Object[]{e});
         return false;
      } finally {
         if (out != null) {
            try {
               out.close();
            } catch (Exception e) {
               LogUtils.error(e);
            }
         }

      }
   }

   public InputStream getInputStream(String remoteFile) {
      String sremoteFile = this.ConvertEncoding(remoteFile);
      this.client.enterLocalPassiveMode();
      InputStream in = null;
      sremoteFile = sremoteFile.replaceAll("\\\\", "/");
      if (sremoteFile.startsWith("/") && this.ftpHome != null) {
         sremoteFile = this.ftpHome + sremoteFile;
      }

      try {
         in = this.client.retrieveFileStream(sremoteFile);
         return in;
      } catch (IOException e) {
         LogUtils.error("ftp下载失败！", new Object[]{e});
         return null;
      }
   }

   public InputStream getBinaryInputStream(String remoteFile) {
      String sremoteFile = this.ConvertEncoding(remoteFile);
      this.client.enterLocalPassiveMode();
      InputStream in = null;
      sremoteFile = sremoteFile.replaceAll("\\\\", "/");
      if (sremoteFile.startsWith("/") && this.ftpHome != null) {
         sremoteFile = this.ftpHome + sremoteFile;
      }

      try {
         this.client.setFileType(2);
         in = this.client.retrieveFileStream(sremoteFile);
         return in;
      } catch (IOException e) {
         LogUtils.error("getBinaryInputStream:ftp下载失败！", new Object[]{e});
         return null;
      }
   }

   public boolean download(String remoteFile, OutputStream out) {
      String sremoteFile = this.ConvertEncoding(remoteFile);
      this.client.enterLocalPassiveMode();
      sremoteFile = sremoteFile.replaceAll("\\\\", "/");
      if (sremoteFile.startsWith("/") && this.ftpHome != null) {
         sremoteFile = this.ftpHome + sremoteFile;
      }

      LogUtils.info("ftp下载文件：" + sremoteFile, new Object[0]);

      try {
         return this.client.retrieveFile(sremoteFile, out);
      } catch (IOException e) {
         LogUtils.error("ftp下载失败！", new Object[]{e});
         return false;
      }
   }

   public boolean downloadForKDH(String remoteFile, OutputStream out) {
      String sremoteFile = this.ConvertEncoding(remoteFile);
      this.client.enterLocalPassiveMode();
      sremoteFile = sremoteFile.replaceAll("\\\\", "/");
      LogUtils.info("ftp下载文件：" + sremoteFile, new Object[0]);

      try {
         return this.client.retrieveFile(sremoteFile, out);
      } catch (IOException e) {
         LogUtils.error("ftp下载失败！", new Object[]{e});
         return false;
      }
   }

   public boolean binaryDownload(String remoteFile, OutputStream out) {
      String sremoteFile = this.ConvertEncoding(remoteFile);
      this.client.enterLocalPassiveMode();
      sremoteFile = sremoteFile.replaceAll("\\\\", "/");
      if (sremoteFile.startsWith("/") && this.ftpHome != null) {
         sremoteFile = this.ftpHome + sremoteFile;
      }

      LogUtils.info("ftp下载文件：" + sremoteFile, new Object[0]);

      try {
         this.client.setFileType(2);
         return this.client.retrieveFile(sremoteFile, out);
      } catch (IOException e) {
         LogUtils.error("ftp下载失败！", new Object[]{e});
         return false;
      }
   }

   public boolean renameFile(String srcFileName, String targFileName) {
      String ssrcFileName = this.ConvertEncoding(srcFileName);
      String stargFileName = this.ConvertEncoding(targFileName);
      LogUtils.info("将修改文件名" + ssrcFileName + "为" + stargFileName, new Object[0]);
      boolean bRet = false;

      try {
         bRet = this.client.rename(ssrcFileName, stargFileName);
      } catch (Exception e) {
         LogUtils.error("重命名文件名异常", new Object[]{e});
      }

      return bRet;
   }

   public FTPFile[] listFiles(String path) {
      String spath = this.ConvertEncoding(path);
      FTPFile[] files = null;

      try {
         files = this.client.listFiles(spath);
      } catch (IOException e) {
         LogUtils.error("获取文件列表异常！", new Object[]{e});
      }

      return files;
   }

   public boolean remove(String filePath) {
      try {
         return this.client.remoteStore(filePath);
      } catch (IOException e) {
         LogUtils.error(e);
         return false;
      }
   }

   private void reInit() {
      this.destroy();
      this.init();
   }

   private String ConvertEncoding(String str) {
      if (str != null && !"".equals(str.trim()) && this.controlEncoding != null && !"".equals(this.controlEncoding.trim())) {
         try {
            return new String(str.getBytes("UTF-8"), this.controlEncoding);
         } catch (UnsupportedEncodingException e) {
            LogUtils.error("编码转换错误", new Object[]{e});
            return str;
         }
      } else {
         return str;
      }
   }
}
