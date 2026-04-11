package com.kuma.boot.webagg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapper;
import com.kuma.boot.webagg.entity.SuperEntity;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.Optional;

public abstract class AbstractBaseSuperService<T extends SuperEntity<T, I>, I extends Serializable, M extends MpSuperMapper<T, I>> extends CrudRepository<M, T> implements BaseSuperService<T, I> {
   public AbstractBaseSuperService() {
   }

   @SafeVarargs
   public final Optional<T> findByIdWithColumns(Serializable id, SFunction<T, ?>... columns) {
      LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper();
      queryWrapper.select(columns);
      queryWrapper.eq(SuperEntity::getId, id);
      return Optional.ofNullable((SuperEntity)this.mapper().selectOne(queryWrapper));
   }

   @SafeVarargs
   public final List<T> findByIdsWithColumns(List<Serializable> ids, SFunction<T, ?>... columns) {
      LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper();
      queryWrapper.select(columns);
      queryWrapper.in(SuperEntity::getId, ids);
      return this.mapper().selectList(queryWrapper);
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/baomidou/mybatisplus/core/toolkit/support/SFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/webagg/entity/SuperEntity") && lambda.getImplMethodSignature().equals("()Ljava/io/Serializable;")) {
               return SuperEntity::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/baomidou/mybatisplus/core/toolkit/support/SFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/webagg/entity/SuperEntity") && lambda.getImplMethodSignature().equals("()Ljava/io/Serializable;")) {
               return SuperEntity::getId;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
