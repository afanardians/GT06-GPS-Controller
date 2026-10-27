package com.gt06.gpscontroller;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Android Home Screen AppWidget untuk kontrol ON/OFF mesin mobil Rush Control
 */
public class EngineControlWidget extends AppWidgetProvider {

    public static final String ACTION_WIDGET_TOGGLE = "com.gt06.gpscontroller.ACTION_WIDGET_TOGGLE";
    public static final String EXTRA_PROMPT_STOP_ENGINE = "extra_prompt_stop_engine";

    private static final String PREFS_NAME = "GT06_PREFERENCES";
    private static final String KEY_PHONE_NUMBER = "GPS_PHONE_NUMBER";
    private static final String KEY_RELAY_STATE = "SAVED_RELAY_STATE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isCutOff = prefs.getBoolean(KEY_RELAY_STATE, false);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_engine_control);

        views.setTextViewText(R.id.tvWidgetTitle, "GPS Control");
        views.setImageViewResource(R.id.ivWidgetIcon, R.drawable.app_logo);

        if (isCutOff) {
            // Mesin sedang mati -> Tampilkan status MATI dan tombol hijau (untuk menyalakan)
            views.setImageViewResource(R.id.ibWidgetPower, R.drawable.btn_power_green);
            views.setTextViewText(R.id.tvWidgetStatusBadge, "STATUS: MATI");
            views.setTextColor(R.id.tvWidgetStatusBadge, 0xFFEF4444);
        } else {
            // Mesin sedang normal / menyala -> Tampilkan status NORMAL dan tombol merah (untuk mematikan)
            views.setImageViewResource(R.id.ibWidgetPower, R.drawable.btn_power_red);
            views.setTextViewText(R.id.tvWidgetStatusBadge, "STATUS: NORMAL");
            views.setTextColor(R.id.tvWidgetStatusBadge, 0xFF22C55E);
        }

        // Klik Tombol Power -> Eksekusi Toggle / Konfirmasi
        Intent toggleIntent = new Intent(context, EngineControlWidget.class);
        toggleIntent.setAction(ACTION_WIDGET_TOGGLE);
        PendingIntent pToggle = PendingIntent.getBroadcast(
                context,
                0,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        views.setOnClickPendingIntent(R.id.ibWidgetPower, pToggle);

        // Klik Area Header/Background -> Buka Aplikasi Rush Control
        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pOpenApp = PendingIntent.getActivity(
                context,
                1,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        views.setOnClickPendingIntent(R.id.widgetRoot, pOpenApp);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent != null && ACTION_WIDGET_TOGGLE.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean isCutOff = prefs.getBoolean(KEY_RELAY_STATE, false);
            String phoneNumber = prefs.getString(KEY_PHONE_NUMBER, "").trim();

            if (isCutOff) {
                // Mesin sedang mati -> Nyalakan langsung via SMS RELAY,0#
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(context, "Atur nomor SIM GPS di aplikasi terlebih dahulu!", Toast.LENGTH_LONG).show();
                    openApp(context, false);
                    return;
                }

                try {
                    SmsManager smsManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        smsManager = context.getSystemService(SmsManager.class);
                    } else {
                        smsManager = SmsManager.getDefault();
                    }
                    if (smsManager == null) {
                        smsManager = SmsManager.getDefault();
                    }

                    smsManager.sendTextMessage(phoneNumber, null, "RELAY,0#", null, null);
                    prefs.edit().putBoolean(KEY_RELAY_STATE, false).apply();
                    updateAllWidgets(context);

                    Toast.makeText(context, "🟢 Menyalakan Mesin: Perintah terkirim ke " + phoneNumber, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, "Gagal kirim SMS via widget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    openApp(context, false);
                }
            } else {
                // Mesin sedang menyala -> Buka aplikasi dengan dialog konfirmasi keamanan mematikan mesin
                Toast.makeText(context, "⚠️ Membuka validasi keamanan matikan mesin...", Toast.LENGTH_SHORT).show();
                openApp(context, true);
            }
        }
    }

    private void openApp(Context context, boolean promptStopEngine) {
        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (promptStopEngine) {
            appIntent.putExtra(EXTRA_PROMPT_STOP_ENGINE, true);
        }
        context.startActivity(appIntent);
    }

    /**
     * Memperbarui seluruh widget yang ada di home screen secara real-time
     */
    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, EngineControlWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }
}
