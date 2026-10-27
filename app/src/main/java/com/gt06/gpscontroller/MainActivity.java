package com.gt06.gpscontroller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.app.KeyguardManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "GT06_PREFERENCES";
    private static final String KEY_PHONE_NUMBER = "GPS_PHONE_NUMBER";
    private static final String KEY_DIRECT_SMS = "USE_DIRECT_SMS";

    private static final String KEY_SAVED_LAT = "SAVED_LAT";
    private static final String KEY_SAVED_LON = "SAVED_LON";
    private static final String KEY_SAVED_IMEI = "SAVED_IMEI";
    private static final String KEY_SAVED_CENTER = "SAVED_CENTER";
    private static final String KEY_SAVED_TIMER = "SAVED_TIMER";
    private static final String KEY_SAVED_PARAM_SENDS = "SAVED_PARAM_SENDS";
    private static final String KEY_SAVED_SOS = "SAVED_SOS";
    private static final String KEY_SAVED_PARAM_DEFENSE = "SAVED_PARAM_DEFENSE";
    private static final String KEY_SAVED_SENSOR = "SAVED_SENSOR";
    private static final String KEY_SAVED_GMT = "SAVED_GMT";
    private static final String KEY_SAVED_PARAM_TIME = "SAVED_PARAM_TIME";
    private static final String KEY_SAVED_PARAM_RAW = "SAVED_PARAM_RAW";
    private static final String KEY_RELAY_STATE = "SAVED_RELAY_STATE";

    // Keys Status Operasional
    private static final String KEY_SAVED_BATTERY = "SAVED_BATTERY";
    private static final String KEY_SAVED_ACC = "SAVED_ACC";
    private static final String KEY_SAVED_GPRS = "SAVED_GPRS";
    private static final String KEY_SAVED_GPS_FIX = "SAVED_GPS_FIX";
    private static final String KEY_SAVED_STATUS_DEFENSE = "SAVED_STATUS_DEFENSE";
    private static final String KEY_SAVED_STATUS_GSM = "SAVED_STATUS_GSM";
    private static final String KEY_SAVED_STATUS_TIME = "SAVED_STATUS_TIME";
    private static final String KEY_SAVED_STATUS_RAW = "SAVED_STATUS_RAW";

    // Keys Status & Konfigurasi GPRS (GPRSSET#)
    private static final String KEY_SAVED_GPRS_MODE = "SAVED_GPRS_MODE";
    private static final String KEY_SAVED_GPRS_APN = "SAVED_GPRS_APN";
    private static final String KEY_SAVED_GPRS_SERVER = "SAVED_GPRS_SERVER";
    private static final String KEY_SAVED_GPRS_PORT = "SAVED_GPRS_PORT";
    private static final String KEY_SAVED_GPRS_PROTOCOL = "SAVED_GPRS_PROTOCOL";
    private static final String KEY_SAVED_GPRS_URL = "SAVED_GPRS_URL";
    private static final String KEY_SAVED_GPRS_TIME = "SAVED_GPRS_TIME";
    private static final String KEY_SAVED_GPRS_RAW = "SAVED_GPRS_RAW";

    // Keys Versi Firmware (VERSION#)
    private static final String KEY_SAVED_FW_FULL = "SAVED_FW_FULL";
    private static final String KEY_SAVED_FW_VER = "SAVED_FW_VER";
    private static final String KEY_SAVED_FW_HW = "SAVED_FW_HW";
    private static final String KEY_SAVED_FW_DATE = "SAVED_FW_DATE";
    private static final String KEY_SAVED_FW_MODEL = "SAVED_FW_MODEL";
    private static final String KEY_SAVED_FW_TIME = "SAVED_FW_TIME";
    private static final String KEY_SAVED_FW_RAW = "SAVED_FW_RAW";

    // Keys Pengaturan Master, SOS & Kecepatan
    private static final String KEY_MASTER_NUMBER = "SETTINGS_MASTER_NUMBER";
    private static final String KEY_MASTER_NAME = "SETTINGS_MASTER_NAME";
    private static final String KEY_SOS_1 = "SETTINGS_SOS_1";
    private static final String KEY_SOS_2 = "SETTINGS_SOS_2";
    private static final String KEY_SOS_3 = "SETTINGS_SOS_3";
    private static final String KEY_SOS_NAME_1 = "SETTINGS_SOS_NAME_1";
    private static final String KEY_SOS_NAME_2 = "SETTINGS_SOS_NAME_2";
    private static final String KEY_SOS_NAME_3 = "SETTINGS_SOS_NAME_3";
    private static final String KEY_SPEED_ALARM_ENABLED = "SETTINGS_SPEED_ENABLED";
    private static final String KEY_SPEED_LIMIT = "SETTINGS_SPEED_LIMIT";
    private static final String KEY_SPEED_DURATION = "SETTINGS_SPEED_DURATION";
    private static final String KEY_SPEED_MODE = "SETTINGS_SPEED_MODE";

    // Keys Status Update Lokasi & Speed
    private static final String KEY_MAPS_LAST_UPDATE = "KEY_MAPS_LAST_UPDATE";
    private static final String KEY_COORDS_LAST_UPDATE = "KEY_COORDS_LAST_UPDATE";
    private static final String KEY_SAVED_SPEED = "KEY_SAVED_SPEED";

    // Keys Pengaturan Alarm Getaran (SENALM)
    private static final String KEY_VIBRATION_ALARM_ENABLED = "KEY_VIBRATION_ALARM_ENABLED";

    // Keys Kunci Akses Menu Pengaturan (Sidik Jari / Pola Sistem HP)
    private static final String KEY_LOCK_ENABLED = "SETTINGS_LOCK_ENABLED";
    private static final int REQUEST_CODE_DEVICE_CREDENTIAL = 2006;

    private static final int PERMISSION_REQUEST_SEND_SMS = 1001;
    private static final int PERMISSION_REQUEST_RECEIVE_SMS = 1002;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 1003;

    private static final int REQUEST_PICK_CONTACT = 2001;
    private static final int REQUEST_PICK_MASTER = 2002;
    private static final int REQUEST_PICK_SOS_1 = 2003;
    private static final int REQUEST_PICK_SOS_2 = 2004;
    private static final int REQUEST_PICK_SOS_3 = 2005;

    private int pendingContactRequestCode = REQUEST_PICK_CONTACT;
    private static final long SMS_TIMEOUT_MS = 25000; // 25 detik timeout balasan

    // Navigation & Page Containers (Home & Pengaturan)
    private ScrollView layoutHomeContainer;
    private ScrollView layoutSettingsContainer;
    private LinearLayout navTabHome;
    private LinearLayout navTabSettings;
    private TextView navLabelHome;
    private TextView navLabelSettings;

    // Halaman Pengaturan (Mandiri / Bukan Pop-up)
    private EditText etSettingsGpsNumber;
    private Button btnPickContact;
    private CheckBox cbSettingsDirectSms;

    // Tombol Sinkronisasi PARAM# dari Tracker di Pengaturan
    private Button btnFetchParamFromTracker;
    private TextView btnFetchParamMaster;
    private TextView btnFetchParamSos;

    // Pengaturan Nomor Master
    private EditText etSettingsMasterNumber;
    private TextView tvMasterContactName;
    private Button btnPickMasterContact;
    private Button btnSetMasterNumber;
    private Button btnDeleteMasterNumber;
    private TextView tvSettingsParamMaster;
    private TextView btnCopyParamMaster;

    // Pengaturan SOS (1, 2, 3)
    private EditText etSettingsSos1, etSettingsSos2, etSettingsSos3;
    private TextView tvSosName1, tvSosName2, tvSosName3;
    private Button btnPickSos1, btnPickSos2, btnPickSos3;
    private Button btnDeleteSos1, btnDeleteSos2, btnDeleteSos3;
    private Button btnSaveAndSendSos;
    private TextView tvSettingsParamSos;
    private TextView btnCopyParamSos;

    // Pengaturan Pembatasan Kecepatan
    private Switch switchSpeedAlarm;
    private TextView tvSpeedAlarmStatusBadge;
    private LinearLayout layoutSpeedSettingsDetail;
    private EditText etSpeedLimit;
    private EditText etSpeedDuration;
    private Spinner spinnerSpeedMode;
    private Button btnApplySpeedAlarm;
    private Button btnTurnOffSpeedAlarm;

    // Pengaturan Alarm Getaran (SENALM)
    private Switch switchVibrationAlarm;
    private TextView tvVibrationAlarmStatusBadge;

    private Button btnSaveSettings;
    private Button btnSettingsBackToHome;

    // Kunci Keamanan Menu Pengaturan (Sidik Jari / Pola Sistem HP)
    private Switch switchSettingsLock;
    private TextView tvSettingsLockStatusBadge;

    // Banner Target GPS Aktif (btnOpenSettings & tvQuickEditSettings telah dihapus)
    private TextView tvActiveTargetInfo;

    // Kategori 1: Kontrol Mesin (Tombol Tunggal Tergabung)
    private ImageButton ibCombinedEngine;
    private TextView tvCombinedEngineLabel;
    private TextView tvCombinedEngineSub;
    private TextView tvRelayStatusBadge;
    private TextView tvSyncRelayState;

    // Kategori 2: Pelacakan & Lokasi
    private Button btnCheckGoogleMaps;
    private TextView tvMapsLastUpdate;
    private Button btnCheckCoords;
    private TextView tvCoordsSpeed;
    private TextView tvCoordsLastUpdate;
    private WebView wvMap;
    private TextView tvMapCoords;
    private TextView tvMapSpeed;
    private TextView tvMapTime;
    private Button btnOpenExternalMap;

    // Kategori 3: Konfigurasi Parameter (Terpisah)
    private Button btnCheckParam;
    private TextView tvParamLastUpdate;
    private TextView tvParamImei;
    private TextView tvParamCenter;
    private TextView tvParamTimer;
    private TextView tvParamSends;
    private TextView tvParamSos;
    private TextView tvParamDefense;
    private TextView tvParamSensor;
    private TextView tvParamGmt;
    private TextView tvParamRawSms;

    // Kategori 4: Status Operasional Kendaraan (Terpisah)
    private Button btnCheckStatus;
    private TextView tvStatusLastUpdate;
    private TextView tvStatusBattery;
    private TextView tvStatusAcc;
    private TextView tvStatusGprs;
    private TextView tvStatusGps;
    private TextView tvStatusDefense;
    private TextView tvStatusGsm;
    private TextView tvStatusRawSms;

    // Kategori 5: Status & Konfigurasi GPRS (Baru)
    private Button btnCheckGprsConfig;
    private TextView tvGprsLastUpdate;
    private TextView tvGprsStatusMode;
    private TextView tvGprsApn;
    private TextView tvGprsServer;
    private TextView tvGprsPort;
    private TextView tvGprsProtocol;
    private TextView tvGprsUrl;
    private TextView tvGprsRawSms;

    // Kategori 6: Versi Firmware Perangkat (Baru)
    private Button btnCheckFirmware;
    private TextView tvFirmwareLastUpdate;
    private TextView tvFirmwareFullBuild;
    private TextView tvFirmwareVersion;
    private TextView tvHardwareVersion;
    private TextView tvFirmwareRelease;
    private TextView tvFirmwareModel;
    private TextView tvFirmwareRawSms;

    // Stack Diagnostik & Informasi (Kategori 3, 4, 5, 6 Swipeable)
    private HorizontalScrollView hsvDiagnosticStack;
    private Button btnStackPrev, btnStackNext;
    private TextView tvStackPageIndicator;
    private Button tabStackParam, tabStackStatus, tabStackGprs, tabStackFirmware;
    private View cardStackParam, cardStackStatus, cardStackGprs, cardStackFirmware;
    private int currentStackPage = 0;

    // Kategori 7: Pemeliharaan (Tombol Restart Icon Baru)
    private ImageButton ibRestartDevice;

    // Log & Demo
    private TextView btnSimulateSms;
    private TextView btnClearLog;
    private TextView tvCommandLog;
    private TextView tvSmsFilterNotice;

    private SharedPreferences preferences;
    private String pendingCommand = null;
    private String lastSentCommand = "";

    private boolean isEngineCutOff = false;
    private double currentLat = -6.208800; // Default: Jakarta
    private double currentLon = 106.845600;

    // Watchdog Timer untuk Memantau Respon SMS dari Tracker
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private boolean isAwaitingResponse = false;
    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAwaitingResponse) {
                isAwaitingResponse = false;
                showNoResponseWarningDialog();
            }
        }
    };

    private boolean isAllowedSender(String sender, String targetNumber) {
        if ("GPS-GT06".equals(sender) || "SIMULASI".equals(sender)) {
            return true;
        }
        return GpsSmsReceiver.isMatchingSender(sender, targetNumber);
    }

    // Broadcast receiver untuk menangkap SMS dari GpsSmsReceiver
    private final BroadcastReceiver smsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && GpsSmsReceiver.ACTION_GPS_SMS.equals(intent.getAction())) {
                String sender = intent.getStringExtra(GpsSmsReceiver.EXTRA_SENDER);
                String message = intent.getStringExtra(GpsSmsReceiver.EXTRA_MESSAGE);
                if (message != null) {
                    String targetGpsNumber = preferences.getString(KEY_PHONE_NUMBER, "").trim();
                    if (!targetGpsNumber.isEmpty() && !isAllowedSender(sender, targetGpsNumber)) {
                        // Abaikan SMS dari pengirim selain SIM Tracker GPS
                        return;
                    }
                    handleIncomingTrackerSms(sender, message);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        GpsNotificationHelper.initChannel(this);

        initViews();
        setupWebView();
        loadSavedData();
        setupListeners();
        checkPermissions();
        handleWidgetIntent(getIntent());
        handleAlarmIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleWidgetIntent(intent);
        handleAlarmIntent(intent);
    }

    private void handleAlarmIntent(Intent intent) {
        if (intent != null && intent.hasExtra("extra_alarm_type")) {
            String msg = intent.getStringExtra("extra_alarm_msg");
            GpsNotificationHelper.AlarmInfo info = GpsNotificationHelper.detectAlarm(msg);
            if (info != null) {
                showAlarmAlertDialog(info, msg != null ? msg : info.description);
            }
        }
    }

    private void handleWidgetIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra(EngineControlWidget.EXTRA_PROMPT_STOP_ENGINE, false)) {
            showConfirmationDialog(
                    getString(R.string.dialog_stop_title),
                    getString(R.string.dialog_stop_message),
                    "RELAY,1#",
                    "Matikan Mesin"
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(GpsSmsReceiver.ACTION_GPS_SMS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsBroadcastReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(smsBroadcastReceiver, filter);
        }
        updateActiveTargetBanner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(smsBroadcastReceiver);
        } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacksAndMessages(null);
    }

    private void initViews() {
        tvActiveTargetInfo = findViewById(R.id.tvActiveTargetInfo);

        ibCombinedEngine = findViewById(R.id.ibCombinedEngine);
        tvCombinedEngineLabel = findViewById(R.id.tvCombinedEngineLabel);
        tvCombinedEngineSub = findViewById(R.id.tvCombinedEngineSub);
        tvRelayStatusBadge = findViewById(R.id.tvRelayStatusBadge);
        tvSyncRelayState = findViewById(R.id.tvSyncRelayState);

        btnCheckGoogleMaps = findViewById(R.id.btnCheckGoogleMaps);
        tvMapsLastUpdate = findViewById(R.id.tvMapsLastUpdate);
        btnCheckCoords = findViewById(R.id.btnCheckCoords);
        tvCoordsSpeed = findViewById(R.id.tvCoordsSpeed);
        tvCoordsLastUpdate = findViewById(R.id.tvCoordsLastUpdate);
        wvMap = findViewById(R.id.wvMap);
        tvMapCoords = findViewById(R.id.tvMapCoords);
        tvMapSpeed = findViewById(R.id.tvMapSpeed);
        tvMapTime = findViewById(R.id.tvMapTime);
        btnOpenExternalMap = findViewById(R.id.btnOpenExternalMap);

        // Kategori 3: Parameter
        btnCheckParam = findViewById(R.id.btnCheckParam);
        tvParamLastUpdate = findViewById(R.id.tvParamLastUpdate);
        tvParamImei = findViewById(R.id.tvParamImei);
        tvParamCenter = findViewById(R.id.tvParamCenter);
        tvParamTimer = findViewById(R.id.tvParamTimer);
        tvParamSends = findViewById(R.id.tvParamSends);
        tvParamSos = findViewById(R.id.tvParamSos);
        tvParamDefense = findViewById(R.id.tvParamDefense);
        tvParamSensor = findViewById(R.id.tvParamSensor);
        tvParamGmt = findViewById(R.id.tvParamGmt);
        tvParamRawSms = findViewById(R.id.tvParamRawSms);

        // Kategori 4: Status Operasional
        btnCheckStatus = findViewById(R.id.btnCheckStatus);
        tvStatusLastUpdate = findViewById(R.id.tvStatusLastUpdate);
        tvStatusBattery = findViewById(R.id.tvStatusBattery);
        tvStatusAcc = findViewById(R.id.tvStatusAcc);
        tvStatusGprs = findViewById(R.id.tvStatusGprs);
        tvStatusGps = findViewById(R.id.tvStatusGps);
        tvStatusDefense = findViewById(R.id.tvStatusDefense);
        tvStatusGsm = findViewById(R.id.tvStatusGsm);
        tvStatusRawSms = findViewById(R.id.tvStatusRawSms);

        // Kategori 5: Status & Konfigurasi GPRS (Baru)
        btnCheckGprsConfig = findViewById(R.id.btnCheckGprsConfig);
        tvGprsLastUpdate = findViewById(R.id.tvGprsLastUpdate);
        tvGprsStatusMode = findViewById(R.id.tvGprsStatusMode);
        tvGprsApn = findViewById(R.id.tvGprsApn);
        tvGprsServer = findViewById(R.id.tvGprsServer);
        tvGprsPort = findViewById(R.id.tvGprsPort);
        tvGprsProtocol = findViewById(R.id.tvGprsProtocol);
        tvGprsUrl = findViewById(R.id.tvGprsUrl);
        tvGprsRawSms = findViewById(R.id.tvGprsRawSms);

        // Kategori 6: Versi Firmware Perangkat (Baru)
        btnCheckFirmware = findViewById(R.id.btnCheckFirmware);
        tvFirmwareLastUpdate = findViewById(R.id.tvFirmwareLastUpdate);
        tvFirmwareFullBuild = findViewById(R.id.tvFirmwareFullBuild);
        tvFirmwareVersion = findViewById(R.id.tvFirmwareVersion);
        tvHardwareVersion = findViewById(R.id.tvHardwareVersion);
        tvFirmwareRelease = findViewById(R.id.tvFirmwareRelease);
        tvFirmwareModel = findViewById(R.id.tvFirmwareModel);
        tvFirmwareRawSms = findViewById(R.id.tvFirmwareRawSms);

        // Stack Diagnostik & Informasi (Kategori 3, 4, 5, 6 Swipeable)
        hsvDiagnosticStack = findViewById(R.id.hsvDiagnosticStack);
        btnStackPrev = findViewById(R.id.btnStackPrev);
        btnStackNext = findViewById(R.id.btnStackNext);
        tvStackPageIndicator = findViewById(R.id.tvStackPageIndicator);

        tabStackParam = findViewById(R.id.tabStackParam);
        tabStackStatus = findViewById(R.id.tabStackStatus);
        tabStackGprs = findViewById(R.id.tabStackGprs);
        tabStackFirmware = findViewById(R.id.tabStackFirmware);

        cardStackParam = findViewById(R.id.cardStackParam);
        cardStackStatus = findViewById(R.id.cardStackStatus);
        cardStackGprs = findViewById(R.id.cardStackGprs);
        cardStackFirmware = findViewById(R.id.cardStackFirmware);

        // Kategori 7: Restart ImageButton
        ibRestartDevice = findViewById(R.id.ibRestartDevice);

        btnSimulateSms = findViewById(R.id.btnSimulateSms);
        btnClearLog = findViewById(R.id.btnClearLog);
        tvCommandLog = findViewById(R.id.tvCommandLog);
        tvSmsFilterNotice = findViewById(R.id.tvSmsFilterNotice);

        // Navigasi & Container Halaman (Home & Pengaturan)
        layoutHomeContainer = findViewById(R.id.layoutHomeContainer);
        layoutSettingsContainer = findViewById(R.id.layoutSettingsContainer);
        navTabHome = findViewById(R.id.navTabHome);
        navTabSettings = findViewById(R.id.navTabSettings);
        navLabelHome = findViewById(R.id.navLabelHome);
        navLabelSettings = findViewById(R.id.navLabelSettings);

        // Halaman Pengaturan (Mandiri / Bukan Pop-up)
        etSettingsGpsNumber = findViewById(R.id.etSettingsGpsNumber);
        btnPickContact = findViewById(R.id.btnPickContact);
        cbSettingsDirectSms = findViewById(R.id.cbSettingsDirectSms);

        // Tombol Sinkronisasi PARAM# dari Tracker
        btnFetchParamFromTracker = findViewById(R.id.btnFetchParamFromTracker);
        btnFetchParamMaster = findViewById(R.id.btnFetchParamMaster);
        btnFetchParamSos = findViewById(R.id.btnFetchParamSos);

        // Pengaturan Nomor Master
        etSettingsMasterNumber = findViewById(R.id.etSettingsMasterNumber);
        tvMasterContactName = findViewById(R.id.tvMasterContactName);
        btnPickMasterContact = findViewById(R.id.btnPickMasterContact);
        btnSetMasterNumber = findViewById(R.id.btnSetMasterNumber);
        btnDeleteMasterNumber = findViewById(R.id.btnDeleteMasterNumber);
        tvSettingsParamMaster = findViewById(R.id.tvSettingsParamMaster);
        btnCopyParamMaster = findViewById(R.id.btnCopyParamMaster);

        // Pengaturan SOS (1, 2, 3)
        etSettingsSos1 = findViewById(R.id.etSettingsSos1);
        etSettingsSos2 = findViewById(R.id.etSettingsSos2);
        etSettingsSos3 = findViewById(R.id.etSettingsSos3);
        tvSosName1 = findViewById(R.id.tvSosName1);
        tvSosName2 = findViewById(R.id.tvSosName2);
        tvSosName3 = findViewById(R.id.tvSosName3);
        btnPickSos1 = findViewById(R.id.btnPickSos1);
        btnPickSos2 = findViewById(R.id.btnPickSos2);
        btnPickSos3 = findViewById(R.id.btnPickSos3);
        btnDeleteSos1 = findViewById(R.id.btnDeleteSos1);
        btnDeleteSos2 = findViewById(R.id.btnDeleteSos2);
        btnDeleteSos3 = findViewById(R.id.btnDeleteSos3);
        btnSaveAndSendSos = findViewById(R.id.btnSaveAndSendSos);
        tvSettingsParamSos = findViewById(R.id.tvSettingsParamSos);
        btnCopyParamSos = findViewById(R.id.btnCopyParamSos);

        // Pengaturan Pembatasan Kecepatan
        switchSpeedAlarm = findViewById(R.id.switchSpeedAlarm);
        tvSpeedAlarmStatusBadge = findViewById(R.id.tvSpeedAlarmStatusBadge);
        layoutSpeedSettingsDetail = findViewById(R.id.layoutSpeedSettingsDetail);
        etSpeedLimit = findViewById(R.id.etSpeedLimit);
        etSpeedDuration = findViewById(R.id.etSpeedDuration);
        spinnerSpeedMode = findViewById(R.id.spinnerSpeedMode);
        btnApplySpeedAlarm = findViewById(R.id.btnApplySpeedAlarm);
        btnTurnOffSpeedAlarm = findViewById(R.id.btnTurnOffSpeedAlarm);

        // Pengaturan Alarm Getaran (SENALM)
        switchVibrationAlarm = findViewById(R.id.switchVibrationAlarm);
        tvVibrationAlarmStatusBadge = findViewById(R.id.tvVibrationAlarmStatusBadge);

        // Kunci Keamanan Menu Pengaturan (Sidik Jari / Pola Sistem HP)
        switchSettingsLock = findViewById(R.id.switchSettingsLock);
        tvSettingsLockStatusBadge = findViewById(R.id.tvSettingsLockStatusBadge);

        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnSettingsBackToHome = findViewById(R.id.btnSettingsBackToHome);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = wvMap.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        wvMap.setWebViewClient(new WebViewClient());
    }

    private void loadSavedData() {
        updateActiveTargetBanner();

        // Load saved coordinates
        currentLat = Double.longBitsToDouble(preferences.getLong(KEY_SAVED_LAT, Double.doubleToLongBits(-6.208800)));
        currentLon = Double.longBitsToDouble(preferences.getLong(KEY_SAVED_LON, Double.doubleToLongBits(106.845600)));
        renderMap(currentLat, currentLon, "Posisi Terakhir Disimpan");

        // Load saved parameters
        tvParamImei.setText(preferences.getString(KEY_SAVED_IMEI, "-"));
        tvParamCenter.setText(preferences.getString(KEY_SAVED_CENTER, "-"));
        tvParamTimer.setText(preferences.getString(KEY_SAVED_TIMER, "-"));
        tvParamSends.setText(preferences.getString(KEY_SAVED_PARAM_SENDS, "-"));
        tvParamSos.setText(preferences.getString(KEY_SAVED_SOS, "-"));
        tvParamDefense.setText(preferences.getString(KEY_SAVED_PARAM_DEFENSE, "-"));
        tvParamSensor.setText(preferences.getString(KEY_SAVED_SENSOR, "-"));
        tvParamGmt.setText(preferences.getString(KEY_SAVED_GMT, "-"));
        tvParamLastUpdate.setText(preferences.getString(KEY_SAVED_PARAM_TIME, "Belum dicek"));
        tvParamRawSms.setText(preferences.getString(KEY_SAVED_PARAM_RAW, "Belum ada balasan SMS diterima"));

        // Load saved operational status
        tvStatusBattery.setText(preferences.getString(KEY_SAVED_BATTERY, "-"));
        tvStatusAcc.setText(preferences.getString(KEY_SAVED_ACC, "-"));
        tvStatusGprs.setText(preferences.getString(KEY_SAVED_GPRS, "-"));
        tvStatusGps.setText(preferences.getString(KEY_SAVED_GPS_FIX, "-"));
        tvStatusDefense.setText(preferences.getString(KEY_SAVED_STATUS_DEFENSE, "-"));
        tvStatusGsm.setText(preferences.getString(KEY_SAVED_STATUS_GSM, "-"));
        tvStatusLastUpdate.setText(preferences.getString(KEY_SAVED_STATUS_TIME, "Belum dicek"));
        tvStatusRawSms.setText(preferences.getString(KEY_SAVED_STATUS_RAW, "Belum ada balasan SMS diterima"));

        // Load saved GPRS config
        tvGprsStatusMode.setText(preferences.getString(KEY_SAVED_GPRS_MODE, "-"));
        tvGprsApn.setText(preferences.getString(KEY_SAVED_GPRS_APN, "-"));
        tvGprsServer.setText(preferences.getString(KEY_SAVED_GPRS_SERVER, "-"));
        tvGprsPort.setText(preferences.getString(KEY_SAVED_GPRS_PORT, "-"));
        tvGprsProtocol.setText(preferences.getString(KEY_SAVED_GPRS_PROTOCOL, "-"));
        tvGprsUrl.setText(preferences.getString(KEY_SAVED_GPRS_URL, "-"));
        tvGprsLastUpdate.setText(preferences.getString(KEY_SAVED_GPRS_TIME, "Belum dicek"));
        tvGprsRawSms.setText(preferences.getString(KEY_SAVED_GPRS_RAW, "Belum ada balasan SMS diterima"));

        // Load saved Firmware info
        if (tvFirmwareFullBuild != null) {
            tvFirmwareFullBuild.setText(preferences.getString(KEY_SAVED_FW_FULL, "-"));
        }
        tvFirmwareVersion.setText(preferences.getString(KEY_SAVED_FW_VER, "-"));
        tvHardwareVersion.setText(preferences.getString(KEY_SAVED_FW_HW, "-"));
        tvFirmwareRelease.setText(preferences.getString(KEY_SAVED_FW_DATE, "-"));
        tvFirmwareModel.setText(preferences.getString(KEY_SAVED_FW_MODEL, "-"));
        tvFirmwareLastUpdate.setText(preferences.getString(KEY_SAVED_FW_TIME, "Belum dicek"));
        tvFirmwareRawSms.setText(preferences.getString(KEY_SAVED_FW_RAW, "Belum ada balasan SMS diterima"));

        // Inisialisasi awal dengan data balasan tracker jika belum pernah dicek
        if (!preferences.contains(KEY_SAVED_FW_FULL) || preferences.getString(KEY_SAVED_FW_RAW, "").contains("Belum")) {
            parseFirmwareFromSms("[VERSION]NT36_11_A1D_D23_R0_V17_c_WM", "07:55:11");
        }

        // Load Relay State (false = mesin menyala, true = mesin mati)
        isEngineCutOff = preferences.getBoolean(KEY_RELAY_STATE, false);
        updateCombinedEngineUI(isEngineCutOff);

        // Load saved Maps & Coords status (Tanggal, Jam, dan Speed)
        String savedMapsTime = preferences.getString(KEY_MAPS_LAST_UPDATE, "Belum ada data");
        if (tvMapsLastUpdate != null) tvMapsLastUpdate.setText("Update: " + savedMapsTime);
        String savedCoordsTime = preferences.getString(KEY_COORDS_LAST_UPDATE, "Belum ada data");
        if (tvCoordsLastUpdate != null) tvCoordsLastUpdate.setText("Update: " + savedCoordsTime);
        String savedSpeed = preferences.getString(KEY_SAVED_SPEED, "- Km/h");
        if (tvCoordsSpeed != null) tvCoordsSpeed.setText("⚡ Speed: " + savedSpeed);
        if (tvMapSpeed != null) tvMapSpeed.setText("⚡ Speed: " + savedSpeed);
        if (tvMapTime != null && !"Belum ada data".equals(savedCoordsTime)) {
            tvMapTime.setText("Update: " + savedCoordsTime);
        }
    }

    private void updateActiveTargetBanner() {
        String number = preferences.getString(KEY_PHONE_NUMBER, "");
        boolean directSms = preferences.getBoolean(KEY_DIRECT_SMS, true);

        if (TextUtils.isEmpty(number)) {
            tvActiveTargetInfo.setText("📱 Tracker: Belum Diatur");
            tvActiveTargetInfo.setTextColor(0xFFFCA5A5);
            if (tvSmsFilterNotice != null) {
                tvSmsFilterNotice.setText("⚠️ Nomor kartu SIM GPS belum diatur di Pengaturan");
                tvSmsFilterNotice.setTextColor(0xFFEF4444);
            }
        } else {
            tvActiveTargetInfo.setText("📱 Tracker: " + number + "  •  " + (directSms ? "SMS Langsung" : "Aplikasi SMS"));
            tvActiveTargetInfo.setTextColor(0xFFCBD5E1);
            if (tvSmsFilterNotice != null) {
                tvSmsFilterNotice.setText("🔒 Filter Aktif: Hanya menerima balasan dari " + number);
                tvSmsFilterNotice.setTextColor(0xFF059669);
            }
        }
    }

    private void updateSettingsParamDisplay() {
        // 1. Tampilkan Master dari PARAM#
        String savedMasterParam = preferences.getString(KEY_SAVED_CENTER, "").trim();
        if (tvSettingsParamMaster != null) {
            if (!TextUtils.isEmpty(savedMasterParam) && !"-".equals(savedMasterParam)) {
                tvSettingsParamMaster.setText("👑 " + savedMasterParam);
                tvSettingsParamMaster.setTextColor(0xFF0F172A);
                if (btnCopyParamMaster != null) btnCopyParamMaster.setVisibility(View.VISIBLE);
            } else {
                tvSettingsParamMaster.setText("Belum ada data (Kirim 'Cek Parameter' di Beranda)");
                tvSettingsParamMaster.setTextColor(0xFF94A3B8);
                if (btnCopyParamMaster != null) btnCopyParamMaster.setVisibility(View.GONE);
            }
        }

        // 2. Tampilkan SOS dari PARAM#
        String savedSosParam = preferences.getString(KEY_SAVED_SOS, "").trim();
        if (tvSettingsParamSos != null) {
            if (!TextUtils.isEmpty(savedSosParam) && !"-".equals(savedSosParam)) {
                String[] sosParts = savedSosParam.split("[,;]");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < sosParts.length && i < 3; i++) {
                    String p = sosParts[i].trim();
                    if (!p.isEmpty()) {
                        if (sb.length() > 0) sb.append("\n");
                        sb.append("Slot ").append(i + 1).append(": ").append(p);
                    }
                }
                if (sb.length() > 0) {
                    tvSettingsParamSos.setText(sb.toString());
                } else {
                    tvSettingsParamSos.setText("SOS: " + savedSosParam);
                }
                tvSettingsParamSos.setTextColor(0xFF0F172A);
                if (btnCopyParamSos != null) btnCopyParamSos.setVisibility(View.VISIBLE);
            } else {
                tvSettingsParamSos.setText("Belum ada data (Kirim 'Cek Parameter' di Beranda)");
                tvSettingsParamSos.setTextColor(0xFF94A3B8);
                if (btnCopyParamSos != null) btnCopyParamSos.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Verifikasi Kunci Akses (Sidik Jari / Pola Sistem HP) sebelum membuka Menu Pengaturan
     */
    private void attemptOpenSettings() {
        if (layoutSettingsContainer != null && layoutSettingsContainer.getVisibility() == View.VISIBLE) {
            return;
        }

        boolean lockEnabled = preferences.getBoolean(KEY_LOCK_ENABLED, false);
        if (!lockEnabled) {
            showPage(1);
            return;
        }

        authenticateWithSystemSecurity();
    }

    private void authenticateWithSystemSecurity() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km == null || !km.isKeyguardSecure()) {
            Toast.makeText(this, "Perangkat belum mengaktifkan Kunci Layar (Pola/PIN/Sidik Jari). Akses diberikan.", Toast.LENGTH_LONG).show();
            showPage(1);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            BiometricPrompt.Builder builder = new BiometricPrompt.Builder(this)
                    .setTitle("🔐 Autentikasi Menu Pengaturan")
                    .setSubtitle("Gunakan Sidik Jari atau Pola/PIN Sistem HP Anda");

            if (Build.VERSION.SDK_INT >= 30) {
                builder.setAllowedAuthenticators(
                        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK |
                        android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
                );
            } else {
                builder.setDeviceCredentialAllowed(true);
            }

            BiometricPrompt prompt = builder.build();
            CancellationSignal cancellationSignal = new CancellationSignal();
            java.util.concurrent.Executor executor = command -> new Handler(Looper.getMainLooper()).post(command);

            prompt.authenticate(cancellationSignal, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(MainActivity.this, "🔓 Verifikasi Berhasil", Toast.LENGTH_SHORT).show();
                    showPage(1);
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.BIOMETRIC_ERROR_CANCELED) {
                        Toast.makeText(MainActivity.this, "🔒 Verifikasi dibatalkan", Toast.LENGTH_SHORT).show();
                    } else {
                        fallbackToSystemKeyguard();
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(MainActivity.this, "Sidik jari tidak cocok, coba lagi", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fallbackToSystemKeyguard();
        }
    }

    private void fallbackToSystemKeyguard() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km != null && km.isKeyguardSecure()) {
            Intent intent = km.createConfirmDeviceCredentialIntent(
                    "🔐 Autentikasi Pengaturan",
                    "Gunakan Sidik Jari atau Pola/PIN sistem HP Anda"
            );
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_DEVICE_CREDENTIAL);
                return;
            }
        }
        showPage(1);
    }

    /**
     * Berpindah antar Halaman: Beranda (0) dan Pengaturan Mandiri (1)
     */
    private void showPage(int pageIndex) {
        if (pageIndex == 0) {
            // Halaman Home / Beranda
            layoutHomeContainer.setVisibility(View.VISIBLE);
            layoutSettingsContainer.setVisibility(View.GONE);

            navLabelHome.setTextColor(0xFF0284C7);
            navLabelHome.setTypeface(null, Typeface.BOLD);
            navLabelSettings.setTextColor(0xFF94A3B8);
            navLabelSettings.setTypeface(null, Typeface.NORMAL);
        } else {
            // Halaman Pengaturan (Mandiri / Bukan Pop-up)
            layoutHomeContainer.setVisibility(View.GONE);
            layoutSettingsContainer.setVisibility(View.VISIBLE);

            navLabelSettings.setTextColor(0xFF0284C7);
            navLabelSettings.setTypeface(null, Typeface.BOLD);
            navLabelHome.setTextColor(0xFF94A3B8);
            navLabelHome.setTypeface(null, Typeface.NORMAL);

            // Tampilkan status Master & SOS terdaftar dari PARAM#
            updateSettingsParamDisplay();

            // 1. Muat Target SIM Card GPS
            String currentNumber = preferences.getString(KEY_PHONE_NUMBER, "");
            if (TextUtils.isEmpty(currentNumber)) {
                currentNumber = "085117007880";
            }
            etSettingsGpsNumber.setText(currentNumber);
            cbSettingsDirectSms.setChecked(preferences.getBoolean(KEY_DIRECT_SMS, true));

            // 2. Muat Nomor Master
            String masterNum = preferences.getString(KEY_MASTER_NUMBER, "");
            String savedCenterParam = preferences.getString(KEY_SAVED_CENTER, "").trim();
            if (TextUtils.isEmpty(masterNum) && !TextUtils.isEmpty(savedCenterParam) && !"-".equals(savedCenterParam)) {
                masterNum = savedCenterParam;
                preferences.edit().putString(KEY_MASTER_NUMBER, masterNum).apply();
            }
            String masterName = preferences.getString(KEY_MASTER_NAME, "");
            etSettingsMasterNumber.setText(masterNum);
            tvMasterContactName.setText(!TextUtils.isEmpty(masterName) ? "👤 Kontak: " + masterName : "👤 Kontak: Belum dipilih");

            // 3. Muat Nomor SOS 1, 2, 3
            String sos1 = preferences.getString(KEY_SOS_1, "");
            String sos2 = preferences.getString(KEY_SOS_2, "");
            String sos3 = preferences.getString(KEY_SOS_3, "");
            String savedSosParam = preferences.getString(KEY_SAVED_SOS, "").trim();
            if (TextUtils.isEmpty(sos1) && TextUtils.isEmpty(sos2) && TextUtils.isEmpty(sos3) &&
                    !TextUtils.isEmpty(savedSosParam) && !"-".equals(savedSosParam)) {
                String[] parts = savedSosParam.split("[,;]");
                if (parts.length > 0) sos1 = parts[0].trim();
                if (parts.length > 1) sos2 = parts[1].trim();
                if (parts.length > 2) sos3 = parts[2].trim();
                preferences.edit()
                        .putString(KEY_SOS_1, sos1)
                        .putString(KEY_SOS_2, sos2)
                        .putString(KEY_SOS_3, sos3)
                        .apply();
            }
            String sosName1 = preferences.getString(KEY_SOS_NAME_1, "");
            etSettingsSos1.setText(sos1);
            tvSosName1.setText(!TextUtils.isEmpty(sosName1) ? "👤 Kontak: " + sosName1 : "👤 Kontak: Belum dipilih");

            String sosName2 = preferences.getString(KEY_SOS_NAME_2, "");
            etSettingsSos2.setText(sos2);
            tvSosName2.setText(!TextUtils.isEmpty(sosName2) ? "👤 Kontak: " + sosName2 : "👤 Kontak: Belum dipilih");

            String sosName3 = preferences.getString(KEY_SOS_NAME_3, "");
            etSettingsSos3.setText(sos3);
            tvSosName3.setText(!TextUtils.isEmpty(sosName3) ? "👤 Kontak: " + sosName3 : "👤 Kontak: Belum dipilih");

            // 4. Muat Pengaturan Kecepatan
            boolean speedEnabled = preferences.getBoolean(KEY_SPEED_ALARM_ENABLED, false);
            switchSpeedAlarm.setChecked(speedEnabled);
            layoutSpeedSettingsDetail.setVisibility(speedEnabled ? View.VISIBLE : View.GONE);
            String savedSpeed = preferences.getString(KEY_SPEED_LIMIT, "80");
            tvSpeedAlarmStatusBadge.setText(speedEnabled ? "STATUS: ALARM AKTIF (" + savedSpeed + " km/h)" : "STATUS: ALARM NONAKTIF");
            tvSpeedAlarmStatusBadge.setTextColor(speedEnabled ? 0xFF16A34A : 0xFF94A3B8);

            etSpeedLimit.setText(savedSpeed);
            etSpeedDuration.setText(preferences.getString(KEY_SPEED_DURATION, "10"));
            if (spinnerSpeedMode != null) {
                spinnerSpeedMode.setSelection(preferences.getInt(KEY_SPEED_MODE, 1));
            }

            // 5. Muat Status Kunci Keamanan Pengaturan (Sistem HP)
            boolean lockEnabled = preferences.getBoolean(KEY_LOCK_ENABLED, false);
            if (switchSettingsLock != null) switchSettingsLock.setChecked(lockEnabled);
            if (tvSettingsLockStatusBadge != null) {
                tvSettingsLockStatusBadge.setText(lockEnabled ? "STATUS: KUNCI AKTIF (Sistem HP)" : "STATUS: KUNCI NONAKTIF");
                tvSettingsLockStatusBadge.setTextColor(lockEnabled ? 0xFF16A34A : 0xFF94A3B8);
            }

            // 6. Muat Status Alarm Getaran (SENALM)
            boolean vibEnabled = preferences.getBoolean(KEY_VIBRATION_ALARM_ENABLED, false);
            if (switchVibrationAlarm != null) switchVibrationAlarm.setChecked(vibEnabled);
            if (tvVibrationAlarmStatusBadge != null) {
                tvVibrationAlarmStatusBadge.setText(vibEnabled ? "STATUS: ALARM AKTIF (SENALM,ON)" : "STATUS: ALARM NONAKTIF");
                tvVibrationAlarmStatusBadge.setTextColor(vibEnabled ? 0xFF16A34A : 0xFF94A3B8);
            }

            // Sesuaikan lebar kartu Stack Diagnostik & Informasi yang kini ada di menu Pengaturan
            if (hsvDiagnosticStack != null) {
                hsvDiagnosticStack.post(() -> {
                    int width = hsvDiagnosticStack.getWidth();
                    if (width > 0) {
                        setCardWidth(cardStackParam, width);
                        setCardWidth(cardStackStatus, width);
                        setCardWidth(cardStackGprs, width);
                        setCardWidth(cardStackFirmware, width);
                    }
                });
            }
        }
    }

    private void saveSettingsFromPage() {
        String enteredNumber = etSettingsGpsNumber.getText().toString().trim();
        if (TextUtils.isEmpty(enteredNumber)) {
            Toast.makeText(MainActivity.this, "Nomor SIM Card GPS tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean directMode = cbSettingsDirectSms.isChecked();

        String masterNum = etSettingsMasterNumber.getText().toString().trim();
        String sos1 = etSettingsSos1.getText().toString().trim();
        String sos2 = etSettingsSos2.getText().toString().trim();
        String sos3 = etSettingsSos3.getText().toString().trim();

        String speedLimit = etSpeedLimit.getText().toString().trim();
        String speedDur = etSpeedDuration.getText().toString().trim();
        int speedMode = spinnerSpeedMode != null ? spinnerSpeedMode.getSelectedItemPosition() : 1;
        boolean speedEnabled = switchSpeedAlarm.isChecked();

        boolean lockEnabled = switchSettingsLock != null && switchSettingsLock.isChecked();
        boolean vibEnabled = switchVibrationAlarm != null && switchVibrationAlarm.isChecked();

        preferences.edit()
                .putString(KEY_PHONE_NUMBER, enteredNumber)
                .putBoolean(KEY_DIRECT_SMS, directMode)
                .putString(KEY_MASTER_NUMBER, masterNum)
                .putString(KEY_SOS_1, sos1)
                .putString(KEY_SOS_2, sos2)
                .putString(KEY_SOS_3, sos3)
                .putBoolean(KEY_SPEED_ALARM_ENABLED, speedEnabled)
                .putString(KEY_SPEED_LIMIT, TextUtils.isEmpty(speedLimit) ? "80" : speedLimit)
                .putString(KEY_SPEED_DURATION, TextUtils.isEmpty(speedDur) ? "10" : speedDur)
                .putInt(KEY_SPEED_MODE, speedMode >= 0 ? speedMode : 1)
                .putBoolean(KEY_LOCK_ENABLED, lockEnabled)
                .putBoolean(KEY_VIBRATION_ALARM_ENABLED, vibEnabled)
                .apply();

        updateActiveTargetBanner();
        Toast.makeText(MainActivity.this, "💾 Semua pengaturan berhasil disimpan!", Toast.LENGTH_SHORT).show();
        showPage(0); // Kembali ke Beranda
    }

    private void openContactPicker(int requestCode) {
        pendingContactRequestCode = requestCode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutSettingsContainer != null && layoutSettingsContainer.getVisibility() == View.VISIBLE) {
            showPage(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DEVICE_CREDENTIAL) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "🔓 Verifikasi Berhasil", Toast.LENGTH_SHORT).show();
                showPage(1);
            } else {
                Toast.makeText(this, "🔒 Verifikasi dibatalkan", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (resultCode == RESULT_OK && data != null &&
                (requestCode == REQUEST_PICK_CONTACT ||
                 requestCode == REQUEST_PICK_MASTER ||
                 requestCode == REQUEST_PICK_SOS_1 ||
                 requestCode == REQUEST_PICK_SOS_2 ||
                 requestCode == REQUEST_PICK_SOS_3)) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                };
                try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        if (numberIndex >= 0) {
                            String rawNumber = cursor.getString(numberIndex);
                            String name = nameIndex >= 0 ? cursor.getString(nameIndex) : "";
                            String cleanNumber = rawNumber.replaceAll("[^0-9+]", "");

                            if (requestCode == REQUEST_PICK_CONTACT) {
                                if (etSettingsGpsNumber != null) etSettingsGpsNumber.setText(cleanNumber);
                                Toast.makeText(this, "SIM Tracker dipilih: " + cleanNumber + (TextUtils.isEmpty(name) ? "" : " (" + name + ")"), Toast.LENGTH_SHORT).show();
                            } else if (requestCode == REQUEST_PICK_MASTER) {
                                if (etSettingsMasterNumber != null) etSettingsMasterNumber.setText(cleanNumber);
                                if (tvMasterContactName != null) tvMasterContactName.setText("👤 Kontak: " + name);
                                preferences.edit().putString(KEY_MASTER_NAME, name).apply();
                                Toast.makeText(this, "Master dipilih: " + cleanNumber + (TextUtils.isEmpty(name) ? "" : " (" + name + ")"), Toast.LENGTH_SHORT).show();
                            } else if (requestCode == REQUEST_PICK_SOS_1) {
                                if (etSettingsSos1 != null) etSettingsSos1.setText(cleanNumber);
                                if (tvSosName1 != null) tvSosName1.setText("👤 Kontak: " + name);
                                preferences.edit().putString(KEY_SOS_NAME_1, name).apply();
                                Toast.makeText(this, "SOS 1 dipilih: " + cleanNumber + (TextUtils.isEmpty(name) ? "" : " (" + name + ")"), Toast.LENGTH_SHORT).show();
                            } else if (requestCode == REQUEST_PICK_SOS_2) {
                                if (etSettingsSos2 != null) etSettingsSos2.setText(cleanNumber);
                                if (tvSosName2 != null) tvSosName2.setText("👤 Kontak: " + name);
                                preferences.edit().putString(KEY_SOS_NAME_2, name).apply();
                                Toast.makeText(this, "SOS 2 dipilih: " + cleanNumber + (TextUtils.isEmpty(name) ? "" : " (" + name + ")"), Toast.LENGTH_SHORT).show();
                            } else if (requestCode == REQUEST_PICK_SOS_3) {
                                if (etSettingsSos3 != null) etSettingsSos3.setText(cleanNumber);
                                if (tvSosName3 != null) tvSosName3.setText("👤 Kontak: " + name);
                                preferences.edit().putString(KEY_SOS_NAME_3, name).apply();
                                Toast.makeText(this, "SOS 3 dipilih: " + cleanNumber + (TextUtils.isEmpty(name) ? "" : " (" + name + ")"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Gagal mengambil kontak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Memperbarui tampilan tombol gabungan kontrol mesin
     */
    private void updateCombinedEngineUI(boolean isCutOff) {
        this.isEngineCutOff = isCutOff;
        preferences.edit().putBoolean(KEY_RELAY_STATE, isCutOff).apply();

        if (isCutOff) {
            ibCombinedEngine.setImageResource(R.drawable.btn_power_green);
            tvCombinedEngineLabel.setText("MENYALAKAN MESIN");
            tvCombinedEngineLabel.setTextColor(0xFF16A34A);
            tvCombinedEngineSub.setText("Ketuk tombol untuk menyalakan mesin");

            tvRelayStatusBadge.setText("● Mesin: Terputus (Mati)");
            tvRelayStatusBadge.setTextColor(0xFFDC2626);
            tvRelayStatusBadge.setBackgroundResource(R.drawable.badge_stop_bg);
        } else {
            ibCombinedEngine.setImageResource(R.drawable.btn_power_red);
            tvCombinedEngineLabel.setText("MATIKAN MESIN");
            tvCombinedEngineLabel.setTextColor(0xFFDC2626);
            tvCombinedEngineSub.setText("Ketuk tombol untuk mematikan mesin");

            tvRelayStatusBadge.setText("● Mesin: Menyala (Normal)");
            tvRelayStatusBadge.setTextColor(0xFF15803D);
            tvRelayStatusBadge.setBackgroundResource(R.drawable.badge_status_bg);
        }

        // Sinkronisasi status mesin ke seluruh Home Screen Widget secara real-time
        try {
            EngineControlWidget.updateAllWidgets(this);
        } catch (Exception ignored) {}
    }

    private void setupListeners() {
        // Navigasi Menu Bottom Bar (Home & Pengaturan)
        navTabHome.setOnClickListener(v -> showPage(0));
        navTabSettings.setOnClickListener(v -> attemptOpenSettings());

        // Tombol Aksi Halaman Pengaturan - Target SIM
        btnPickContact.setOnClickListener(v -> openContactPicker(REQUEST_PICK_CONTACT));
        btnSaveSettings.setOnClickListener(v -> saveSettingsFromPage());
        btnSettingsBackToHome.setOnClickListener(v -> showPage(0));

        // Pengaturan Kunci Keamanan Menu Pengaturan (Sidik Jari / Pola Sistem HP)
        if (switchSettingsLock != null) {
            switchSettingsLock.setOnCheckedChangeListener((btn, isChecked) -> {
                if (isChecked) {
                    KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                    if (km != null && !km.isKeyguardSecure()) {
                        Toast.makeText(this, "⚠️ Peringatan: Perangkat belum mengaktifkan Kunci Layar (Pola/PIN/Sidik Jari). Silakan atur di Pengaturan HP Anda.", Toast.LENGTH_LONG).show();
                    }
                }
                if (tvSettingsLockStatusBadge != null) {
                    tvSettingsLockStatusBadge.setText(isChecked ? "STATUS: KUNCI AKTIF (Sistem HP)" : "STATUS: KUNCI NONAKTIF");
                    tvSettingsLockStatusBadge.setTextColor(isChecked ? 0xFF16A34A : 0xFF94A3B8);
                }
                preferences.edit().putBoolean(KEY_LOCK_ENABLED, isChecked).apply();
            });
        }

        // Tombol Sinkronisasi PARAM# dari Tracker
        View.OnClickListener fetchParamListener = v -> {
            executeCommand("PARAM#", "Cek Konfigurasi Parameter (PARAM#)");
            Toast.makeText(this, "📡 Mengirim PARAM# ke tracker... Balasan akan otomatis mengisi nomor Master & SOS ke form!", Toast.LENGTH_LONG).show();
        };
        if (btnFetchParamFromTracker != null) btnFetchParamFromTracker.setOnClickListener(fetchParamListener);
        if (btnFetchParamMaster != null) btnFetchParamMaster.setOnClickListener(fetchParamListener);
        if (btnFetchParamSos != null) btnFetchParamSos.setOnClickListener(fetchParamListener);

        // Inisialisasi Spinner Mode Kecepatan
        String[] speedModes = new String[]{
                "Mode 0 (Hanya via Aplikasi/Server)",
                "Mode 1 (SMS + Aplikasi/Server)",
                "Mode 2 (Telepon + SMS + Aplikasi/Server)"
        };
        ArrayAdapter<String> speedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, speedModes);
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerSpeedMode != null) {
            spinnerSpeedMode.setAdapter(speedAdapter);
            spinnerSpeedMode.setSelection(preferences.getInt(KEY_SPEED_MODE, 1));
        }

        // Tombol Aksi Pengaturan Master Number
        if (btnPickMasterContact != null) {
            btnPickMasterContact.setOnClickListener(v -> openContactPicker(REQUEST_PICK_MASTER));
        }
        if (btnSetMasterNumber != null) {
            btnSetMasterNumber.setOnClickListener(v -> {
                String master = etSettingsMasterNumber.getText().toString().trim();
                if (TextUtils.isEmpty(master)) {
                    Toast.makeText(this, "Nomor Master tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showConfirmationDialog(
                        "👑 Tetapkan Nomor Master",
                        "Kirim perintah SMS berikut untuk menetapkan " + master + " sebagai Nomor Master?\n\nPerintah: CENTER,A," + master + "#\n\n(Catatan: Wajib terdaftar sebagai nomor yang berhak mengontrol relay mesin).",
                        "CENTER,A," + master + "#",
                        "Tetapkan Nomor Master"
                );
                preferences.edit().putString(KEY_MASTER_NUMBER, master).apply();
            });
        }
        if (btnDeleteMasterNumber != null) {
            btnDeleteMasterNumber.setOnClickListener(v -> {
                showConfirmationDialog(
                        "🗑️ Hapus Nomor Master",
                        "Kirim perintah SMS CENTER,D# ke tracker untuk menghapus nomor Master yang terdaftar?",
                        "CENTER,D#",
                        "Hapus Nomor Master"
                );
                etSettingsMasterNumber.setText("");
                if (tvMasterContactName != null) tvMasterContactName.setText("👤 Kontak: Belum dipilih");
                preferences.edit().remove(KEY_MASTER_NUMBER).remove(KEY_MASTER_NAME).apply();
            });
        }

        if (btnCopyParamMaster != null) {
            btnCopyParamMaster.setOnClickListener(v -> {
                String savedMaster = preferences.getString(KEY_SAVED_CENTER, "").trim();
                if (!TextUtils.isEmpty(savedMaster) && !"-".equals(savedMaster)) {
                    etSettingsMasterNumber.setText(savedMaster);
                    preferences.edit().putString(KEY_MASTER_NUMBER, savedMaster).apply();
                    Toast.makeText(this, "Nomor Master (" + savedMaster + ") disalin ke form input dan disimpan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Belum ada data Master dari PARAM#", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Tombol Aksi Pengaturan SOS - Pick Kontak
        if (btnPickSos1 != null) btnPickSos1.setOnClickListener(v -> openContactPicker(REQUEST_PICK_SOS_1));
        if (btnPickSos2 != null) btnPickSos2.setOnClickListener(v -> openContactPicker(REQUEST_PICK_SOS_2));
        if (btnPickSos3 != null) btnPickSos3.setOnClickListener(v -> openContactPicker(REQUEST_PICK_SOS_3));

        if (btnCopyParamSos != null) {
            btnCopyParamSos.setOnClickListener(v -> {
                String savedSos = preferences.getString(KEY_SAVED_SOS, "").trim();
                if (!TextUtils.isEmpty(savedSos) && !"-".equals(savedSos)) {
                    String[] parts = savedSos.split("[,;]");
                    String s1 = parts.length > 0 ? parts[0].trim() : "";
                    String s2 = parts.length > 1 ? parts[1].trim() : "";
                    String s3 = parts.length > 2 ? parts[2].trim() : "";
                    if (!s1.isEmpty()) etSettingsSos1.setText(s1);
                    if (!s2.isEmpty()) etSettingsSos2.setText(s2);
                    if (!s3.isEmpty()) etSettingsSos3.setText(s3);
                    preferences.edit()
                            .putString(KEY_SOS_1, s1)
                            .putString(KEY_SOS_2, s2)
                            .putString(KEY_SOS_3, s3)
                            .apply();
                    Toast.makeText(this, "Nomor SOS disalin ke form input dan disimpan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Belum ada data SOS dari PARAM#", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Tombol Aksi Pengaturan SOS - Hapus Masing-Masing Nomor
        if (btnDeleteSos1 != null) {
            btnDeleteSos1.setOnClickListener(v -> {
                String num = etSettingsSos1.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "Nomor SOS 1 kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                showConfirmationDialog(
                        "🗑️ Hapus Nomor SOS 1",
                        "Kirim SMS SOS,D," + num + "# untuk menghapus nomor SOS 1 dari tracker?",
                        "SOS,D," + num + "#",
                        "Hapus SOS 1"
                );
                etSettingsSos1.setText("");
                if (tvSosName1 != null) tvSosName1.setText("👤 Kontak: Belum dipilih");
                preferences.edit().remove(KEY_SOS_1).remove(KEY_SOS_NAME_1).apply();
            });
        }
        if (btnDeleteSos2 != null) {
            btnDeleteSos2.setOnClickListener(v -> {
                String num = etSettingsSos2.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "Nomor SOS 2 kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                showConfirmationDialog(
                        "🗑️ Hapus Nomor SOS 2",
                        "Kirim SMS SOS,D," + num + "# untuk menghapus nomor SOS 2 dari tracker?",
                        "SOS,D," + num + "#",
                        "Hapus SOS 2"
                );
                etSettingsSos2.setText("");
                if (tvSosName2 != null) tvSosName2.setText("👤 Kontak: Belum dipilih");
                preferences.edit().remove(KEY_SOS_2).remove(KEY_SOS_NAME_2).apply();
            });
        }
        if (btnDeleteSos3 != null) {
            btnDeleteSos3.setOnClickListener(v -> {
                String num = etSettingsSos3.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    Toast.makeText(this, "Nomor SOS 3 kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                showConfirmationDialog(
                        "🗑️ Hapus Nomor SOS 3",
                        "Kirim SMS SOS,D," + num + "# untuk menghapus nomor SOS 3 dari tracker?",
                        "SOS,D," + num + "#",
                        "Hapus SOS 3"
                );
                etSettingsSos3.setText("");
                if (tvSosName3 != null) tvSosName3.setText("👤 Kontak: Belum dipilih");
                preferences.edit().remove(KEY_SOS_3).remove(KEY_SOS_NAME_3).apply();
            });
        }

        // Tombol Daftarkan Semua SOS (SOS,A,Nomor1,Nomor2,Nomor3#)
        if (btnSaveAndSendSos != null) {
            btnSaveAndSendSos.setOnClickListener(v -> {
                String s1 = etSettingsSos1.getText().toString().trim();
                String s2 = etSettingsSos2.getText().toString().trim();
                String s3 = etSettingsSos3.getText().toString().trim();

                if (TextUtils.isEmpty(s1) && TextUtils.isEmpty(s2) && TextUtils.isEmpty(s3)) {
                    Toast.makeText(this, "Isi minimal 1 nomor SOS terlebih dahulu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder cmdBuilder = new StringBuilder("SOS,A,");
                if (!TextUtils.isEmpty(s1)) cmdBuilder.append(s1);
                if (!TextUtils.isEmpty(s2) || !TextUtils.isEmpty(s3)) {
                    cmdBuilder.append(",");
                    if (!TextUtils.isEmpty(s2)) cmdBuilder.append(s2);
                }
                if (!TextUtils.isEmpty(s3)) {
                    cmdBuilder.append(",").append(s3);
                }
                cmdBuilder.append("#");
                String cmd = cmdBuilder.toString();

                showConfirmationDialog(
                        "🆘 Daftarkan Nomor SOS",
                        "Kirim perintah pendaftaran nomor SOS berikut ke tracker?\n\nPerintah: " + cmd,
                        cmd,
                        "Daftar Nomor SOS"
                );
                preferences.edit()
                        .putString(KEY_SOS_1, s1)
                        .putString(KEY_SOS_2, s2)
                        .putString(KEY_SOS_3, s3)
                        .apply();
            });
        }

        // Toggle Switch Pembatasan Kecepatan
        if (switchSpeedAlarm != null) {
            switchSpeedAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (layoutSpeedSettingsDetail != null) layoutSpeedSettingsDetail.setVisibility(View.VISIBLE);
                    String currentLimit = etSpeedLimit != null ? etSpeedLimit.getText().toString().trim() : "80";
                    if (tvSpeedAlarmStatusBadge != null) {
                        tvSpeedAlarmStatusBadge.setText("STATUS: FORM AKTIF (" + (TextUtils.isEmpty(currentLimit) ? "80" : currentLimit) + " km/h)");
                        tvSpeedAlarmStatusBadge.setTextColor(0xFF16A34A);
                    }
                } else {
                    if (layoutSpeedSettingsDetail != null) layoutSpeedSettingsDetail.setVisibility(View.GONE);
                    if (tvSpeedAlarmStatusBadge != null) {
                        tvSpeedAlarmStatusBadge.setText("STATUS: ALARM NONAKTIF");
                        tvSpeedAlarmStatusBadge.setTextColor(0xFF94A3B8);
                    }
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("🛑 Matikan Alarm Kecepatan")
                            .setMessage("Apakah Anda ingin mengirim perintah SPEED,OFF# ke tracker untuk menonaktifkan alarm kecepatan?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Kirim SPEED,OFF#", (dialog, which) -> {
                                executeCommand("SPEED,OFF#", "Matikan Alarm Kecepatan");
                                preferences.edit().putBoolean(KEY_SPEED_ALARM_ENABLED, false).apply();
                            })
                            .setNegativeButton("Hanya Matikan di Form", (dialog, which) -> {
                                preferences.edit().putBoolean(KEY_SPEED_ALARM_ENABLED, false).apply();
                            })
                            .show();
                }
            });
        }

        // Tombol Terapkan Pembatasan Kecepatan (SPEED,ON,Detik,Kecepatan,Mode#)
        if (btnApplySpeedAlarm != null) {
            btnApplySpeedAlarm.setOnClickListener(v -> {
                String speed = etSpeedLimit != null ? etSpeedLimit.getText().toString().trim() : "80";
                if (TextUtils.isEmpty(speed)) speed = "80";
                String duration = etSpeedDuration != null ? etSpeedDuration.getText().toString().trim() : "10";
                if (TextUtils.isEmpty(duration)) duration = "10";
                int mode = spinnerSpeedMode != null ? spinnerSpeedMode.getSelectedItemPosition() : 1;
                if (mode < 0) mode = 1;

                String cmd = "SPEED,ON," + duration + "," + speed + "," + mode + "#";
                final String finalSpeed = speed;
                final String finalDuration = duration;
                final int finalMode = mode;

                showConfirmationDialog(
                        "⚡ Terapkan Batas Kecepatan",
                        "Kirim perintah pembatasan kecepatan ke tracker?\n\nPerintah: " + cmd +
                                "\n• Batas Kecepatan: " + speed + " km/jam" +
                                "\n• Durasi Toleransi: " + duration + " detik" +
                                "\n• Mode Peringatan: " + speedModes[mode],
                        cmd,
                        "Atur Batas Kecepatan"
                );

                preferences.edit()
                        .putBoolean(KEY_SPEED_ALARM_ENABLED, true)
                        .putString(KEY_SPEED_LIMIT, finalSpeed)
                        .putString(KEY_SPEED_DURATION, finalDuration)
                        .putInt(KEY_SPEED_MODE, finalMode)
                        .apply();
                if (tvSpeedAlarmStatusBadge != null) {
                    tvSpeedAlarmStatusBadge.setText("STATUS: ALARM AKTIF (" + finalSpeed + " km/h)");
                    tvSpeedAlarmStatusBadge.setTextColor(0xFF16A34A);
                }
                if (switchSpeedAlarm != null && !switchSpeedAlarm.isChecked()) {
                    switchSpeedAlarm.setChecked(true);
                }
            });
        }

        // Tombol Manual Matikan Alarm Kecepatan (SPEED,OFF#)
        if (btnTurnOffSpeedAlarm != null) {
            btnTurnOffSpeedAlarm.setOnClickListener(v -> {
                showConfirmationDialog(
                        "🛑 Matikan Alarm Kecepatan",
                        "Kirim perintah SMS SPEED,OFF# ke tracker untuk menonaktifkan alarm kecepatan?",
                        "SPEED,OFF#",
                        "Matikan Alarm Kecepatan"
                );
                if (switchSpeedAlarm != null) switchSpeedAlarm.setChecked(false);
                preferences.edit().putBoolean(KEY_SPEED_ALARM_ENABLED, false).apply();
            });
        }

        // Pengaturan Alarm Getaran (SENALM,ON# / SENALM,OFF#)
        if (switchVibrationAlarm != null) {
            switchVibrationAlarm.setOnCheckedChangeListener((btn, isChecked) -> {
                if (tvVibrationAlarmStatusBadge != null) {
                    tvVibrationAlarmStatusBadge.setText(isChecked ? "STATUS: ALARM AKTIF (SENALM,ON)" : "STATUS: ALARM NONAKTIF");
                    tvVibrationAlarmStatusBadge.setTextColor(isChecked ? 0xFF16A34A : 0xFF94A3B8);
                }
                if (!btn.isPressed()) return;

                if (isChecked) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("📳 Aktifkan Alarm Getaran")
                            .setMessage("Kirim perintah SENALM,ON# ke tracker untuk mengaktifkan sensor alarm getaran saat mobil diparkir?")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("Kirim SENALM,ON#", (dialog, which) -> {
                                executeCommand("SENALM,ON#", "Aktifkan Alarm Getaran");
                                preferences.edit().putBoolean(KEY_VIBRATION_ALARM_ENABLED, true).apply();
                            })
                            .setNegativeButton("Batal", (dialog, which) -> btn.setChecked(false))
                            .show();
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("🛑 Matikan Alarm Getaran")
                            .setMessage("Kirim perintah SENALM,OFF# ke tracker untuk menonaktifkan sensor alarm getaran?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Kirim SENALM,OFF#", (dialog, which) -> {
                                executeCommand("SENALM,OFF#", "Matikan Alarm Getaran");
                                preferences.edit().putBoolean(KEY_VIBRATION_ALARM_ENABLED, false).apply();
                            })
                            .setNegativeButton("Hanya Matikan di Form", (dialog, which) -> {
                                preferences.edit().putBoolean(KEY_VIBRATION_ALARM_ENABLED, false).apply();
                            })
                            .show();
                }
            });
        }

        btnClearLog.setOnClickListener(v -> {
            String num = preferences.getString(KEY_PHONE_NUMBER, "");
            if (!TextUtils.isEmpty(num)) {
                tvCommandLog.setText("Menunggu balasan SMS dari kartu SIM GPS (" + num + ")...");
            } else {
                tvCommandLog.setText(getString(R.string.log_empty));
            }
        });

        // 1. KONTROL MESIN TERGABUNG
        ibCombinedEngine.setOnClickListener(v -> {
            if (isEngineCutOff) {
                executeCommand("RELAY,0#", "Menyalakan Mesin");
            } else {
                showConfirmationDialog(
                        getString(R.string.dialog_stop_title),
                        getString(R.string.dialog_stop_message),
                        "RELAY,1#",
                        "Matikan Mesin"
                );
            }
        });

        tvSyncRelayState.setOnClickListener(v -> {
            updateCombinedEngineUI(!isEngineCutOff);
            String status = isEngineCutOff ? "Mati / Terputus" : "Menyala / Normal";
            Toast.makeText(MainActivity.this, "Status tampilan diubah manual ke: " + status, Toast.LENGTH_SHORT).show();
        });

        // 2. PELACAKAN KENDARAAN (LOKASI MAPS & KOORDINAT)
        btnCheckGoogleMaps.setOnClickListener(v -> executeCommand("URL#", "Lokasi Maps"));
        btnCheckCoords.setOnClickListener(v -> executeCommand("WHERE#", "Koordinat"));

        btnOpenExternalMap.setOnClickListener(v -> {
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(GT06+Vehicle)", currentLat, currentLon, currentLat, currentLon);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            try {
                startActivity(intent);
            } catch (Exception e) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + currentLat + "," + currentLon));
                startActivity(webIntent);
            }
        });

        // 3. KONFIGURASI PARAMETER SISTEM (TERPISAH)
        btnCheckParam.setOnClickListener(v -> executeCommand("PARAM#", "Cek Konfigurasi Parameter"));

        // 4. STATUS OPERASIONAL KENDARAAN (TERPISAH)
        btnCheckStatus.setOnClickListener(v -> executeCommand("STATUS#", "Cek Status Operasional"));

        // 5. STATUS & KONFIGURASI GPRS (SEBELUM PEMELIHARAAN)
        btnCheckGprsConfig.setOnClickListener(v -> executeCommand("GPRSSET#", "Cek Konfigurasi GPRS"));

        // 6. VERSI FIRMWARE PERANGKAT (SEBELUM PEMELIHARAAN)
        btnCheckFirmware.setOnClickListener(v -> executeCommand("VERSION#", "Cek Versi Firmware"));

        // 7. PEMELIHARAAN SISTEM - TOMBOL RESTART DENGAN ICON BARU
        ibRestartDevice.setOnClickListener(v -> {
            showConfirmationDialog(
                    getString(R.string.dialog_restart_title),
                    getString(R.string.dialog_restart_message),
                    "RESET#",
                    "Restart Perangkat"
            );
        });

        // Demo simulasi respons SMS
        btnSimulateSms.setOnClickListener(v -> simulateIncomingSmsDemo());

        // Navigasi Stack Swipeable (Kategori 3, 4, 5, 6)
        setupStackNavigation();
    }

    private void setupStackNavigation() {
        if (hsvDiagnosticStack == null) return;

        hsvDiagnosticStack.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = hsvDiagnosticStack.getWidth();
                if (width > 0) {
                    hsvDiagnosticStack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setCardWidth(cardStackParam, width);
                    setCardWidth(cardStackStatus, width);
                    setCardWidth(cardStackGprs, width);
                    setCardWidth(cardStackFirmware, width);
                }
            }
        });

        tabStackParam.setOnClickListener(v -> scrollStackTo(0));
        tabStackStatus.setOnClickListener(v -> scrollStackTo(1));
        tabStackGprs.setOnClickListener(v -> scrollStackTo(2));
        tabStackFirmware.setOnClickListener(v -> scrollStackTo(3));

        btnStackPrev.setOnClickListener(v -> {
            if (currentStackPage > 0) {
                scrollStackTo(currentStackPage - 1);
            }
        });
        btnStackNext.setOnClickListener(v -> {
            if (currentStackPage < 3) {
                scrollStackTo(currentStackPage + 1);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hsvDiagnosticStack.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                int width = hsvDiagnosticStack.getWidth();
                if (width <= 0) return;
                int page = Math.round((float) scrollX / width);
                if (page < 0) page = 0;
                if (page > 3) page = 3;
                if (page != currentStackPage) {
                    currentStackPage = page;
                    updateStackTabsUI(page);
                }
            });
        }
    }

    private void setCardWidth(View view, int width) {
        if (view != null) {
            android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                lp.width = width;
                view.setLayoutParams(lp);
            }
        }
    }

    private void scrollStackTo(int pageIndex) {
        currentStackPage = pageIndex;
        updateStackTabsUI(pageIndex);
        if (hsvDiagnosticStack == null) return;
        hsvDiagnosticStack.post(() -> {
            int width = hsvDiagnosticStack.getWidth();
            View[] cards = {cardStackParam, cardStackStatus, cardStackGprs, cardStackFirmware};
            int targetX = 0;
            if (pageIndex >= 0 && pageIndex < cards.length && cards[pageIndex] != null) {
                targetX = cards[pageIndex].getLeft();
            }
            if (targetX == 0 && pageIndex > 0 && width > 0) {
                targetX = pageIndex * width;
            }
            hsvDiagnosticStack.smoothScrollTo(targetX, 0);
        });
    }

    private void updateStackTabsUI(int pageIndex) {
        String[] titles = {"Sistem", "Status", "GPRS", "Firmware"};
        tvStackPageIndicator.setText((pageIndex + 1) + " / 4 • " + titles[pageIndex]);

        Button[] tabs = {tabStackParam, tabStackStatus, tabStackGprs, tabStackFirmware};
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i] != null) {
                if (i == pageIndex) {
                    tabs[i].setBackgroundResource(R.drawable.tab_active_bg);
                    tabs[i].setTextColor(0xFFFFFFFF);
                } else {
                    tabs[i].setBackgroundResource(R.drawable.tab_inactive_bg);
                    tabs[i].setTextColor(0xFF64748B);
                }
            }
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            java.util.List<String> perms = new java.util.ArrayList<>();
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                perms.add(Manifest.permission.RECEIVE_SMS);
            }
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                perms.add(Manifest.permission.SEND_SMS);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    perms.add(Manifest.permission.POST_NOTIFICATIONS);
                }
            }
            if (!perms.isEmpty()) {
                requestPermissions(perms.toArray(new String[0]), PERMISSION_REQUEST_RECEIVE_SMS);
            }
        }
    }

    private void showAlarmAlertDialog(GpsNotificationHelper.AlarmInfo alarm, String fullMessage) {
        if (isFinishing() || isDestroyed()) return;

        new AlertDialog.Builder(this)
                .setTitle(alarm.title)
                .setMessage(alarm.description + "\n\nIsi SMS:\n" + fullMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Mengerti", null)
                .setNeutralButton("Salin SMS", (dialog, which) -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Alarm SMS", fullMessage);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(this, "📋 Pesan alarm disalin ke clipboard", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private String getTargetPhoneNumber() {
        String number = preferences.getString(KEY_PHONE_NUMBER, "").trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Silakan atur nomor SIM Card GPS di menu Pengaturan terlebih dahulu!", Toast.LENGTH_LONG).show();
            attemptOpenSettings();
            return null;
        }
        return number;
    }

    private void showConfirmationDialog(String title, String message, final String command, final String label) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(R.string.btn_yes), (dialog, which) -> executeCommand(command, label))
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void startSmsResponseWatchdog() {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        isAwaitingResponse = true;
        timeoutHandler.postDelayed(timeoutRunnable, SMS_TIMEOUT_MS);
    }

    private void cancelSmsResponseWatchdog() {
        isAwaitingResponse = false;
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }

    private void showNoResponseWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_warning))
                .setMessage(getString(R.string.msg_no_response))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", null)
                .show();
    }

    private void executeCommand(String command, String label) {
        lastSentCommand = command;
        String phoneNumber = getTargetPhoneNumber();
        if (phoneNumber == null) return;

        boolean directSms = preferences.getBoolean(KEY_DIRECT_SMS, true);

        if (directSms) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                pendingCommand = command;
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                return;
            }

            try {
                SmsManager smsManager;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    smsManager = getSystemService(SmsManager.class);
                } else {
                    smsManager = SmsManager.getDefault();
                }

                if (smsManager == null) {
                    smsManager = SmsManager.getDefault();
                }

                smsManager.sendTextMessage(phoneNumber, null, command, null, null);
                Toast.makeText(this, label + " terkirim. Menunggu balasan...", Toast.LENGTH_SHORT).show();
                appendLog(label, "Perintah Terkirim", phoneNumber, "Menunggu Balasan");

                startSmsResponseWatchdog();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal kirim langsung, membuka aplikasi SMS...", Toast.LENGTH_SHORT).show();
                openSystemSmsApp(phoneNumber, command, label);
            }
        } else {
            openSystemSmsApp(phoneNumber, command, label);
        }
    }

    private void openSystemSmsApp(String phoneNumber, String command, String label) {
        lastSentCommand = command;
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
            intent.putExtra("sms_body", command);
            startActivity(intent);
            appendLog(label, "Aplikasi SMS Dibuka", phoneNumber, "Terkirim Manual");

            startSmsResponseWatchdog();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membuka aplikasi SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void handleIncomingTrackerSms(String sender, String message) {
        String targetGpsNumber = preferences.getString(KEY_PHONE_NUMBER, "").trim();
        if (!targetGpsNumber.isEmpty() && !isAllowedSender(sender, targetGpsNumber)) {
            // Tolak dan abaikan balasan dari nomor HP yang bukan SIM Card GPS
            return;
        }

        cancelSmsResponseWatchdog();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeStr = sdf.format(new Date());

        String displaySender = sender != null ? sender : (!targetGpsNumber.isEmpty() ? targetGpsNumber : "GPS");
        appendLog("📥 Balasan Tracker GPS", message, displaySender, "Diterima (Terverifikasi)");
        Toast.makeText(this, "💬 Balasan dari GPS diterima!", Toast.LENGTH_SHORT).show();

        // 0. Deteksi Alarm Keamanan (Overspeed, Cut Power, Low Battery, Getaran)
        GpsNotificationHelper.AlarmInfo alarm = GpsNotificationHelper.detectAlarm(message);
        if (alarm != null) {
            GpsNotificationHelper.checkAndTriggerAlarmNotification(this, message);
            showAlarmAlertDialog(alarm, message);
        }

        // 1. Lokasi (Maps & Koordinat & Speed)
        parseLocationFromSms(message, timeStr);

        // 2. Parameter (PARAM#)
        if (message.contains("IMEI") || message.contains("Center") || message.contains("Timer:") || message.contains("Sensorset")) {
            parseParameterFromSms(message, timeStr);
            scrollStackTo(0);
        }
        // 3. Status & Konfigurasi GPRS (GPRSSET#)
        else if (message.contains("APN") || message.contains("GPRSSET") || message.contains("Server:") || (message.contains("IP:") && message.contains("Port:"))) {
            parseGprsConfigFromSms(message, timeStr);
            scrollStackTo(2);
        }
        // 4. Status Operasional (STATUS#)
        else if (message.contains("Battery") || message.contains("STATUS") || (message.contains("ACC:") && !message.contains("Timer")) || message.contains("Link UP") || message.contains("Link Down") || message.contains("Signal Level")) {
            parseStatusFromSms(message, timeStr);
            scrollStackTo(1);
        }
        // 5. Versi Firmware Perangkat (VERSION#)
        else if (message.contains("[VERSION]") || message.contains("VERSION") || message.contains("VER:") || message.contains("Release:") || (message.contains("HW:") && message.contains("Model:"))) {
            parseFirmwareFromSms(message, timeStr);
            scrollStackTo(3);
        }

        // 6. Relay Mesin
        if (message.contains("Cut off the fuel supply") || message.contains("Cut fuel") || message.contains("DYD success")) {
            updateCombinedEngineUI(true);
            Toast.makeText(this, "🛑 Mesin Berhasil Diputus!", Toast.LENGTH_LONG).show();
        } else if (message.contains("Restore fuel supply") || message.contains("Restore fuel") || message.contains("HFYD success")) {
            updateCombinedEngineUI(false);
            Toast.makeText(this, "🟢 Mesin Berhasil Dinyalakan!", Toast.LENGTH_LONG).show();
        }

        // 7. Sensor Alarm Getaran (SENALM)
        if (message.contains("SENALM") || message.contains("Sensor alarm")) {
            if (Pattern.compile("(?i)(ON|success|enabled)").matcher(message).find() && !message.contains("OFF")) {
                if (switchVibrationAlarm != null) switchVibrationAlarm.setChecked(true);
                if (tvVibrationAlarmStatusBadge != null) {
                    tvVibrationAlarmStatusBadge.setText("STATUS: ALARM AKTIF (SENALM,ON)");
                    tvVibrationAlarmStatusBadge.setTextColor(0xFF16A34A);
                }
                preferences.edit().putBoolean(KEY_VIBRATION_ALARM_ENABLED, true).apply();
                Toast.makeText(this, "📳 Alarm Getaran Aktif!", Toast.LENGTH_SHORT).show();
            } else if (Pattern.compile("(?i)(OFF|disabled)").matcher(message).find()) {
                if (switchVibrationAlarm != null) switchVibrationAlarm.setChecked(false);
                if (tvVibrationAlarmStatusBadge != null) {
                    tvVibrationAlarmStatusBadge.setText("STATUS: ALARM NONAKTIF");
                    tvVibrationAlarmStatusBadge.setTextColor(0xFF94A3B8);
                }
                preferences.edit().putBoolean(KEY_VIBRATION_ALARM_ENABLED, false).apply();
                Toast.makeText(this, "🛑 Alarm Getaran Dinonaktifkan!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean parseLocationFromSms(String message, String timeStr) {
        try {
            SimpleDateFormat fullSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String fullDateTime = fullSdf.format(new Date());

            // 1. Ekstraksi Kecepatan Kendaraan (Speed) jika ada
            Matcher mSpeed = Pattern.compile("(?i)Speed:?\\s*([0-9]+(?:\\.[0-9]+)?)\\s*(?:Km/h|kmh)?").matcher(message);
            if (mSpeed.find()) {
                String speedVal = mSpeed.group(1);
                String speedFormatted = speedVal + " Km/h";
                if (tvCoordsSpeed != null) tvCoordsSpeed.setText("⚡ Speed: " + speedFormatted);
                if (tvMapSpeed != null) tvMapSpeed.setText("⚡ Speed: " + speedFormatted);
                preferences.edit().putString(KEY_SAVED_SPEED, speedFormatted).apply();
            }

            // 2. Ekstraksi Koordinat Lat & Lon
            Pattern p = Pattern.compile("(?i)(?:q=|Lat:)\\s*([NS]?[-+]?\\d+\\.\\d+)[,\\s]+(?:Lon:)?\\s*([EW]?[-+]?\\d+\\.\\d+)");
            Matcher m = p.matcher(message);

            if (m.find()) {
                String latStr = m.group(1);
                String lonStr = m.group(2);

                double lat = parseCoordinate(latStr);
                double lon = parseCoordinate(lonStr);

                currentLat = lat;
                currentLon = lon;

                preferences.edit()
                        .putLong(KEY_SAVED_LAT, Double.doubleToRawLongBits(lat))
                        .putLong(KEY_SAVED_LON, Double.doubleToRawLongBits(lon))
                        .apply();

                renderMap(lat, lon, "Posisi GT06 (" + fullDateTime + ")");
                tvMapCoords.setText(String.format(Locale.ENGLISH, "Koordinat: %.6f, %.6f", lat, lon));
                tvMapTime.setText("Update: " + fullDateTime);

                // Cek apakah balasan merupakan URL Maps atau Koordinat Lengkap (WHERE#)
                boolean isUrl = "URL#".equalsIgnoreCase(lastSentCommand) || message.contains("maps.google.com") || message.contains("http");
                boolean isWhere = "WHERE#".equalsIgnoreCase(lastSentCommand) || message.contains("Speed:") || message.contains("Course:") || message.contains("WHERE");

                if (isUrl) {
                    if (tvMapsLastUpdate != null) tvMapsLastUpdate.setText("Update: " + fullDateTime);
                    preferences.edit().putString(KEY_MAPS_LAST_UPDATE, fullDateTime).apply();
                }
                if (isWhere) {
                    if (tvCoordsLastUpdate != null) tvCoordsLastUpdate.setText("Update: " + fullDateTime);
                    preferences.edit().putString(KEY_COORDS_LAST_UPDATE, fullDateTime).apply();
                }
                if (!isUrl && !isWhere) {
                    if (tvMapsLastUpdate != null) tvMapsLastUpdate.setText("Update: " + fullDateTime);
                    if (tvCoordsLastUpdate != null) tvCoordsLastUpdate.setText("Update: " + fullDateTime);
                    preferences.edit()
                            .putString(KEY_MAPS_LAST_UPDATE, fullDateTime)
                            .putString(KEY_COORDS_LAST_UPDATE, fullDateTime)
                            .apply();
                }
                return true;
            } else if (message.contains("WHERE") || "WHERE#".equalsIgnoreCase(lastSentCommand)) {
                if (tvCoordsLastUpdate != null) tvCoordsLastUpdate.setText("Update: " + fullDateTime);
                preferences.edit().putString(KEY_COORDS_LAST_UPDATE, fullDateTime).apply();
                return true;
            } else if (message.contains("URL") || "URL#".equalsIgnoreCase(lastSentCommand)) {
                if (tvMapsLastUpdate != null) tvMapsLastUpdate.setText("Update: " + fullDateTime);
                preferences.edit().putString(KEY_MAPS_LAST_UPDATE, fullDateTime).apply();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private double parseCoordinate(String coordStr) {
        if (coordStr == null) return 0;
        coordStr = coordStr.trim().toUpperCase();
        boolean isNegative = coordStr.startsWith("S") || coordStr.startsWith("W") || coordStr.startsWith("-");
        String clean = coordStr.replaceAll("[NSEW+]", "");
        double val = Double.parseDouble(clean);
        return isNegative ? -Math.abs(val) : Math.abs(val);
    }

    private void parseParameterFromSms(String message, String timeStr) {
        if (tvParamRawSms != null) {
            tvParamRawSms.setText(message);
        }
        preferences.edit().putString(KEY_SAVED_PARAM_RAW, message).apply();

        Matcher mImei = Pattern.compile("(?i)IMEI:?\\s*([0-9]+)").matcher(message);
        if (mImei.find()) {
            String imei = mImei.group(1);
            tvParamImei.setText(imei);
            preferences.edit().putString(KEY_SAVED_IMEI, imei).apply();
        }

        Matcher mCenter = Pattern.compile("(?i)Center(?:\\s*Number)?:?\\s*([0-9+]+)").matcher(message);
        if (mCenter.find()) {
            String center = mCenter.group(1).trim();
            tvParamCenter.setText(center);
            preferences.edit().putString(KEY_SAVED_CENTER, center).apply();

            // Otomatis isikan ke textbox Master di Halaman Pengaturan dan simpan datanya
            if (etSettingsMasterNumber != null) {
                etSettingsMasterNumber.setText(center);
            }
            preferences.edit().putString(KEY_MASTER_NUMBER, center).apply();
        }

        Matcher mTimer = Pattern.compile("(?i)Timer:?\\s*([0-9,]+)").matcher(message);
        if (mTimer.find()) {
            String timer = mTimer.group(1);
            tvParamTimer.setText(timer + "s");
            preferences.edit().putString(KEY_SAVED_TIMER, timer + "s").apply();
        }

        Matcher mSends = Pattern.compile("(?i)SENDS:?\\s*([0-9]+)").matcher(message);
        if (mSends.find()) {
            String sends = mSends.group(1) + "s";
            if (tvParamSends != null) {
                tvParamSends.setText(sends);
            }
            preferences.edit().putString(KEY_SAVED_PARAM_SENDS, sends).apply();
        }

        Matcher mSos = Pattern.compile("(?i)SOS(?:1)?:?\\s*([0-9,+;\\s]+)").matcher(message);
        if (mSos.find()) {
            String sos = mSos.group(1).trim();
            tvParamSos.setText(sos);
            preferences.edit().putString(KEY_SAVED_SOS, sos).apply();

            // Otomatis isikan ke textbox SOS 1, 2, 3 di Halaman Pengaturan dan simpan datanya
            String[] sosParts = sos.split("[,;]");
            String s1 = sosParts.length > 0 ? sosParts[0].trim() : "";
            String s2 = sosParts.length > 1 ? sosParts[1].trim() : "";
            String s3 = sosParts.length > 2 ? sosParts[2].trim() : "";

            if (etSettingsSos1 != null) etSettingsSos1.setText(s1);
            if (etSettingsSos2 != null) etSettingsSos2.setText(s2);
            if (etSettingsSos3 != null) etSettingsSos3.setText(s3);

            preferences.edit()
                    .putString(KEY_SOS_1, s1)
                    .putString(KEY_SOS_2, s2)
                    .putString(KEY_SOS_3, s3)
                    .apply();
        }

        Matcher mDefense = Pattern.compile("(?i)Defense\\s*time:?\\s*([0-9]+)").matcher(message);
        if (mDefense.find()) {
            String def = mDefense.group(1) + " Menit";
            if (tvParamDefense != null) {
                tvParamDefense.setText(def);
            }
            preferences.edit().putString(KEY_SAVED_PARAM_DEFENSE, def).apply();
        }

        Matcher mSensor = Pattern.compile("(?i)Sensorset:?\\s*([0-9,]+)").matcher(message);
        if (mSensor.find()) {
            String sensor = mSensor.group(1);
            tvParamSensor.setText(sensor);
            preferences.edit().putString(KEY_SAVED_SENSOR, sensor).apply();
        }

        Matcher mGmt = Pattern.compile("(?i)TimeZone:?\\s*([A-Za-z0-9,+-]+)").matcher(message);
        if (mGmt.find()) {
            String gmt = mGmt.group(1);
            tvParamGmt.setText("GMT: " + gmt);
            preferences.edit().putString(KEY_SAVED_GMT, "GMT: " + gmt).apply();
        }

        String lastUpdate = "Update: " + timeStr;
        tvParamLastUpdate.setText(lastUpdate);
        preferences.edit().putString(KEY_SAVED_PARAM_TIME, lastUpdate).apply();

        // Perbarui tampilan status nomor Master dan SOS terdaftar di Halaman Pengaturan
        updateSettingsParamDisplay();
    }

    private void parseStatusFromSms(String message, String timeStr) {
        if (tvStatusRawSms != null) {
            tvStatusRawSms.setText(message);
        }
        preferences.edit().putString(KEY_SAVED_STATUS_RAW, message).apply();

        Matcher mBattery = Pattern.compile("(?i)(?:Battery|Bat):?\\s*([0-9%Vv.]+)").matcher(message);
        if (mBattery.find()) {
            String bat = mBattery.group(1);
            tvStatusBattery.setText(bat);
            preferences.edit().putString(KEY_SAVED_BATTERY, bat).apply();
        }

        Matcher mAcc = Pattern.compile("(?i)ACC:?\\s*(ON|OFF)").matcher(message);
        if (mAcc.find()) {
            String acc = mAcc.group(1).toUpperCase();
            String accLabel = "ON".equals(acc) ? "ACC ON (Mesin Hidup)" : "ACC OFF (Parkir)";
            tvStatusAcc.setText(accLabel);
            preferences.edit().putString(KEY_SAVED_ACC, accLabel).apply();
        }

        Matcher mGprs = Pattern.compile("(?i)GPRS:?\\s*(Link\\s*UP|Link\\s*Down|ON|OFF|[A-Za-z]+)").matcher(message);
        if (mGprs.find()) {
            String gprs = mGprs.group(1);
            tvStatusGprs.setText("GPRS: " + gprs);
            preferences.edit().putString(KEY_SAVED_GPRS, "GPRS: " + gprs).apply();
        }

        Matcher mGps = Pattern.compile("(?i)GPS:?\\s*(Fix|Searching|Sleep|A|V|[A-Za-z]+)").matcher(message);
        if (mGps.find()) {
            String gps = mGps.group(1);
            tvStatusGps.setText("GPS: " + gps);
            preferences.edit().putString(KEY_SAVED_GPS_FIX, "GPS: " + gps).apply();
        }

        Matcher mDefense = Pattern.compile("(?i)Defense:?\\s*(Armed|Disarmed|[A-Za-z]+)").matcher(message);
        if (mDefense.find()) {
            String def = mDefense.group(1);
            if (tvStatusDefense != null) {
                tvStatusDefense.setText(def);
            }
            preferences.edit().putString(KEY_SAVED_STATUS_DEFENSE, def).apply();
        }

        Matcher mGsm = Pattern.compile("(?i)GSM\\s*Signal\\s*Level:?\\s*([0-9]+)").matcher(message);
        if (mGsm.find()) {
            String gsm = mGsm.group(1) + " / 31";
            if (tvStatusGsm != null) {
                tvStatusGsm.setText(gsm);
            }
            preferences.edit().putString(KEY_SAVED_STATUS_GSM, gsm).apply();
        }

        String lastUpdate = "Update: " + timeStr;
        tvStatusLastUpdate.setText(lastUpdate);
        preferences.edit().putString(KEY_SAVED_STATUS_TIME, lastUpdate).apply();
    }

    private void parseGprsConfigFromSms(String message, String timeStr) {
        if (tvGprsRawSms != null) {
            tvGprsRawSms.setText(message);
        }
        preferences.edit().putString(KEY_SAVED_GPRS_RAW, message).apply();

        Matcher mMode = Pattern.compile("(?i)GPRS:?\\s*([A-Za-z0-9]+)").matcher(message);
        if (mMode.find()) {
            String rawMode = mMode.group(1).toUpperCase();
            String modeStr = ("1".equals(rawMode) || "ON".equals(rawMode)) ? "GPRS: ON" : ("0".equals(rawMode) || "OFF".equals(rawMode) ? "GPRS: OFF" : "GPRS: " + rawMode);
            tvGprsStatusMode.setText(modeStr);
            preferences.edit().putString(KEY_SAVED_GPRS_MODE, modeStr).apply();
        }

        Matcher mApn = Pattern.compile("(?i)(?:Currently\\s*use\\s*APN|APN):?\\s*([A-Za-z0-9_.-]+)").matcher(message);
        if (mApn.find()) {
            String apn = mApn.group(1);
            tvGprsApn.setText(apn);
            preferences.edit().putString(KEY_SAVED_GPRS_APN, apn).apply();
        }

        // Standard Concox GT06 server pattern: Server:1,103.20.10.5,5023,0;
        Matcher mServerGT06 = Pattern.compile("(?i)Server:?\\s*([01]),([A-Za-z0-9_.-]+),(\\d+)(?:,(\\d+))?").matcher(message);
        if (mServerGT06.find()) {
            String modeDigit = mServerGT06.group(1);
            String host = mServerGT06.group(2);
            String port = mServerGT06.group(3);
            String protoDigit = mServerGT06.group(4);

            tvGprsServer.setText(host);
            preferences.edit().putString(KEY_SAVED_GPRS_SERVER, host).apply();

            tvGprsPort.setText(port);
            preferences.edit().putString(KEY_SAVED_GPRS_PORT, port).apply();

            String protocol = ("1".equals(modeDigit) ? "Domain" : "IP") + ("0".equals(protoDigit) ? " (TCP)" : " (UDP)");
            if (tvGprsProtocol != null) {
                tvGprsProtocol.setText(protocol);
            }
            preferences.edit().putString(KEY_SAVED_GPRS_PROTOCOL, protocol).apply();
        } else {
            Matcher mServer = Pattern.compile("(?i)(?:IP|Server|Domain):?\\s*([A-Za-z0-9_.-]+)").matcher(message);
            if (mServer.find()) {
                String server = mServer.group(1);
                tvGprsServer.setText(server);
                preferences.edit().putString(KEY_SAVED_GPRS_SERVER, server).apply();
            }

            Matcher mPort = Pattern.compile("(?i)Port:?\\s*([0-9]+)").matcher(message);
            if (mPort.find()) {
                String port = mPort.group(1);
                tvGprsPort.setText(port);
                preferences.edit().putString(KEY_SAVED_GPRS_PORT, port).apply();
            }
        }

        Matcher mUrl = Pattern.compile("(?i)URL:?\\s*([^\r\n;]+)").matcher(message);
        if (mUrl.find()) {
            String url = mUrl.group(1).trim();
            if (tvGprsUrl != null) {
                tvGprsUrl.setText(url);
            }
            preferences.edit().putString(KEY_SAVED_GPRS_URL, url).apply();
        }

        String lastUpdate = "Update: " + timeStr;
        tvGprsLastUpdate.setText(lastUpdate);
        preferences.edit().putString(KEY_SAVED_GPRS_TIME, lastUpdate).apply();
    }

    private void parseFirmwareFromSms(String message, String timeStr) {
        if (tvFirmwareRawSms != null) {
            tvFirmwareRawSms.setText(message);
        }
        preferences.edit().putString(KEY_SAVED_FW_RAW, message).apply();

        String fullBuild = "";
        String ver = "-";
        String hw = "-";
        String date = "-";
        String model = "-";

        // Cek format Concox/GT06: [VERSION]NT36_11_A1D_D23_R0_V17_c_WM
        Matcher mBracket = Pattern.compile("(?i)\\[VERSION\\]\\s*([^;\\r\\n]+)").matcher(message);
        if (mBracket.find()) {
            fullBuild = mBracket.group(1).trim();

            String[] parts = fullBuild.split("_");
            if (parts.length >= 1) {
                model = parts[0] + " (GT06 Series)";
            }
            if (parts.length >= 4) {
                hw = parts[2] + "_" + parts[3];
            } else if (parts.length >= 3) {
                hw = parts[2];
            }

            for (int i = 0; i < parts.length; i++) {
                String p = parts[i];
                if (p.matches("(?i)R\\d+.*")) {
                    date = p;
                } else if (p.matches("(?i)V\\d+.*")) {
                    StringBuilder sbVer = new StringBuilder();
                    for (int j = i; j < parts.length; j++) {
                        if (sbVer.length() > 0) sbVer.append("_");
                        sbVer.append(parts[j]);
                    }
                    ver = sbVer.toString();
                    break;
                }
            }
            if ("-".equals(ver)) {
                ver = fullBuild;
            }
        } else {
            // Format Key-Value
            Matcher mVer = Pattern.compile("(?i)(?:VERSION|VER):?\\s*([A-Za-z0-9_.-]+)").matcher(message);
            if (mVer.find()) ver = mVer.group(1);

            Matcher mHw = Pattern.compile("(?i)(?:HW|Hardware):?\\s*([A-Za-z0-9_.-]+)").matcher(message);
            if (mHw.find()) hw = mHw.group(1);

            Matcher mDate = Pattern.compile("(?i)(?:Release|Date|Build):?\\s*([0-9/_-]+)").matcher(message);
            if (mDate.find()) date = mDate.group(1);

            Matcher mModel = Pattern.compile("(?i)Model:?\\s*([^;,\\r\\n]+)").matcher(message);
            if (mModel.find()) model = mModel.group(1).trim();

            fullBuild = !"-".equals(ver) ? ver : message;
        }

        if (tvFirmwareFullBuild != null) {
            tvFirmwareFullBuild.setText(fullBuild.isEmpty() ? "-" : fullBuild);
            preferences.edit().putString(KEY_SAVED_FW_FULL, fullBuild).apply();
        }
        if (tvFirmwareVersion != null) {
            tvFirmwareVersion.setText(ver);
            preferences.edit().putString(KEY_SAVED_FW_VER, ver).apply();
        }
        if (tvHardwareVersion != null) {
            tvHardwareVersion.setText(hw);
            preferences.edit().putString(KEY_SAVED_FW_HW, hw).apply();
        }
        if (tvFirmwareRelease != null) {
            tvFirmwareRelease.setText(date);
            preferences.edit().putString(KEY_SAVED_FW_DATE, date).apply();
        }
        if (tvFirmwareModel != null) {
            tvFirmwareModel.setText(model);
            preferences.edit().putString(KEY_SAVED_FW_MODEL, model).apply();
        }

        String lastUpdate = "Update: " + timeStr;
        if (tvFirmwareLastUpdate != null) {
            tvFirmwareLastUpdate.setText(lastUpdate);
            preferences.edit().putString(KEY_SAVED_FW_TIME, lastUpdate).apply();
        }
    }

    private void renderMap(double lat, double lon, String label) {
        String html = "<!DOCTYPE html>" +
                "<html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>" +
                "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>" +
                "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
                "<style>" +
                "  body, html, #map { margin: 0; padding: 0; width: 100%; height: 100%; background: #0F172A; }" +
                "  .leaflet-popup-content-wrapper { border-radius: 8px; font-family: sans-serif; font-size: 12px; }" +
                "</style>" +
                "</head><body>" +
                "<div id='map'></div>" +
                "<script>" +
                "  var map = L.map('map', {zoomControl: true}).setView([" + lat + ", " + lon + "], 16);" +
                "  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
                "    maxZoom: 19," +
                "    attribution: 'GT06 GPS'" +
                "  }).addTo(map);" +
                "  var marker = L.marker([" + lat + ", " + lon + "]).addTo(map);" +
                "  marker.bindPopup('<b>" + label + "</b><br>Lat: " + lat + "<br>Lon: " + lon + "').openPopup();" +
                "</script>" +
                "</body></html>";

        wvMap.loadDataWithBaseURL("https://unpkg.com", html, "text/html", "UTF-8", null);
        tvMapCoords.setText(String.format(Locale.ENGLISH, "Koordinat: %.6f, %.6f", lat, lon));
    }

    private void appendLog(String label, String command, String phoneNumber, String status) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date());

        String logEntry = String.format("[%s] %s\n%s (Tujuan/Pengirim: %s | %s)\n----------------------------------------\n",
                time, label, command, phoneNumber, status);

        CharSequence currentText = tvCommandLog.getText();
        if (currentText.toString().equals(getString(R.string.log_empty))) {
            tvCommandLog.setText(logEntry);
        } else {
            tvCommandLog.setText(logEntry + currentText);
        }
    }

    /**
     * Demo Simulasi Balasan SMS Tracker untuk pengujian langsung
     */
    private void simulateIncomingSmsDemo() {
        String simSender = preferences.getString(KEY_PHONE_NUMBER, "085117007880");
        if (TextUtils.isEmpty(simSender)) simSender = "085117007880";
        final String sender = simSender;

        new AlertDialog.Builder(this)
                .setTitle("Simulasi Balasan SMS Tracker (" + sender + ")")
                .setItems(new String[]{
                        "1. Balasan Lokasi Maps (URL#)",
                        "2. Balasan Koordinat & Speed (WHERE#)",
                        "3. 🚨 Alarm Melebihi Batas Kecepatan",
                        "4. 🚨 Alarm Putus Daya Aki (Cut Power)",
                        "5. 🪫 Alarm Baterai Cadangan Lemah (Low Battery)",
                        "6. 📳 Alarm Getaran / Guncangan (Vibration Alarm)",
                        "7. Balasan Alarm Getaran Sukses (SENALM:ON)",
                        "8. Balasan Parameter Sistem (PARAM#)",
                        "9. Balasan Status Operasional (STATUS#)",
                        "10. Balasan Konfigurasi GPRS (GPRSSET#)",
                        "11. Balasan Versi Firmware (VERSION#)",
                        "12. Balasan Matikan Mesin Sukses",
                        "13. Balasan Menyalakan Mesin Sukses",
                        "14. Uji Timeout Peringatan Tanpa Balasan"
                }, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            handleIncomingTrackerSms(sender, "<Date Time: 26-10-27 16:30:15> http://maps.google.com/maps?q=N-6.208800,E106.845600");
                            break;
                        case 1:
                            handleIncomingTrackerSms(sender, "Lat:S06.20880,Lon:E106.84560,Course:180,Speed:65.5Km/h,DateTime:26-10-27 16:30:15");
                            break;
                        case 2:
                            handleIncomingTrackerSms(sender, "Over speed alarm! Speed:95.5Km/h, Lat:S06.20880,Lon:E106.84560,DateTime:26-10-27 16:35:00");
                            break;
                        case 3:
                            handleIncomingTrackerSms(sender, "Cut power alarm! External power supply disconnected! Lat:S06.20880,Lon:E106.84560,DateTime:26-10-27 16:36:00");
                            break;
                        case 4:
                            handleIncomingTrackerSms(sender, "Low battery alarm! Battery power below 3.6V, please charge! DateTime:26-10-27 16:37:00");
                            break;
                        case 5:
                            handleIncomingTrackerSms(sender, "Sensor alarm! Vehicle vibration detected! Lat:S06.20880,Lon:E106.84560,DateTime:26-10-27 16:38:00");
                            break;
                        case 6:
                            handleIncomingTrackerSms(sender, "SENALM:ON; Set vibration sensor alarm success!");
                            break;
                        case 7:
                            handleIncomingTrackerSms(sender, "IMEI: 353419088231940; Timer: 10,60; SENDS:5; SOS: 081234567890, 081987654321, 085512345678; Center Number: 081234567890; Sensorset:10,1,5,180; Defense time: 10; TimeZone:E,7,0;");
                            break;
                        case 8:
                            handleIncomingTrackerSms(sender, "Battery: 100%; GPRS: Link UP; GSM Signal Level: 28; ACC: ON; GPS: Fix; Defense: Armed;");
                            break;
                        case 9:
                            handleIncomingTrackerSms(sender, "GPRS:ON; Currently use APN:internet; Server:1,103.20.10.5,5023,0; URL:http://maps.google.com/maps?q=N-6.208800,E106.845600;");
                            break;
                        case 10:
                            handleIncomingTrackerSms(sender, "[VERSION]NT36_11_A1D_D23_R0_V17_c_WM");
                            break;
                        case 11:
                            handleIncomingTrackerSms(sender, "Cut off the fuel supply: Success! Speed:0 Km/h");
                            break;
                        case 12:
                            handleIncomingTrackerSms(sender, "Restore fuel supply:Success!");
                            break;
                        case 13:
                            showNoResponseWarningDialog();
                            break;
                    }
                })
                .setNegativeButton("Tutup", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin Kirim SMS diberikan", Toast.LENGTH_SHORT).show();
                if (pendingCommand != null) {
                    executeCommand(pendingCommand, "Perintah");
                    pendingCommand = null;
                }
            } else {
                Toast.makeText(this, "Izin SMS ditolak, menggunakan aplikasi SMS...", Toast.LENGTH_LONG).show();
                if (pendingCommand != null) {
                    String phoneNumber = getTargetPhoneNumber();
                    if (phoneNumber != null) openSystemSmsApp(phoneNumber, pendingCommand, "Perintah");
                    pendingCommand = null;
                }
            }
        } else if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, pendingContactRequestCode);
            } else {
                Toast.makeText(this, "Izin kontak diperlukan untuk memilih nomor dari kontak HP", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
