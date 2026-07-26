package com.kuma.cloud.lab.transaction.blackbox;

import com.kuma.cloud.lab.transaction.domain.dto.TransactionTransferDTO;
import com.kuma.cloud.lab.transaction.domain.vo.TransactionTransferVO;

/**
 * 事务转账黑盒。
 *
 * <p>调用方只依赖输入和输出协议，不依赖内部执行的 SQL。</p>
 */
public interface TransactionBlackBox {

    TransactionTransferVO execute(TransactionTransferDTO dto);
}
