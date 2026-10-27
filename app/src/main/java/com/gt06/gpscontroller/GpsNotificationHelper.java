package com.gt06.gpscontroller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import java.util.regex.Pattern;

public class GpsNotificationHelper {

    public static final String CHANNEL_ID = "gt06_alarm_alerts";
    public static final String CHANNEL_NAME = "🚨 Alarm Keamanan Kendaraan";

    public static class AlarmInfo {
        public final String type;
        public final String title;
        public final String description;
        public final int id;

        public AlarmInfo(String type, String title, String description, int id) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.id = id;
        }
    }

    public static void initChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            NotificationChannel existing = nm.getNotificationChannel(CHANNEL_ID);
            if (existing == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifikasi darurat peringatan overspeed, cut power, dan low battery");
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 500, 200, 500, 200, 500});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (soundUri != null) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build();
                    channel.setSound(soundUri, audioAttributes);
                }
                nm.createNotificationChannel(channel);
            }
        }
    }

    public static AlarmInfo detectAlarm(String message) {
        if (message == null || message.trim().isEmpty()) return null;

        // 1. Alarm Melebihi Batas Kecepatan (Over-speed Alarm)
        if (Pattern.compile("(?i)(over\\s*speed|overspeed|speeding|speed\\s*alarm)").matcher(message).find()) {
            return new AlarmInfo(
                    "OVERSPEED",
                    "🚨 PERINGATAN: Melebihi Batas Kecepatan!",
                    "Kendaraan terdeteksi melaju melebihi batas kecepatan yang ditentukan.",
                    1001
            );
        }

        // 2. Alarm Putus Daya Aki (Cut Power Alarm)
        if (Pattern.compile("(?i)(cut\\s*power|power\\s*cut|power\\s*off\\s*alarm|external\\s*power\\s*cut)").matcher(message).find()) {
            return new AlarmInfo(
                    "CUT_POWER",
                    "🚨 BAHAYA: Cut Power Alarm (Aki Diputus)!",
                    "Kabel aki / suplai daya eksternal perangkat GPS terputus!",
                    1002
            );
        }

        // 3. Alarm Baterai Lemah (Low Battery Alarm)
        if (Pattern.compile("(?i)(low\\s*battery|battery\\s*low|low\\s*power\\s*alarm)").matcher(message).find()) {
            return new AlarmInfo(
                    "LOW_BATTERY",
                    "🪫 PERINGATAN: Baterai GPS Lemah (Low Battery)!",
                    "Daya baterai internal GPS cadangan hampir habis (< 3.7V).",
                    1003
            );
        }

        // 4. Alarm Getaran (Sensor/Vibration Alarm)
        if (Pattern.compile("(?i)(sensor\\s*alarm|vibration\\s*alarm|shock\\s*alarm)").matcher(message).find()) {
            return new AlarmInfo(
                    "VIBRATION",
                    "📳 ALARM GETARAN: Terdeteksi Guncangan/Getaran!",
                    "Kendaraan mendeteksi guncangan atau getaran saat posisi parkir (ACC OFF).",
                    1004
            );
        }

        return null;
    }

    public static AlarmInfo checkAndTriggerAlarmNotification(Context context, String message) {
        AlarmInfo alarm = detectAlarm(message);
        if (alarm == null) return null;

        initChannel(context);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return alarm;

        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchIntent.putExtra("extra_alarm_type", alarm.type);
        launchIntent.putExtra("extra_alarm_msg", message);

        PendingIntent pi = PendingIntent.getActivity(
                context,
                alarm.id,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(alarm.title)
                .setContentText(message)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(alarm.description + "\n\nIsi SMS: " + message)
                        .setSummaryText("GT06 Keamanan"))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_MAX);

        nm.notify(alarm.id, builder.build());
        return alarm;
    }
}
