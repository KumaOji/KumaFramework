/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.event;

import com.kuma.boot.security.spring.event.domain.DialogueMessage;
import java.time.Clock;

public class LocalSendDialogueMessageEvent
extends LocalApplicationEvent<DialogueMessage> {
    public LocalSendDialogueMessageEvent(DialogueMessage data) {
        super(data);
    }

    public LocalSendDialogueMessageEvent(DialogueMessage data, Clock clock) {
        super(data, clock);
    }
}

