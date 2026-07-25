package com.kuma.cloud.lab.transaction.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.transaction.domain.dto.TransactionTransferDTO;
import com.kuma.cloud.lab.transaction.domain.vo.TransactionTransferVO;
import com.kuma.cloud.lab.transaction.service.TransactionTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据库事务测试")
@RestController
@RequestMapping("/lab/transaction")
@RequiredArgsConstructor
public class TransactionTestController {

    private final TransactionTestService transactionTestService;

    @Operation(summary = "执行转账并返回数据变更与 SQL，可选择在执行完成后回滚")
    @PostMapping("/transfer")
    public Result<TransactionTransferVO> transfer(@Valid @RequestBody TransactionTransferDTO dto) {
        return Result.success(transactionTestService.transfer(dto));
    }
}
