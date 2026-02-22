/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogMessage
 *  org.springframework.security.core.context.DeferredSecurityContext
 *  org.springframework.security.core.context.SecurityContext
 *  org.springframework.security.core.context.SecurityContextHolderStrategy
 */
package com.kuma.boot.security.spring.core.context;

import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

public final class SupplierDeferredSecurityContext
implements DeferredSecurityContext {
    private static final Log logger = LogFactory.getLog(SupplierDeferredSecurityContext.class);
    private final Supplier<SecurityContext> supplier;
    private final SecurityContextHolderStrategy strategy;
    private SecurityContext securityContext;
    private boolean missingContext;

    public SupplierDeferredSecurityContext(Supplier<SecurityContext> supplier, SecurityContextHolderStrategy strategy) {
        this.supplier = supplier;
        this.strategy = strategy;
    }

    public SecurityContext get() {
        this.init();
        return this.securityContext;
    }

    public boolean isGenerated() {
        this.init();
        return this.missingContext;
    }

    private void init() {
        if (this.securityContext != null) {
            return;
        }
        this.securityContext = this.supplier.get();
        boolean bl = this.missingContext = this.securityContext == null;
        if (this.missingContext) {
            this.securityContext = this.strategy.createEmptyContext();
            if (logger.isTraceEnabled()) {
                logger.trace((Object)LogMessage.format((String)"Created %s", (Object)this.securityContext));
            }
        }
    }
}

