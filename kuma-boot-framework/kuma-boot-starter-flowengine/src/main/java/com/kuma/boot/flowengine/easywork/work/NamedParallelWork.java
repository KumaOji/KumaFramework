package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import java.util.ArrayList;
import java.util.List;

public class NamedParallelWork extends NamedWork {
   private List<Work> supplierWorks = new ArrayList();

   public List<Work> getSupplierWorks() {
      return this.supplierWorks;
   }

   private NamedParallelWork(List<Work> works) {
      this.supplierWorks = works;
   }

   public static NamedParallelWork aNewParallelWork(List<Work> works) {
      return new NamedParallelWork(works);
   }

   public NamedParallelWork named(String name) {
      this.name = name;
      return this;
   }

   public Object execute(WorkContext context) {
      return null;
   }

   public void addWork(Work work) {
      this.supplierWorks.add(work);
   }
}
