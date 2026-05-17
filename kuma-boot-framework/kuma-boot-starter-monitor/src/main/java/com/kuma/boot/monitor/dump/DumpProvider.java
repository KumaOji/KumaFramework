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
      stringBuilder.append("\u4e0d\u8981\u591a\u6b21dump,\u4e00\u5206\u949f\u4ec5\u9650\u4e00\u6b21,\u4ec5\u9650linux\u7cfb\u7edf\u6709\u6548<br/><a href='do/'>\u7acb\u5373Dump</a><br/>");

      for(File f : this.getList()) {
         if (!f.getName().endsWith(".tar")) {
            stringBuilder.append(String.format("%s (%s M)<a href='zip/?name=%s'>\u538b\u7f29</a><br/>", f.getName(), f.length() / 1024L / 1024L, f.getName()));
         } else {
            stringBuilder.append(String.format("%s (%s M)<a href='download/?name=%s'>\u4e0b\u8f7d</a><br/>", f.getName(), f.length() / 1024L / 1024L, f.getName()));
         }
      }

      this.response(stringBuilder.toString());
   }

   public void zip(String name) {
      for(File f : this.getList()) {
         if (name != null && name.equals(f.getName())) {
            try {
               Runtime.getRuntime().exec(String.format("tar -zcvf %s.tar %s", name, name), (String[])null, (File)null);
               this.response("\u538b\u7f29\u6210\u529f,\u8bf7\u7b49\u5f85\u8010\u5fc3\u7b49\u5f85,\u4e0d\u8981\u91cd\u590d\u6267\u884c!");
            } catch (Exception exp) {
               LogUtils.error("kuma-boot-starter-monitor", new Object[]{"zip \u51fa\u9519", exp});
               this.response("\u538b\u7f29\u51fa\u9519:" + exp.getMessage());
            }
         }
      }

   }

   public void dump() {
      try {
         if (System.currentTimeMillis() - lastDumpTime < TimeUnit.MINUTES.toMillis(1L)) {
            throw new HealthException("dump\u8fc7\u4e8e\u9891\u7e41,\u8bf7\u7b49\u5f85\u540e1\u5206\u949f\u91cd\u8bd5");
         }

         Runtime.getRuntime().exec(String.format("jmap -dump:format=b,file=heap.%s.hprof %s", (new SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date()), ProcessUtils.getProcessID()));
         lastDumpTime = System.currentTimeMillis();
         this.response("dump\u6210\u529f,\u8bf7\u7b49\u5f85\u8010\u5fc3\u7b49\u5f85,\u4e0d\u8981\u91cd\u590d\u6267\u884c!");
      } catch (Exception exp) {
         LogUtils.error("kuma-boot-starter-monitor", new Object[]{"dump \u51fa\u9519", exp});
         this.response("dump\u51fa\u9519:" + exp.getMessage());
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
               LogUtils.error("kuma-boot-starter-monitor", new Object[]{"download \u51fa\u9519", exp});
               this.response("\u4e0b\u8f7d\u51fa\u9519:" + exp.getMessage());
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
