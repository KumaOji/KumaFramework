package com.kuma.boot.monitor.monitor.registry.seconds;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.monitor.utils.IOUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorController {
   @Resource
   private SourceStatistician sourceStatistician;

   public MonitorController() {
   }

   @RequestMapping(
      value = {"/seconds/monitor"},
      method = {RequestMethod.GET}
   )
   public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
         List<String> secondDataList = this.sourceStatistician.createInfluxData();
         PrintWriter out = resp.getWriter();

         for(String s : secondDataList) {
            out.write(s);
            out.flush();
         }

         out.close();
      } catch (Exception e) {
         LogUtils.error("error", new Object[]{e});
      } finally {
         IOUtils.close(resp.getWriter());
      }

   }
}
