package com.nexus.morphinalert;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.SharedPreferences;

public class WhatsAppNotificationListener extends NotificationListenerService {
    @Override public void onListenerConnected() {
        super.onListenerConnected();
        getSharedPreferences("hunter", MODE_PRIVATE).edit()
            .putLong("listener_connected", System.currentTimeMillis()).apply();
    }

    @Override public void onNotificationPosted(StatusBarNotification notification) {
        if (notification == null) return;
        String packageName = notification.getPackageName();
        if (!"com.whatsapp".equals(packageName)
                && !"com.whatsapp.w4b".equals(packageName)) return;

        String key = notification.getKey();
        long now = System.currentTimeMillis();
        SharedPreferences prefs = getSharedPreferences("hunter", MODE_PRIVATE);
        String previousKey = prefs.getString("last_key", "");
        long previousTime = prefs.getLong("last_time", 0L);
        if (key != null && key.equals(previousKey) && now - previousTime < 2500L) return;

        prefs.edit()
            .putString("last_key", key == null ? "" : key)
            .putLong("last_time", now)
            .putLong("last_event", now)
            .putLong("count", prefs.getLong("count", 0L) + 1L)
            .putLong("alert_attempt", now)
            .apply();
        AlertEngine.play(this, false);
    }
}
