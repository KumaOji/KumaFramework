package com.kuma.boot.sensitive.sensitivelog.core.bs;

import com.kuma.boot.common.support.deepcopy.DeepCopy;
import com.kuma.boot.common.support.deepcopy.FastJsonDeepCopy;
import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.sensitive.sensitivelog.api.Sensitive;
import com.kuma.boot.sensitive.sensitivelog.api.SensitiveConfig;
import com.kuma.boot.sensitive.sensitivelog.core.api.SensitiveService;
import com.kuma.boot.sensitive.sensitivelog.core.support.config.DefaultSensitiveConfig;

public final class SensitiveBs {
   private DeepCopy deepCopy = FastJsonDeepCopy.getInstance();
   private Sensitive sensitive = (Sensitive)Instances.singleton(SensitiveService.class);

   private SensitiveBs() {
   }

   public static SensitiveBs newInstance() {
      return new SensitiveBs();
   }

   public SensitiveBs deepCopy(DeepCopy deepCopy) {
      ArgUtils.notNull(deepCopy, "deepCopy");
      this.deepCopy = deepCopy;
      return this;
   }

   public <T> T desCopy(T object) {
      SensitiveConfig config = this.buildConfig();
      return (T)this.sensitive.desCopy(object, config);
   }

   public String desJson(Object object) {
      SensitiveConfig config = this.buildConfig();
      return this.sensitive.desJson(object, config);
   }

   private SensitiveConfig buildConfig() {
      return DefaultSensitiveConfig.newInstance().deepCopy(this.deepCopy);
   }
}
