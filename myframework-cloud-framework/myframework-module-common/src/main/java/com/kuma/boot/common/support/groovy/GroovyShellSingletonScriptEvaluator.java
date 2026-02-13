package com.kuma.boot.common.support.groovy;

import static org.codehaus.groovy.runtime.InvokerHelper.createScript;

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

/**
 * GroovyShellSingletonScriptEvaluator
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class GroovyShellSingletonScriptEvaluator extends AbstractScriptEvaluator {

    private final GroovyClassLoader loader;
    private final CompilerConfiguration config;
    private final GroovyCacheShell groovyShell;

    public GroovyShellSingletonScriptEvaluator() {
        this(null, null);
    }

    public GroovyShellSingletonScriptEvaluator( ClassLoader classLoader, CompilerConfiguration config ) {
        if (null != config) {
            this.config = config;
        } else {
            this.config = new CompilerConfiguration();
        }
        ClassLoader parentLoader = ( null != classLoader ) ? classLoader : GroovyShell.class.getClassLoader();
        if (parentLoader instanceof GroovyClassLoader
                && ( (GroovyClassLoader) parentLoader ).hasCompatibleConfiguration(
                this.config)) {
            this.loader = (GroovyClassLoader) parentLoader;
        } else {
            this.loader = this.doPrivileged(() -> new GroovyClassLoader(parentLoader, this.config));
        }
        this.groovyShell = new GroovyCacheShell(loader, this.config);
    }

    @Override
    public Object evaluate( ScriptSource script, Map<String, Object> arguments ) throws ScriptCompilationException {
        //GroovyCacheShell groovyShell = new GroovyCacheShell(this.getGroovyClassLoader()，this.getConfig())；
        GroovyCodeSource gcs = this.convert(script);
        try {
            return this.groovyShell.evaluate(gcs, new Binding(arguments));
        } catch (GroovyRuntimeException ex) {
            throw new ScriptCompilationException(script, ex);
        }
    }

    protected GroovyCodeSource convert( ScriptSource script ) {
        String text;
        try {
            text = script.getScriptAsString();
        } catch (IOException e) {
            throw new ScriptCompilationException(script, "Cannot access Groovyoovy script", e);
        }

        String fileName = ( script instanceof ResourceScriptSource ?
                ( (ResourceScriptSource) script ).getResource().getFilename() : null );
        if (null == fileName) {
            fileName = StringUtils.join("Script_Ext_",
                    DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8)), ".groovy");
        }
        return this.convert(text, fileName);
    }

    protected GroovyCodeSource convert( String text, String fileName ) {
        GroovyCodeSource gcs = AccessController.doPrivileged(
                (PrivilegedAction<GroovyCodeSource>) () -> new GroovyCodeSource(text, fileName, "/groovy/script"));
        //使用缓存是关键.虽然newv GroovyCodeSource()默认cache是true，这里还是显示的再设置一次，为了突出重点.
        gcs.setCachable(Boolean.TRUE);
        return gcs;
    }

    public GroovyClassLoader getGroovyClassLoader() {
        return this.loader;
    }

    public CompilerConfiguration getConfig() {
        return config;
    }

    private <T> T doPrivileged( PrivilegedAction<T> action ) {
        return AccessController.doPrivileged(action);
    }

    /**
     * GroovyCacheShell
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class GroovyCacheShell extends GroovyShell {

        public GroovyCacheShell( ClassLoader parent, CompilerConfiguration config ) {
            super(parent, config);
        }

        protected Class parseClass( final GroovyCodeSource codeSource ) throws CompilationFailedException {
            return super.getClassLoader().parseClass(codeSource);
        }

        public Script parse( final GroovyCodeSource codeSource, final Binding binding )
                throws CompilationFailedException {
            return createScript(this.parseClass(codeSource), binding);
        }

        public Object evaluate( GroovyCodeSource codeSource, Binding binding ) throws CompilationFailedException {
            Script script = this.parse(codeSource, binding);
            return script.run();
        }
    }
}
