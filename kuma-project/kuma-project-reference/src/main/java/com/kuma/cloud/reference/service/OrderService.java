package com.kuma.cloud.reference.service;

import com.kuma.cloud.reference.domain.dto.CreateOrderDTO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.domain.qo.OrderPageQO;
import com.kuma.cloud.reference.domain.vo.OrderVO;
import com.kuma.cloud.reference.domain.vo.PageVO;
import java.util.UUID;

public interface OrderService {
    OrderVO create(CreateOrderDTO request, DataScope scope);
    OrderVO get(UUID id, DataScope scope);
    PageVO<OrderVO> page(OrderPageQO query);
    OrderVO confirm(UUID id, DataScope scope);
}
