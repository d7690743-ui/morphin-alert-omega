package com.nexus.morphinalert;

import android.content.Context;
import android.media.ToneGenerator;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.os.Build;
import android.os.VibrationEffect;

public class AlertEngine {
    private static long lastPlayed = 0L;
    private static ToneGenerator tone;

    public static synchronized void play(final Context context, boolean test) {
        long now = System.currentTimeMillis();
        if (!test && now - lastPlayed < 1800L) return;
        lastPlayed = now;

        try {
            if (tone == null) {
                tone = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90);
            }
            final ToneGenerator player = tone;
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override public void run() {
                    player.startTone(ToneGenerator.TONE_DTMF_5, 140);
                    handler.postDelayed(new Runnable() {
                        @Override public void run() {
                            player.startTone(ToneGenerator.TONE_DTMF_7, 140);
                        }
                    }, 190);
                    handler.postDelayed(new Runnable() {
                        @Override public void run() {
                            player.startTone(ToneGenerator.TONE_DTMF_9, 260);
                        }
                    }, 390);
                }
            });
        } catch (Exception ignored) { }

        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = new long[] {0, 120, 70, 180};
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    vibrator.vibrate(pattern, -1);
                }
            }
        } catch (Exception ignored) { }
    }
}
