/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.GroupSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.JoinSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.OperationSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.OverSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.WhereSDFrame;

public interface ConfigurableSDFrame<T>
extends IFrame<T>,
WhereSDFrame<T>,
JoinSDFrame<T>,
GroupSDFrame<T>,
OverSDFrame<T>,
OperationSDFrame<T> {
}

