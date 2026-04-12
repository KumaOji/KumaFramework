package com.kuma.boot.ddd.domain.support;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

public abstract class AbstractTransactionHandler extends TraceHandler implements RocketMQLocalTransactionListener {
   public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object args) {
      RocketMQLocalTransactionState var4;
      try {
         this.putTrace(message);
         this.executeExtLocalTransaction(message, args);
         LogUtils.info("执行本地事务，事务提交", new Object[0]);
         RocketMQLocalTransactionState var3 = RocketMQLocalTransactionState.COMMIT;
         return var3;
      } catch (Exception e) {
         LogUtils.error("执行本地事务，事务回滚，错误信息：{}", new Object[]{e.getMessage(), e});
         var4 = RocketMQLocalTransactionState.ROLLBACK;
      } finally {
         this.clearTrace();
      }

      return var4;
   }

   public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
      RocketMQLocalTransactionState var2;
      try {
         this.putTrace(message);
         if (!this.checkExtLocalTransaction(message)) {
            LogUtils.info("事务回查后，事务回滚", new Object[0]);
            var2 = RocketMQLocalTransactionState.ROLLBACK;
            return var2;
         }

         LogUtils.info("事务回查后，事务提交", new Object[0]);
         var2 = RocketMQLocalTransactionState.COMMIT;
      } catch (Exception e) {
         LogUtils.error("事务回查异常，事务回滚，错误信息：{}", new Object[]{e.getMessage(), e});
         RocketMQLocalTransactionState var3 = RocketMQLocalTransactionState.ROLLBACK;
         return var3;
      } finally {
         this.clearTrace();
      }

      return var2;
   }

   protected abstract void executeExtLocalTransaction(Message message, Object args);

   protected abstract boolean checkExtLocalTransaction(Message message);
}
