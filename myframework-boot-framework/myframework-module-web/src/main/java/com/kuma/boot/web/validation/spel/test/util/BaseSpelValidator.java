/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.test.util;

import com.kuma.boot.web.validation.spel.core.SpelValidContext;
import com.kuma.boot.web.validation.spel.core.SpelValidExecutor;
import com.kuma.boot.web.validation.spel.core.result.ObjectValidResult;

import java.util.List;

public class BaseSpelValidator
extends AbstractSpelValidator {
    private static final BaseSpelValidator INSTANCE = new BaseSpelValidator();

    public static boolean check(List<VerifyObject> verifyObjectList) {
        return INSTANCE.checkConstraintResult(verifyObjectList);
    }

    @Override
    public ObjectValidResult validate(Object obj, String[] spelGroups, SpelValidContext context) {
        return SpelValidExecutor.validateObject(obj, spelGroups, context);
    }
}

