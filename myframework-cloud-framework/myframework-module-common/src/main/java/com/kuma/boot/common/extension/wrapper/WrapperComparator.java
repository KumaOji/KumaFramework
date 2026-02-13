/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.wrapper;

import com.kuma.boot.common.extension.active.ActivateComparator;
import java.util.Comparator;

public class WrapperComparator
extends ActivateComparator {
    public static final Comparator<Object> COMPARATOR = new WrapperComparator();
}

