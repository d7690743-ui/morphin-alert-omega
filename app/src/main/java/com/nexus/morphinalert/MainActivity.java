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

public class MainActivity extends Activity {
    private LinearLayout root;
    private TextView status;
    private WebView web;
    private boolean showing = false;

    private int dp(int n) {
        return (int) (n * getResources().getDisplayMetrics().density + 0.5f);
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

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(12), dp(14), dp(8));
        root.setBackgroundColor(Color.rgb(13, 21, 30));

        TextView title = makeText("MORPHIN ALERT Ω", 25, Color.rgb(64, 232, 165));
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(60)));

        status = makeText("RADAR: comprobando permisos", 16, Color.WHITE);
        status.setBackgroundColor(Color.rgb(27, 43, 54));
        root.addView(status, new LinearLayout.LayoutParams(-1, dp(55)));

        Button permission = makeButton("1. Activar acceso a notificaciones");
        permission.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } catch (Exception e) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        });
        root.addView(permission, new LinearLayout.LayoutParams(-1, dp(54)));

        Button test = makeButton("2. Probar sonido Morphin");
        test.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { AlertEngine.play(MainActivity.this, true); }
        });
        root.addView(test, new LinearLayout.LayoutParams(-1, dp(54)));

        Button webButton = makeButton("3. Abrir / ocultar WhatsApp Web");
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showing = !showing;
                web.setVisibility(showing ? View.VISIBLE : View.GONE);
            }
        });
        root.addView(webButton, new LinearLayout.LayoutParams(-1, dp(54)));

        web = new WebView(this);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.loadUrl("https://web.whatsapp.com/");
        web.setVisibility(View.GONE);
        root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1));

        root.addView(makeText("WhatsApp Web es auxiliar. El detector Android funciona sin ella.", 11, Color.GRAY),
            new LinearLayout.LayoutParams(-1, dp(52)));
        setContentView(root);
        refreshStatus();
    }

    private void refreshStatus() {
        String enabled = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (enabled != null && enabled.contains(getPackageName())) {
            status.setText("RADAR ACTIVO - Escuchando notificaciones");
        } else {
            status.setText("RADAR INACTIVO - Falta conceder acceso");
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (status != null) refreshStatus();
    }

    @Override public void onBackPressed() {
        if (showing) {
            showing = false;
            web.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
