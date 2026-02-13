/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.FastByteArrayOutputStream
 */
package com.kuma.boot.common.support.compiler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.springframework.util.FastByteArrayOutputStream;

public class InMemoryJavaCompiler {
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    public static byte[] compile(String className, CharSequence sourceCode) {
        MemoryJavaFileObject file = new MemoryJavaFileObject(className, sourceCode);
        JavaCompiler.CompilationTask task = InMemoryJavaCompiler.getCompilationTask(file);
        if (!task.call().booleanValue()) {
            throw new IllegalArgumentException("Could not compile " + className + " with source code :\t" + String.valueOf(sourceCode));
        }
        return file.getByteCode();
    }

    private static JavaCompiler.CompilationTask getCompilationTask(MemoryJavaFileObject file) {
        return COMPILER.getTask(null, new FileManagerWrapper(file), null, null, null, Collections.singletonList(file));
    }

    private static class MemoryJavaFileObject
    extends SimpleJavaFileObject {
        private final String className;
        private final CharSequence sourceCode;
        private final FastByteArrayOutputStream byteCode;

        public MemoryJavaFileObject(String className, CharSequence sourceCode) {
            super(URI.create("string:///" + className.replace(".", "/") + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.className = className;
            this.sourceCode = sourceCode;
            this.byteCode = new FastByteArrayOutputStream();
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return this.sourceCode;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return this.byteCode;
        }

        public byte[] getByteCode() {
            return this.byteCode.toByteArray();
        }

        public String getClassName() {
            return this.className;
        }
    }

    private static class FileManagerWrapper
    extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final MemoryJavaFileObject file;

        public FileManagerWrapper(MemoryJavaFileObject file) {
            super(COMPILER.getStandardFileManager(null, null, null));
            this.file = file;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            if (!this.file.getClassName().equals(className)) {
                throw new IOException("Expected class with name " + this.file.getClassName() + ", but got " + className);
            }
            return this.file;
        }
    }
}

