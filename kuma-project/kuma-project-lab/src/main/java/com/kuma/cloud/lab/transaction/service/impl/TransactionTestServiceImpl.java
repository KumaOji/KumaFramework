package com.kuma.cloud.lab.transaction.service.impl;

import com.kuma.cloud.lab.transaction.blackbox.TransactionBlackBox;
import com.kuma.cloud.lab.transaction.domain.dto.TransactionTransferDTO;
import com.kuma.cloud.lab.transaction.domain.vo.TransactionTransferVO;
import com.kuma.cloud.lab.transaction.service.TransactionTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionTestServiceImpl implements TransactionTestService {

    private final TransactionBlackBox transactionBlackBox;

    @Override
    public TransactionTransferVO transfer(TransactionTransferDTO dto) {
        return transactionBlackBox.execute(dto);
    }
}
