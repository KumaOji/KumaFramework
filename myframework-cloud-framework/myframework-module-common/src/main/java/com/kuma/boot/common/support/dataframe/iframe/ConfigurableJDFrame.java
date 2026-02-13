/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.GroupJDFrame;
import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.JoinJDFrame;
import com.kuma.boot.common.support.dataframe.iframe.OperationJDFrame;
import com.kuma.boot.common.support.dataframe.iframe.OverJDFrame;
import com.kuma.boot.common.support.dataframe.iframe.WhereJDFrame;

public interface ConfigurableJDFrame<T>
extends IFrame<T>,
JoinJDFrame<T>,
WhereJDFrame<T>,
GroupJDFrame<T>,
OverJDFrame<T>,
OperationJDFrame<T> {
}

