package com.kuma.cloud.lab.transaction.service;

import com.kuma.cloud.lab.transaction.domain.dto.TransactionTransferDTO;
import com.kuma.cloud.lab.transaction.domain.vo.TransactionTransferVO;

public interface TransactionTestService {

    TransactionTransferVO transfer(TransactionTransferDTO dto);
}
