/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.scripting.ScriptCompilationException
 *  org.springframework.scripting.ScriptEvaluator
 *  org.springframework.scripting.ScriptSource
 */
package com.kuma.boot.common.support.groovy;

import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;

public abstract class AbstractScriptEvaluator
implements ScriptEvaluator {
    public Object evaluate(ScriptSource script) throws ScriptCompilationException {
        return this.evaluate(script, null);
    }
}

