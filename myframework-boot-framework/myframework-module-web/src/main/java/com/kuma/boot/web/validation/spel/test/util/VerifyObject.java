/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.intellij.lang.annotations.Language
 */
package com.kuma.boot.web.validation.spel.test.util;

import com.kuma.boot.web.validation.spel.core.SpelValidContext;

import java.util.Arrays;
import java.util.Collection;
import org.intellij.lang.annotations.Language;

public class VerifyObject {
    private Object object;
    private Collection<VerifyFailedField> verifyFailedFields;
    private boolean expectException;
    @Language(value="spel")
    private String[] spelGroups;
    private SpelValidContext context = SpelValidContext.getDefault();

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Collection<VerifyFailedField> getVerifyFailedFields() {
        return this.verifyFailedFields;
    }

    public void setVerifyFailedFields(Collection<VerifyFailedField> verifyFailedFields) {
        this.verifyFailedFields = verifyFailedFields;
    }

    public boolean isExpectException() {
        return this.expectException;
    }

    public void setExpectException(boolean expectException) {
        this.expectException = expectException;
    }

    public String[] getSpelGroups() {
        return this.spelGroups;
    }

    public void setSpelGroups(String[] spelGroups) {
        this.spelGroups = spelGroups;
    }

    public SpelValidContext getContext() {
        return this.context;
    }

    private VerifyObject() {
    }

    public static VerifyObject of(Object object) {
        return VerifyObject.of(object, new VerifyFailedField[0]);
    }

    public static VerifyObject of(Object object, boolean expectException) {
        return VerifyObject.of(object, new VerifyFailedField[0], expectException);
    }

    public static VerifyObject of(Object object, VerifyFailedField ... verifyFailedFields) {
        return VerifyObject.of(object, verifyFailedFields, false);
    }

    public static VerifyObject of(Object object, Collection<VerifyFailedField> verifyFailedFields) {
        return VerifyObject.of(object, verifyFailedFields, false);
    }

    public static VerifyObject of(Object object, Collection<VerifyFailedField> verifyFailedFields1, VerifyFailedField ... verifyFailedFields2) {
        verifyFailedFields1.addAll(Arrays.asList(verifyFailedFields2));
        return VerifyObject.of(object, verifyFailedFields1, false);
    }

    public static VerifyObject of(Object object, VerifyFailedField[] verifyFailedFields, boolean expectException) {
        return VerifyObject.of(object, Arrays.asList(verifyFailedFields), expectException);
    }

    public static VerifyObject of(Object object, Collection<VerifyFailedField> verifyFailedFields, boolean expectException) {
        VerifyObject verifyObject = new VerifyObject();
        verifyObject.setObject(object);
        verifyObject.setVerifyFailedFields(verifyFailedFields);
        verifyObject.setExpectException(expectException);
        return verifyObject;
    }

    public VerifyObject setGroups(String ... spelGroups) {
        this.spelGroups = spelGroups;
        return this;
    }

    public VerifyObject setContext(SpelValidContext context) {
        this.context = context;
        return this;
    }
}

