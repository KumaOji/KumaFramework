package com.kuma.boot.ddd.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DomainEventMapper extends BaseMapper {
   void deleteOldByServiceIdOfThreeMonths(@Param("serviceId") String serviceId);
}
