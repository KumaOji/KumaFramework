/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.io.FileUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.web.multipart.MultipartFile
 */
package com.kuma.boot.web.support.multipart;

import com.kuma.boot.common.utils.io.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

public class ByteMultipartFile
implements MultipartFile {
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] bytes;

    public ByteMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        Assert.hasLength((String)name, (String)"Name must not be empty");
        this.name = name;
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType;
        this.bytes = content != null ? content : new byte[]{};
    }

    public ByteMultipartFile(String name, byte[] content) {
        this(name, "", "", content);
    }

    public ByteMultipartFile(String name, String originalFilename, String contentType, InputStream contentStream) throws IOException {
        this(name, originalFilename, contentType, FileUtils.copyToByteArray((InputStream)contentStream));
    }

    public ByteMultipartFile(String name, InputStream contentStream) throws IOException {
        this(name, FileUtils.copyToByteArray((InputStream)contentStream));
    }

    public String getName() {
        return this.name;
    }

    public String getOriginalFilename() {
        return this.originalFilename;
    }

    public String getContentType() {
        return this.contentType;
    }

    public boolean isEmpty() {
        return ObjectUtils.isEmpty((Object)this.bytes);
    }

    public long getSize() {
        return this.bytes.length;
    }

    public byte[] getBytes() throws IOException {
        return this.bytes;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.bytes);
    }

    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileUtils.copy((byte[])this.bytes, (File)dest);
    }
}

