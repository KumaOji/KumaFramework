package com.kuma.boot.flowengine.module.validate;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.module.NodeRef;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NodeRefValidator implements ConstraintValidator<NodeRefConstraint, NodeRef> {
   public NodeRefValidator() {
   }

   public void initialize(NodeRefConstraint constraintAnnotation) {
   }

   public boolean isValid(NodeRef valve, ConstraintValidatorContext context) {
      return valve != null && StringUtils.isNotBlank(valve.getName());
   }
}
