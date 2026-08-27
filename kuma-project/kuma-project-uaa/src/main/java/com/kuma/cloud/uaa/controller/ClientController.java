package com.kuma.cloud.uaa.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.idempotent.idempotetduplicate.PreventDuplicateSubmit;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.uaa.domain.dto.ClientSaveDTO;
import com.kuma.cloud.uaa.domain.vo.ClientVO;
import com.kuma.cloud.uaa.security.UaaPermissions;
import com.kuma.cloud.uaa.service.UaaClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "接入客户端管理")
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final UaaClientService clientService;

    @Operation(summary = "查询全部接入客户端，不返回密钥")
    @GetMapping
    @Authorize(UaaPermissions.CLIENT_READ)
    public Result<List<ClientVO>> list() {
        return Result.success(clientService.listAll());
    }

    @Operation(summary = "查询客户端详情")
    @GetMapping("/{clientId}")
    @Authorize(UaaPermissions.CLIENT_READ)
    public Result<ClientVO> detail(@PathVariable String clientId) {
        return Result.success(clientService.getByClientId(clientId));
    }

    @Operation(summary = "按 clientId 幂等保存客户端，clientSecret 留空表示保留原密钥")
    @PostMapping
    @Authorize(UaaPermissions.CLIENT_WRITE)
    @PreventDuplicateSubmit(expire = 3)
    public Result<String> save(@Valid @RequestBody ClientSaveDTO dto) {
        clientService.save(dto);
        return Result.success("保存成功");
    }

    @Operation(summary = "删除客户端，同时清理其授权记录与授权同意")
    @DeleteMapping("/{clientId}")
    @Authorize(UaaPermissions.CLIENT_WRITE)
    public Result<String> delete(@PathVariable String clientId) {
        clientService.delete(clientId);
        return Result.success("删除成功");
    }
}
