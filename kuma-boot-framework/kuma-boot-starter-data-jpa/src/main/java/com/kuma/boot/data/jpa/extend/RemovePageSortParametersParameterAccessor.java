package com.kuma.boot.data.jpa.extend;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;

public class RemovePageSortParametersParameterAccessor extends ParametersParameterAccessor {
   private final List<Object> removePageSortValues = new ArrayList();

   public RemovePageSortParametersParameterAccessor(Parameters<?, ?> parameters, Object[] values) {
      super(parameters, values);

      for(int i = 0; i < values.length; ++i) {
         if (parameters.getPageableIndex() != i && parameters.getSortIndex() != i) {
            this.removePageSortValues.add(values[i]);
         }
      }

   }

   public Object[] getRemovePageSortParameters() {
      return this.removePageSortValues.toArray();
   }
}
