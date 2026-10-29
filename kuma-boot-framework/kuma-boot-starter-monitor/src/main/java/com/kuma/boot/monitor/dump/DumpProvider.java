package com.kuma.boot.monitor.dump;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.servlet.RequestUtils;
import com.kuma.boot.monitor.exception.HealthException;
import com.kuma.boot.monitor.utils.ProcessUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DumpProvider {
   private static Long lastDumpTime = 0L;

   public DumpProvider() {
   }

   public File[] getList() {
      File file = new File(".");
      return file.listFiles((dir, name) -> name.contains(".hprof"));
   }

   public void list() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("不要多次dump,一分钟仅限一次,仅限linux系统有效<br/><a href='do/'>立即Dump</a><br/>");

      for(File f : this.getList()) {
         if (!f.getName().endsWith(".tar")) {
            stringBuilder.append(String.format("%s (%s M)<a href='zip/?name=%s'>压缩</a><br/>", f.getName(), f.length() / 1024L / 1024L, f.getName()));
         } else {
            stringBuilder.append(String.format("%s (%s M)<a href='download/?name=%s'>下载</a><br/>", f.getName(), f.length() / 1024L / 1024L, f.getName()));
         }
      }

      this.response(stringBuilder.toString());
   }

   public void zip(String name) {
      for(File f : this.getList()) {
         if (name != null && name.equals(f.getName())) {
            try {
               Runtime.getRuntime().exec(String.format("tar -zcvf %s.tar %s", name, name), (String[])null, (File)null);
               this.response("压缩成功,请等待耐心等待,不要重复执行!");
            } catch (Exception exp) {
               LogUtils.error("kuma-boot-starter-monitor", new Object[]{"zip 出错", exp});
               this.response("压缩出错:" + exp.getMessage());
            }
         }
      }

   }

   public void dump() {
      try {
         if (System.currentTimeMillis() - lastDumpTime < TimeUnit.MINUTES.toMillis(1L)) {
            throw new HealthException("dump过于频繁,请等待后1分钟重试");
         }

         Runtime.getRuntime().exec(String.format("jmap -dump:format=b,file=heap.%s.hprof %s", (new SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date()), ProcessUtils.getProcessID()));
         lastDumpTime = System.currentTimeMillis();
         this.response("dump成功,请等待耐心等待,不要重复执行!");
      } catch (Exception exp) {
         LogUtils.error("kuma-boot-starter-monitor", new Object[]{"dump 出错", exp});
         this.response("dump出错:" + exp.getMessage());
      }

   }

   public void download(String name) {
      for(File f : this.getList()) {
         if (name != null && name.equals(f.getName())) {
            HttpServletResponse response = RequestUtils.getResponse();
            response.reset();
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + f.getName());
            response.addHeader("Content-Length", "" + f.length());
            response.setHeader("Content-type", "");

            try {
               FileInputStream fs = new FileInputStream(f);

               try {
                  InputStream fis = new BufferedInputStream(fs);

                  try {
                     OutputStream out = new BufferedOutputStream(response.getOutputStream());

                     try {
                        response.setContentType("application/octet-stream");
                        byte[] buffer = new byte[1024];

                        int i;
                        while((i = fis.read(buffer)) != -1) {
                           out.write(buffer, 0, i);
                        }

                        fis.close();
                        out.flush();
                     } catch (Throwable var15) {
                        try {
                           out.close();
                        } catch (Throwable var14) {
                           var15.addSuppressed(var14);
                        }

                        throw var15;
                     }

                     out.close();
                  } catch (Throwable var16) {
                     try {
                        fis.close();
                     } catch (Throwable var13) {
                        var16.addSuppressed(var13);
                     }

                     throw var16;
                  }

                  fis.close();
               } catch (Throwable var17) {
                  try {
                     fs.close();
                  } catch (Throwable var12) {
                     var17.addSuppressed(var12);
                  }

                  throw var17;
               }

               fs.close();
            } catch (Exception exp) {
               LogUtils.error("kuma-boot-starter-monitor", new Object[]{"download 出错", exp});
               this.response("下载出错:" + exp.getMessage());
            }
         }
      }

   }

   private void response(String html) {
      try {
         HttpServletResponse response = RequestUtils.getResponse();
         response.reset();
         response.setHeader("Content-type", "text/html;charset=UTF-8");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().append(html);
         response.getWriter().flush();
         response.getWriter().close();
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }
}
