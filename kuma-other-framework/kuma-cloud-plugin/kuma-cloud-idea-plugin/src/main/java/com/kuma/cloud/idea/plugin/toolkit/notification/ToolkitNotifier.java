package com.kuma.cloud.idea.plugin.toolkit.notification;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;

/**
 * Toolkit Notifier
 */
public class ToolkitNotifier {

    private static final String GROUP_ID = "Toolkit Information";

    private ToolkitNotifier() {
    }

    public static void success(String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(message, NotificationType.INFORMATION)
                .notify(null);
    }

    public static void error(String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(message, NotificationType.ERROR)
                .notify(null);
    }
}
