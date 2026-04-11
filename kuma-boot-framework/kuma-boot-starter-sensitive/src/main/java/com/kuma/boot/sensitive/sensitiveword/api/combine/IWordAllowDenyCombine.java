package com.kuma.boot.sensitive.sensitiveword.api.combine;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import java.util.Collection;
import java.util.List;

public interface IWordAllowDenyCombine {
   Collection<String> getActualDenyList(final List<String> allowList, final List<String> denyList, final IWordContext context);
}
