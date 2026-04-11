package com.kuma.boot.flowengine.flow;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.LinkedList;
import java.util.List;

public class FlowDefinition<Context, Result> implements Step<Context, Result> {
   private static ThreadLocal<LinkedList<Step>> stepCache = ThreadLocal.withInitial(LinkedList::new);
   private String name;
   private List<Step<Context, ?>> steps;

   public FlowDefinition(String name, List<Step<Context, ?>> steps) {
      this.name = name;
      this.steps = steps;
   }

   public boolean hasAction(Context context) {
      return true;
   }

   public FlowResultHolder<Result> execute(Context context) {
      FlowResultHolder holder = FlowResultHolder.empty();
      Result result = null;

      for(Step<Context, ?> step : this.steps) {
         if (step.hasAction(context)) {
            try {
               ((LinkedList)stepCache.get()).addLast(step);
               LogUtils.info("\u6267\u884c:{}", new Object[]{this.getCurrentFlowInfo()});
               holder = step.execute(context);
               if (holder.isTerminate()) {
                  holder.setCurrentStepPath(this.getCurrentFlowInfo());
                  break;
               }
            } catch (RuntimeException be) {
               LogUtils.warn("\u6267\u884c:{}\u4e1a\u52a1\u62a5\u9519", new Object[]{this.getCurrentFlowInfo(), be});
               throw be;
            } catch (Exception e) {
               LogUtils.error("\u6267\u884c:{}\u672a\u77e5\u62a5\u9519", new Object[]{this.getCurrentFlowInfo(), e});
               throw e;
            } finally {
               ((LinkedList)stepCache.get()).removeLast();
            }
         } else {
            ((LinkedList)stepCache.get()).addLast(step);
            LogUtils.info("\u8df3\u8fc7:{}", new Object[]{this.getCurrentFlowInfo()});
            ((LinkedList)stepCache.get()).removeLast();
         }
      }

      try {
         result = (Result)holder.getResult();
      } catch (Exception e) {
         LogUtils.info("\u6d41\u7a0b ${}\uff0c\u6700\u7ec8\u83b7\u53d6\u8fd4\u56de\u62a5\u6587\u62a5\u9519", new Object[]{this.name, e});
      }

      LogUtils.info("\u6d41\u7a0b ${} \u7ed3\u675f", new Object[]{this.name});
      return holder;
   }

   public String getName() {
      return this.name;
   }

   public String getFlowName() {
      return null;
   }

   private String getCurrentFlowInfo() {
      StringBuilder sb = new StringBuilder();
      LinkedList<Step> stepInfo = (LinkedList)stepCache.get();
      return stepInfo.size() == 0 ? "" : sb.append("$").append(((Step)stepInfo.getLast()).getFlowName()).append("#").append(((Step)stepInfo.getLast()).getName()).toString();
   }
}
