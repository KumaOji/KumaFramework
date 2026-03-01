package com.kuma.boot.common.support.groovy;

import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;

/**
 * AbstractScriptEvaluator
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public abstract class AbstractScriptEvaluator implements ScriptEvaluator {

    @Override
    public Object evaluate( ScriptSource script ) throws ScriptCompilationException {
        return this.evaluate(script, null);
    }
}
