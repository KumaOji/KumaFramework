package com.kuma.cloud.reference.service;

import com.kuma.cloud.reference.domain.dto.CreateOrderDTO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.domain.qo.OrderPageQO;
import com.kuma.cloud.reference.domain.vo.OrderVO;
import com.kuma.boot.common.model.result.PageResultRecord;

public interface OrderService {
    OrderVO create(CreateOrderDTO request, DataScope scope);
    OrderVO get(Long id, DataScope scope);
    PageResultRecord<OrderVO> page(OrderPageQO query);
    OrderVO confirm(Long id, DataScope scope);
}
