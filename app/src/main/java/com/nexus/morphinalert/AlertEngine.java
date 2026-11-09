package com.nexus.morphinalert;
import android.content.*;
import android.media.*;
import android.os.*;

public class AlertEngine {
    private static long last = 0;

    public static synchronized void play(Context context, boolean test) {
        long now = System.currentTimeMillis();
        if (!test && now - last < 1800) return;
        last = now;

        try {
            final MediaPlayer player = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd("morphin_alert.wav");
            player.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer p) { p.release(); }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer p, int what, int extra) {
                    p.release();
                    return true;
                }
            });
            player.prepare();
            player.start();
        } catch (Exception ignored) { }

        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 120, 70, 180};
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    vibrator.vibrate(pattern, -1);
                }
            }
        } catch (Exception ignored) { }
    }
}
