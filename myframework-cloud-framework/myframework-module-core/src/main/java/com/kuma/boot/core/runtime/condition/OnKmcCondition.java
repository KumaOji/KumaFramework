package com.kuma.boot.core.runtime.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * KmcCondition 类
 *
 * @author kuma
 * @version 2022.05
 * @since 2025/12/24
 */
public class OnKmcCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome( ConditionContext context, AnnotatedTypeMetadata metadata ) {

        return ConditionOutcome.match();
    }
}
