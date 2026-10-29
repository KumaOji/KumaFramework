package com.kuma.boot.flowengine.module;

import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.delegate.InvokeDelegateContext;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.engine.NodeExecution;
import com.kuma.boot.flowengine.exception.FlowException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.Iterator;
import java.util.Map;

public class Condition extends AbstractNode {
   private ActivityNode activityNode;
   private @Size(
   min = 1
) @Valid Map<String, Transition> transitions = Maps.newHashMap();
   private String mvelScript;

   public Condition() {
   }

   public void addTransition(Transition transition) {
      this.transitions.put(transition.getEvent(), transition);
   }

   public Map<String, Transition> getTransitions() {
      return this.transitions;
   }

   public String getMvelScript() {
      return this.mvelScript;
   }

   public void setMvelScript(String mvelScript) {
      this.mvelScript = mvelScript;
   }

   public void setActivityNode(ActivityNode activityNode) {
      this.activityNode = activityNode;
   }

   public void initialize(Flow flow) {
      for(Map.Entry<String, Transition> each : this.transitions.entrySet()) {
         ((Transition)each.getValue()).initialize(flow);
      }

   }

   public void execute(Execution execution) {
      NodeExecution nodeExecution = execution.currentNodeExecution();
      Flow flow = execution.getCurrentFlow();
      FlowNode currentFlowNode = nodeExecution.currentNode();
      if (currentFlowNode instanceof StartNode) {
         Iterator<Transition> ts = this.getTransitions().values().iterator();
         if (!ts.hasNext()) {
            throw new FlowException("流程定义Flow={},Version={}开始节点没有配置正常的Transition流转...");
         } else {
            Transition transition = (Transition)ts.next();
            transition.execute(execution);
         }
      } else if (!(currentFlowNode instanceof EndNode)) {
         if (!this.isTerminate(nodeExecution.decision(), execution)) {
            InvokeDelegateContext invokeDelegateContext = execution.getEngine().getInvokeDelegateContext();
            Flow.Key flowKey = new Flow.Key(flow.getName(), flow.getVersion());
            Object conditionResult = invokeDelegateContext.invoke(new Object[]{flowKey, nodeExecution.currentNode().getName(), Condition.class, execution});
            if (conditionResult == null || !this.isTerminate(conditionResult.toString(), execution)) {
               if (StringUtils.isNotBlank(this.mvelScript)) {
                  Object mvelResult = execution.getEngine().getMvelScriptContext().calculate(execution, flowKey, nodeExecution.currentNode().getName());
                  if (mvelResult == null) {
                     return;
                  }

                  if (this.isTerminate(mvelResult.toString(), execution)) {
                     return;
                  }
               }

               if (this.getTransitions().values().size() == 1) {
                  Transition transition = (Transition)this.getTransitions().values().iterator().next();
                  execution.getEngine().getListenerDelegateContext().action(execution, transition.getEvent());
                  transition.execute(execution);
               } else {
                  throw new FlowException(String.format("汽起汽结出造,不能成到肥的流转节点Decisi=NULL FLOWE=%s.Versi0n=%s,.N0de=%s活法四?的流结务件,", flow.getName(), flow.getVersion(), execution.currentNodeExecution().currentNode()));
               }
            }
         }
      }
   }

   private boolean isTerminate(String decision, Execution execution) {
      if (StringUtils.isNotBlank(decision)) {
         if (decision.equals("app.kit.flow.decision.stop")) {
            return Boolean.TRUE;
         }

         if (!decision.equals("app.kit.flow.decision.skip")) {
            Transition transition = (Transition)this.transitions.get(decision);
            if (transition == null) {
               throw new FlowException(String.format("快择条件出错Decision=%s不存在对应的Iransition定义,流程Flow=%s,version=%s,Modelamne=%s", decision, execution.getCurrentFlow().getName(), execution.getCurrentFlow().getVersion(), this.activityNode.getName()));
            }

            execution.getEngine().getListenerDelegateContext().action(execution, decision);
            transition.execute(execution);
            return Boolean.TRUE;
         }
      }

      return Boolean.FALSE;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("Condition{");
      sb.append("activityNodeName=").append(this.activityNode.getName());
      sb.append("mvelScript=").append(this.mvelScript);
      sb.append("transitions=").append(this.transitions);
      sb.append('}');
      return sb.toString();
   }
}
