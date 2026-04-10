package com.kuma.boot.test.junitperf.model;

import java.io.Serializable;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public class BaseModel implements Serializable {
   private static final long serialVersionUID = 8216424549704594536L;

   public BaseModel() {
   }
}
