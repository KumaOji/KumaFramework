package com.kuma.boot.test.junitperf.support.builder;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.kuma.boot.test.junitperf.core.annotation.KmcTestRequire;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationRequire;
import java.util.Map;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public class EvaluationRequireBuilder {
   private final KmcTestRequire kmcTestRequire;

   public EvaluationRequireBuilder(KmcTestRequire kmcTestRequire) {
      this.kmcTestRequire = kmcTestRequire;
   }

   public EvaluationRequire build() {
      EvaluationRequire evaluationRequire = new EvaluationRequire();
      if (ObjUtil.isNotNull(this.kmcTestRequire)) {
         this.validateJunitPerfRequire(this.kmcTestRequire);
         evaluationRequire.setRequireMin(this.kmcTestRequire.min());
         evaluationRequire.setRequireMax(this.kmcTestRequire.max());
         evaluationRequire.setRequireAverage(this.kmcTestRequire.average());
         evaluationRequire.setRequireTimesPerSecond(this.kmcTestRequire.timesPerSecond());
         evaluationRequire.setRequirePercentilesMap(this.parseRequirePercentilesMap(this.kmcTestRequire.percentiles()));
      } else {
         evaluationRequire.setRequireMin(-1.0F);
         evaluationRequire.setRequireMax(-1.0F);
         evaluationRequire.setRequireAverage(-1.0F);
         evaluationRequire.setRequireTimesPerSecond(-1);
         evaluationRequire.setRequirePercentilesMap(Maps.newHashMap());
      }

      return evaluationRequire;
   }

   private void validateJunitPerfRequire(KmcTestRequire kmcTestRequire) {
      Preconditions.checkState(kmcTestRequire.timesPerSecond() >= 0, "timesPerSecond must be >= 0");
   }

   private Map<Integer, Float> parseRequirePercentilesMap(String[] percentiles) {
      Map<Integer, Float> percentilesMap = Maps.newHashMap();
      if (ArrayUtil.isNotEmpty(percentiles)) {
         try {
            for(String percent : percentiles) {
               String[] strings = percent.split(":");
               Integer left = Ints.tryParse(strings[0]);
               Float right = Floats.tryParse(strings[1]);
               percentilesMap.put(left, right);
            }
         } catch (Exception var10) {
            throw new IllegalArgumentException("Percentiles format is error! please like this: 80:50000.");
         }
      }

      return percentilesMap;
   }
}
