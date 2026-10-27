package com.gt06.gpscontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class GpsSmsReceiver extends BroadcastReceiver {

    public static final String ACTION_GPS_SMS = "com.gt06.gpscontroller.SMS_RECEIVED";
    public static final String EXTRA_SENDER = "extra_sender";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_TIMESTAMP = "extra_timestamp";

    private static final String PREFS_NAME = "GT06_PREFERENCES";
    private static final String KEY_LAST_SMS = "LAST_TRACKER_SMS";
    public static final String KEY_PHONE_NUMBER = "GPS_PHONE_NUMBER";

    public static boolean isMatchingSender(String incomingSender, String targetNumber) {
        if (incomingSender == null || targetNumber == null) return false;
        String cleanIncoming = incomingSender.replaceAll("[^0-9]", "");
        String cleanTarget = targetNumber.replaceAll("[^0-9]", "");
        if (cleanIncoming.isEmpty() || cleanTarget.isEmpty()) return false;
        if (cleanIncoming.equals(cleanTarget)) return true;
        if (cleanIncoming.endsWith(cleanTarget) || cleanTarget.endsWith(cleanIncoming)) return true;

        int minLen = Math.min(cleanIncoming.length(), cleanTarget.length());
        int compareLen = Math.min(minLen, 9);
        if (compareLen >= 7) {
            String suffix1 = cleanIncoming.substring(cleanIncoming.length() - compareLen);
            String suffix2 = cleanTarget.substring(cleanTarget.length() - compareLen);
            return suffix1.equals(suffix2);
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");

                if (pdus != null && pdus.length > 0) {
                    StringBuilder fullBody = new StringBuilder();
                    String sender = "";
                    long timestamp = System.currentTimeMillis();

                    for (Object pdu : pdus) {
                        SmsMessage message;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            message = SmsMessage.createFromPdu((byte[]) pdu, format);
                        } else {
                            message = SmsMessage.createFromPdu((byte[]) pdu);
                        }

                        if (message != null) {
                            sender = message.getDisplayOriginatingAddress();
                            fullBody.append(message.getMessageBody());
                            timestamp = message.getTimestampMillis();
                        }
                    }

                    // Filter nomor pengirim: Hanya proses SMS jika berasal dari nomor kartu SIM GPS Tracker
                    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String targetGpsNumber = prefs.getString(KEY_PHONE_NUMBER, "").trim();
                    if (!targetGpsNumber.isEmpty() && !isMatchingSender(sender, targetGpsNumber)) {
                        // Abaikan SMS dari pengirim selain SIM Tracker GPS
                        return;
                    }

                    String messageBody = fullBody.toString();

                    // Periksa dan picu notifikasi darurat sistem jika ada alarm keamanan
                    GpsNotificationHelper.checkAndTriggerAlarmNotification(context, messageBody);

                    // Simpan ke SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit()
                            .putString(KEY_LAST_SMS, messageBody)
                            .putLong(EXTRA_TIMESTAMP, timestamp);

                    // Sinkronisasi status Relay ke Widget
                    if (messageBody.contains("Cut off the fuel supply") || messageBody.contains("Cut fuel") || messageBody.contains("DYD success")) {
                        editor.putBoolean("SAVED_RELAY_STATE", true);
                        editor.apply();
                        EngineControlWidget.updateAllWidgets(context);
                    } else if (messageBody.contains("Restore fuel supply") || messageBody.contains("Restore fuel") || messageBody.contains("HFYD success")) {
                        editor.putBoolean("SAVED_RELAY_STATE", false);
                        editor.apply();
                        EngineControlWidget.updateAllWidgets(context);
                    } else {
                        editor.apply();
                    }

                    // Broadcast ke MainActivity
                    Intent broadcastIntent = new Intent(ACTION_GPS_SMS);
                    broadcastIntent.setPackage(context.getPackageName());
                    broadcastIntent.putExtra(EXTRA_SENDER, sender);
                    broadcastIntent.putExtra(EXTRA_MESSAGE, messageBody);
                    broadcastIntent.putExtra(EXTRA_TIMESTAMP, timestamp);
                    context.sendBroadcast(broadcastIntent);
                }
            }
        }
    }
}
