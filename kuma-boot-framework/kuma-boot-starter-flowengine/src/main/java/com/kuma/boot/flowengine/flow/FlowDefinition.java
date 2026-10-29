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
               LogUtils.info("执行:{}", new Object[]{this.getCurrentFlowInfo()});
               holder = step.execute(context);
               if (holder.isTerminate()) {
                  holder.setCurrentStepPath(this.getCurrentFlowInfo());
                  break;
               }
            } catch (RuntimeException be) {
               LogUtils.warn("执行:{}业务报错", new Object[]{this.getCurrentFlowInfo(), be});
               throw be;
            } catch (Exception e) {
               LogUtils.error("执行:{}未知报错", new Object[]{this.getCurrentFlowInfo(), e});
               throw e;
            } finally {
               ((LinkedList)stepCache.get()).removeLast();
            }
         } else {
            ((LinkedList)stepCache.get()).addLast(step);
            LogUtils.info("跳过:{}", new Object[]{this.getCurrentFlowInfo()});
            ((LinkedList)stepCache.get()).removeLast();
         }
      }

      try {
         result = (Result)holder.getResult();
      } catch (Exception e) {
         LogUtils.info("流程 ${}，最终获取返回报文报错", new Object[]{this.name, e});
      }

      LogUtils.info("流程 ${} 结束", new Object[]{this.name});
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
