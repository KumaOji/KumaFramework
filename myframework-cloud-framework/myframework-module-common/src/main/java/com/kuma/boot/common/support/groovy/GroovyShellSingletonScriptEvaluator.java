/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Binding
 *  groovy.lang.GroovyClassLoader
 *  groovy.lang.GroovyCodeSource
 *  groovy.lang.GroovyRuntimeException
 *  groovy.lang.GroovyShell
 *  groovy.lang.Script
 *  org.codehaus.groovy.control.CompilationFailedException
 *  org.codehaus.groovy.control.CompilerConfiguration
 *  org.codehaus.groovy.runtime.InvokerHelper
 *  org.springframework.scripting.ScriptCompilationException
 *  org.springframework.scripting.ScriptSource
 *  org.springframework.scripting.support.ResourceScriptSource
 *  org.springframework.util.DigestUtils
 */
package com.kuma.boot.common.support.groovy;

import com.kuma.boot.common.support.groovy.AbstractScriptEvaluator;
import com.kuma.boot.common.utils.lang.StringUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.DigestUtils;

public class GroovyShellSingletonScriptEvaluator
extends AbstractScriptEvaluator {
    private final GroovyClassLoader loader;
    private final CompilerConfiguration config;
    private final GroovyCacheShell groovyShell;

    public GroovyShellSingletonScriptEvaluator() {
        this(null, null);
    }

    public GroovyShellSingletonScriptEvaluator(ClassLoader classLoader, CompilerConfiguration config) {
        this.config = null != config ? config : new CompilerConfiguration();
        ClassLoader parentLoader = null != classLoader ? classLoader : GroovyShell.class.getClassLoader();
        this.loader = parentLoader instanceof GroovyClassLoader && ((GroovyClassLoader)parentLoader).hasCompatibleConfiguration(this.config) ? (GroovyClassLoader)parentLoader : this.doPrivileged(() -> new GroovyClassLoader(parentLoader, this.config));
        this.groovyShell = new GroovyCacheShell((ClassLoader)this.loader, this.config);
    }

    public Object evaluate(ScriptSource script, Map<String, Object> arguments) throws ScriptCompilationException {
        GroovyCodeSource gcs = this.convert(script);
        try {
            return this.groovyShell.evaluate(gcs, new Binding(arguments));
        }
        catch (GroovyRuntimeException ex) {
            throw new ScriptCompilationException(script, (Throwable)ex);
        }
    }

    protected GroovyCodeSource convert(ScriptSource script) {
        String fileName;
        String text;
        try {
            text = script.getScriptAsString();
        }
        catch (IOException e) {
            throw new ScriptCompilationException(script, "Cannot access Groovyoovy script", (Throwable)e);
        }
        String string = fileName = script instanceof ResourceScriptSource ? ((ResourceScriptSource)script).getResource().getFilename() : null;
        if (null == fileName) {
            fileName = StringUtils.join("Script_Ext_", DigestUtils.md5DigestAsHex((byte[])text.getBytes(StandardCharsets.UTF_8)), ".groovy");
        }
        return this.convert(text, fileName);
    }

    protected GroovyCodeSource convert(String text, String fileName) {
        GroovyCodeSource gcs = AccessController.doPrivileged(() -> new GroovyCodeSource(text, fileName, "/groovy/script"));
        gcs.setCachable(Boolean.TRUE.booleanValue());
        return gcs;
    }

    public GroovyClassLoader getGroovyClassLoader() {
        return this.loader;
    }

    public CompilerConfiguration getConfig() {
        return this.config;
    }

    private <T> T doPrivileged(PrivilegedAction<T> action) {
        return AccessController.doPrivileged(action);
    }

    public static class GroovyCacheShell
    extends GroovyShell {
        public GroovyCacheShell(ClassLoader parent, CompilerConfiguration config) {
            super(parent, config);
        }

        protected Class parseClass(GroovyCodeSource codeSource) throws CompilationFailedException {
            return super.getClassLoader().parseClass(codeSource);
        }

        public Script parse(GroovyCodeSource codeSource, Binding binding) throws CompilationFailedException {
            return InvokerHelper.createScript((Class)this.parseClass(codeSource), (Binding)binding);
        }

        public Object evaluate(GroovyCodeSource codeSource, Binding binding) throws CompilationFailedException {
            Script script = this.parse(codeSource, binding);
            return script.run();
        }
    }
}

