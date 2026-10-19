package com.kuma.cloud.reference.mapper;

import com.kuma.cloud.reference.domain.entity.OrderDO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis 只负责数据库访问；SQL 位于 resources/mapper/OrderMapper.xml。 */
@Mapper
public interface OrderMapper {
    OrderDO selectById(@Param("id") String id);
    int insert(OrderDO order);
    int updateStatus(@Param("id") String id, @Param("status") String status,
                     @Param("version") long version, @Param("expectedVersion") long expectedVersion);
    List<OrderDO> selectPage(@Param("sku") String sku, @Param("status") String status,
                             @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("sku") String sku, @Param("status") String status);
}
