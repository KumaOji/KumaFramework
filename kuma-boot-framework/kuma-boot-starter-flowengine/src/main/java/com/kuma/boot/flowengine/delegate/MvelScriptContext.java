package com.kuma.boot.flowengine.delegate;

import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.Flow;
import com.kuma.boot.flowengine.state.TraceLogFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.CompiledExpression;

public class MvelScriptContext implements Script, InvokeSupport {
   private final ApplicationContext applicationContext;
   private Map<Key, String> scripts = Maps.newHashMap();

   public MvelScriptContext(ApplicationContext applicationcontext) {
      this.applicationContext = applicationcontext;
   }

   public String calculate(Execution execution, Flow.Key key, String nodeName) {
      Object result = null;
      Key mKey = new Key(key.getFlowName(), key.getVersion(), nodeName);
      String script = (String)this.scripts.get(mKey);
      if (script == null) {
         return null;
      } else {
         String var9;
         try {
            CompiledExpression compiled = (CompiledExpression)MVEL.compileExpression(script);
            Map vars = new HashMap();
            vars.put("execution", execution);
            result = MVEL.executeExpression(compiled, vars);
            if (!(result instanceof String)) {
               throw new FlowException(String.format("状态机 【stateachine=%s,version=%s ,Node=%s】执行脚本【script=%s】无返回值", key.getFlowName(), key.getVersion(), nodeName, script));
            }

            var9 = result.toString();
         } finally {
            Logger logger = TraceLogFactory.getLogger(execution.getCurrentFlow().getLogName());
            if (logger.isDebugEnabled()) {
               logger.debug("节点:{}进行脚本决策|nMVEL->{}n入参->execution={}rn快策结果->{}", new Object[]{execution.currentNodeExecution().currentNode().getName(), script, execution, result});
            }

         }

         return var9;
      }
   }

   public void proceed(Flow.Key flowKey, String nodeName, String target) {
      if (StringUtils.isNotBlank(target)) {
         String stateMachine = flowKey.getFlowName();
         int version = flowKey.getVersion();
         Key mKey = new Key(stateMachine, version, nodeName);
         this.scripts.put(mKey, target);
      }

   }

   public static class Key {
      private String stateMachineName;
      private int version;
      private String nodeName;

      public Key(String stateMachineName, int version, String nodeName) {
         this.stateMachineName = stateMachineName;
         this.version = version;
         this.nodeName = nodeName;
      }

      public String getStateMachineName() {
         return this.stateMachineName;
      }

      public int getVersion() {
         return this.version;
      }

      public String getNodeName() {
         return this.nodeName;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Key key = (Key)o;
            return this.version == key.version && Objects.equals(this.stateMachineName, key.stateMachineName) && Objects.equals(this.nodeName, key.nodeName);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.stateMachineName, this.version, this.nodeName});
      }
   }
}
