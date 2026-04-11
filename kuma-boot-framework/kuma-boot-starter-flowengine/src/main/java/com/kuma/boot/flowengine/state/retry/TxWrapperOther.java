package com.kuma.boot.flowengine.state.retry;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TxWrapperOther {
   public TxWrapperOther() {
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW
   )
   public void withNewTx(TxCallback callback) {
      callback.doWithNewTx();
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW
   )
   public <T> T withNewTx(TxtCallback<T> callback) {
      return callback.doWithNewTx();
   }

   public interface TxCallback {
      void doWithNewTx();
   }

   public interface TxtCallback<T> {
      T doWithNewTx();
   }
}
