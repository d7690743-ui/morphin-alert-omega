package com.nexus.morphinalert;
import android.service.notification.*;
import android.content.*;

public class WhatsAppNotificationListener extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        if (notification == null) return;
        String packageName = notification.getPackageName();
        if (!"com.whatsapp".equals(packageName) && !"com.whatsapp.w4b".equals(packageName)) return;

        // Solo se guarda una clave técnica para evitar duplicados; no se lee el texto.
        String key = notification.getKey();
        long now = System.currentTimeMillis();
        SharedPreferences prefs = getSharedPreferences("hunter", MODE_PRIVATE);
        String previousKey = prefs.getString("key", "");
        long previousTime = prefs.getLong("time", 0L);
        if (key != null && key.equals(previousKey) && now - previousTime < 2500L) return;

        prefs.edit().putString("key", key == null ? "" : key)
            .putLong("time", now).putLong("last_event", now).apply();
        AlertEngine.play(this, false);
    }
}
