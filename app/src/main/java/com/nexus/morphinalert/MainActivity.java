package com.nexus.morphinalert;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.SharedPreferences;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView status, diagnostics;
    private WebView web;
    private boolean webVisible = false;
    private final Handler handler = new Handler();
    private final Runnable refresher = new Runnable() {
        @Override public void run() {
            refreshStatus();
            handler.postDelayed(this, 2000);
        }
    };

    private int dp(int n) {
        return (int)(n * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView makeText(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(17);
        t.setPadding(dp(8), dp(8), dp(8), dp(8));
        return t;
    }

    private Button makeButton(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.rgb(24, 95, 75));
        return b;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(12), dp(12), dp(8));
        root.setBackgroundColor(Color.rgb(13, 21, 30));

        TextView title = makeText("MORPHIN ALERT Ω", 24, Color.rgb(64, 232, 165));
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(56)));

        status = makeText("Comprobando el radar...", 15, Color.WHITE);
        status.setBackgroundColor(Color.rgb(27, 43, 54));
        root.addView(status, new LinearLayout.LayoutParams(-1, dp(55)));

        diagnostics = makeText("Sin datos todavía", 13, Color.LTGRAY);
        root.addView(diagnostics, new LinearLayout.LayoutParams(-1, dp(90)));

        Button permission = makeButton("1. Acceso a notificaciones");
        permission.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } catch (Exception e) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        });
        root.addView(permission, new LinearLayout.LayoutParams(-1, dp(48)));

        Button test = makeButton("2. Probar sonido y vibración");
        test.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AlertEngine.play(MainActivity.this, true);
                getSharedPreferences("hunter", MODE_PRIVATE).edit()
                    .putLong("test_event", System.currentTimeMillis()).apply();
                refreshStatus();
            }
        });
        root.addView(test, new LinearLayout.LayoutParams(-1, dp(48)));

        Button webButton = makeButton("3. WhatsApp Web (escritorio)");
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                webVisible = !webVisible;
                web.setVisibility(webVisible ? View.VISIBLE : View.GONE);
            }
        });
        root.addView(webButton, new LinearLayout.LayoutParams(-1, dp(48)));

        web = new WebView(this);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUserAgentString(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        );
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.setVisibility(View.GONE);
        root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1));
        web.loadUrl("https://web.whatsapp.com/");

        TextView note = makeText(
            "WhatsApp Web puede bloquear WebView aunque se solicite escritorio. " +
            "El detector principal funciona sin WhatsApp Web.", 11, Color.GRAY);
        root.addView(note, new LinearLayout.LayoutParams(-1, dp(58)));

        setContentView(root);
        refreshStatus();
    }

    private String clock(long timestamp) {
        if (timestamp == 0L) return "ninguna";
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            .format(new Date(timestamp));
    }

    private void refreshStatus() {
        if (status == null) return;
        String enabled = Settings.Secure.getString(
            getContentResolver(), "enabled_notification_listeners");
        boolean permitted = enabled != null && enabled.contains(getPackageName());
        SharedPreferences prefs = getSharedPreferences("hunter", MODE_PRIVATE);
        long connected = prefs.getLong("listener_connected", 0L);
        long last = prefs.getLong("last_event", 0L);
        long test = prefs.getLong("test_event", 0L);
        long alerts = prefs.getLong("alert_attempt", 0L);
        long count = prefs.getLong("count", 0L);
        status.setText(permitted ? "PERMISO CONCEDIDO - Radar configurado"
                                 : "SIN PERMISO - Activa acceso a notificaciones");
        diagnostics.setText(
            "Servicio conectado alguna vez: " + clock(connected) +
            "\nÚltimo evento WhatsApp: " + clock(last) +
            " | Eventos: " + count +
            "\nÚltimo intento de alerta: " + clock(alerts) +
            " | Prueba manual: " + clock(test));
    }

    @Override protected void onResume() {
        super.onResume();
        handler.removeCallbacks(refresher);
        handler.post(refresher);
    }

    @Override protected void onPause() {
        handler.removeCallbacks(refresher);
        super.onPause();
    }

    @Override public void onBackPressed() {
        if (webVisible) {
            webVisible = false;
            web.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override protected void onDestroy() {
        handler.removeCallbacks(refresher);
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
