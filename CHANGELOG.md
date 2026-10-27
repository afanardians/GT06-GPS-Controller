# 📋 LOG PERUBAHAN PROYEK (CHANGELOG)
## GPS Control (GT06 GPS Tracker Controller for Android)

Dokumen ini mencatat seluruh riwayat perubahan, penambahan fitur, perbaikan antarmuka, dan arsitektur teknis aplikasi Android **GPS Control** dari awal pembuatan hingga versi terkini.

---

### [v1.0.2 - Pembaruan Pelacakan, Alarm Getaran & Notifikasi Darurat] (27 Oktober 2026)
#### 📍 Pembaruan Halaman Beranda (Pelacakan Kendaraan)
1. **Tombol "Lokasi Maps" & Timestamp Lengkap**:
   - Mengubah label tombol dari `Cek lokasi google maps` menjadi **`Lokasi Maps`** (`btnCheckGoogleMaps`).
   - Menambahkan teks keterangan waktu update terakhir (`tvMapsLastUpdate`) dengan format lengkap tanggal dan jam (`dd/MM/yyyy HH:mm:ss`), tersimpan permanen di SharedPreferences.
2. **Tombol "Koordinat" (Hijau), Speed Kendaraan & Timestamp**:
   - Mengubah label tombol dari `Cek koordinat lengkap` menjadi **`Koordinat`** (`btnCheckCoords`).
   - Mengubah tampilan tombol menjadi **warna hijau** elegan dengan sudut melengkung 10dp (`@drawable/btn_coords_green_bg.xml` - `#16A34A` / `#15803D`).
   - Menambahkan tampilan **Speed kendaraan** real-time (`tvCoordsSpeed`, misal: `⚡ Speed: 65.5 Km/h`) yang otomatis diekstraksi dari balasan perintah `WHERE#`.
   - Menambahkan keterangan waktu update terakhir (`tvCoordsLastUpdate`) dengan format tanggal dan jam (`dd/MM/yyyy HH:mm:ss`).
   - Menampilkan indikator kecepatan kendaraan (`tvMapSpeed`) langsung pada bilah info di bawah peta interaktif Leaflet.

#### 📳 Pengaturan Alarm Getaran (SENALM) di Menu Pengaturan
1. **Toggle Switch Alarm Getaran**:
   - Menambahkan kartu konfigurasi baru `📳 ALARM GETARAN` di halaman Pengaturan dengan sakelar interaktif `switchVibrationAlarm`.
   - **Fungsi ON**: Memunculkan konfirmasi keamanan lalu mengirim perintah SMS **`SENALM,ON#`** ke kartu SIM tracker untuk mengaktifkan sensor getaran saat mobil parkir (ACC OFF).
   - **Fungsi OFF**: Memunculkan konfirmasi keamanan lalu mengirim perintah SMS **`SENALM,OFF#`** ke tracker untuk menonaktifkan sensor getaran.
   - Status badge dinamis (`STATUS: ALARM AKTIF (SENALM,ON)` warna hijau / `STATUS: ALARM NONAKTIF` warna abu-abu).
   - Otomatis sinkronisasi status bila menerima balasan konfirmasi `SENALM:ON` atau `SENALM:OFF` dari tracker.

#### 🚨 Sistem Notifikasi Darurat (Status Bar & In-App Alert)
1. **Notifikasi Native Sistem Android (`GpsNotificationHelper`)**:
   - Saluran notifikasi berprioritas tinggi (`gt06_alarm_alerts`, `IMPORTANCE_HIGH`) dengan getaran darurat berulang dan suara alarm.
   - Tetap menerima dan memunculkan notifikasi status bar meskipun aplikasi sedang berada di latar belakang (background) atau ditutup melalui `GpsSmsReceiver`.
   - Menambahkan izin runtime `android.permission.POST_NOTIFICATIONS` untuk Android 13+ (API 33).
2. **Pemicu Notifikasi Darurat**:
   - **Melebihi Batas Kecepatan (Over-speed Alarm)**: Deteksi otomatis pola `over speed`, `overspeed`, `speeding`.
   - **Putus Daya Aki (Cut Power Alarm)**: Deteksi otomatis pola `cut power`, `power cut`, `external power cut`.
   - **Baterai Cadangan GPS Lemah (Low Battery Alarm)**: Deteksi otomatis daya baterai internal cadangan `< 3.7V`.
   - **Alarm Getaran (Sensor/Vibration Alarm)**: Deteksi otomatis guncangan / sensor getaran saat parkir.
3. **Peringatan Dalam Aplikasi (In-App Alert Dialog)**:
   - Menampilkan popup dialog darurat seketika balasan diterima saat aplikasi terbuka, lengkap dengan pesan detail, opsi `Salin SMS` ke clipboard, dan pencatatan status darurat pada log Beranda.
4. **Demo Simulasi Balasan Diperluas**:
   - Menambahkan opsi simulasi langsung pada menu demo untuk: Balasan Lokasi Maps (URL#), Balasan Koordinat & Speed (WHERE#), Alarm Melebihi Kecepatan, Alarm Putus Daya Aki, Alarm Baterai Lemah, dan Alarm Getaran.

---

### [v1.0.2 - Pembersihan Label & Keamanan Sistem HP] (27 Oktober 2026)
#### 🎨 Pembersihan Label & Penomoran Antarmuka (UI/UX)
1. **Pembersihan Judul Kategori di Halaman Beranda (Home)**:
   - Menghapus nomor urut dan menyederhanakan teks judul kategori:
     - `1. Kontrol Mesin` / `1. KONTROL MESIN` → **`KONTROL MESIN`**
     - `2. Pelacakan & Google Maps` / `2. PELACAKAN & GOOGLE MAPS` → **`PELACAKAN KENDARAAN`**
     - `7. Pemeliharaan Sistem` / `7. PEMELIHARAAN SISTEM` → **`PEMELIHARAAN SISTEM`**
2. **Penyelarasan Tab & Judul Kartu Diagnostik di Halaman Pengaturan**:
   - Tab navigasi carousel: `📋 Parameter` diubah menjadi **`📋 Sistem`** (`tabStackParam`).
   - Teks indikator carousel: `1 / 4 • Sistem`.
   - Judul kartu carousel 1: `3. Konfigurasi Parameter` → **`KONFIGURASI SISTEM`**.
   - Judul kartu carousel 2: `4. Status Operasional` → **`STATUS OPERASIONAL`**.
   - Judul kartu carousel 3: `5. Status & Konfigurasi GPRS` → **`STATUS GPRS`**.
   - Judul kartu carousel 4: `6. Versi Firmware Perangkat` → **`FIRMWARE`**.

#### 🔐 Keamanan Terpadu Sistem HP (Biometric & System Keyguard)
1. **Otentikasi Kunci Layar Bawaan HP**:
   - Mengganti sistem pola in-app kustom dengan integrasi keamanan native sistem HP langsung.
   - Pengguna dapat membuka kunci Pengaturan menggunakan **Sidik Jari (Fingerprint)** atau **Pola/PIN/Password Layar Kunci** yang terdaftar di ponsel Android.
   - Menggunakan `android.hardware.biometrics.BiometricPrompt` dengan authenticator `BIOMETRIC_WEAK | DEVICE_CREDENTIAL` (API 30+ / Q) dan fallback `KeyguardManager.createConfirmDeviceCredentialIntent()` (API 26-28).
   - Kartu pengaturan kunci disederhanakan menjadi satu toggle sakelar yang bersih dan elegan tanpa konfigurasi pola manual tambahan.

---

### [v1.0.2 - Pembaruan Keamanan & Tata Letak] (27 Oktober 2026)
#### 🚀 Fitur Baru & Keamanan
1. **Kunci Akses Menu Pengaturan (Pola & Sidik Jari)**:
   - Menambahkan pengamanan proteksi sebelum pengguna dapat membuka menu Pengaturan.
   - Pilihan metode otentikasi ganda:
     - **Sidik Jari / Biometrik Perangkat**: Menggunakan `android.hardware.biometrics.BiometricPrompt` (API 28+) dan `KeyguardManager` sebagai fallback native (PIN/Pola layar perangkat).
     - **Kunci Pola 3x3 In-App**: Dibuat komponen native `PatternLockView.java` (grid 9 titik dengan garis real-time, getaran haptic feedback, indikator visual error merah / sukses hijau, dan validasi minimal 4 titik dengan dialog konfirmasi 2 langkah).
   - Menambahkan kartu konfigurasi `🔐 KUNCI AKSES PENGATURAN` di halaman Pengaturan untuk mengaktifkan/menonaktifkan kunci dan mengubah pola/metode otentikasi.
   - Mengalihkan navigasi tab Pengaturan di bottom bar dan fallback validasi nomor SIM kosong ke sistem verifikasi kunci (`attemptOpenSettings()`).
2. **Ikon Aplikasi Baru**:
   - Memperbarui ikon peluncur (launcher icon) aplikasi di semua tingkat kepadatan layar Android (`mipmap-mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`, dan adaptif vector `anydpi-v26` foreground + round icon) serta drawable `app_logo.png` menggunakan aset visual terbaru beresolusi 512x512 piksel.

#### 🔄 Penataan Ulang Antarmuka (UI/UX)
1. **Pemindahan Diagnostik & Informasi ke Menu Pengaturan**:
   - Memindahkan carousel swipeable 4 kartu Diagnostik & Informasi (Konfigurasi Parameter `PARAM#`, Status Operasional `STATUS#`, Status & Konfigurasi GPRS `GPRSSET#`, dan Versi Firmware `VERSION#`) dari Halaman Beranda (`layoutHomeContainer`) ke Halaman Pengaturan (`layoutSettingsContainer`).
   - Halaman Beranda kini menjadi jauh lebih ringkas, fokus pada **Kontrol Mesin Utama**, **Peta Lokasi Google Maps Real-Time**, dan **Riwayat Log Perintah**.
   - Menambahkan penyesuaian lebar kartu otomatis (`hsvDiagnosticStack.post()`) agar kartu diagnostik langsung menyesuaikan lebar layar secara presisi saat halaman Pengaturan dibuka.

---

### [v1.0.2 - Integrasi Hardware PARAM# & Filter SMS] (27 Oktober 2026)
#### 🚀 Fitur Baru & Optimasi
1. **Tampilan Nomor Terdaftar dari PARAM# di Pengaturan**:
   - Menambahkan card informatif pada pengaturan Nomor Master dan Nomor SOS yang menampilkan nomor yang saat ini tersimpan di memori perangkat tracker berdasarkan hasil pembacaan balasan SMS perintah `PARAM#`.
   - Menambahkan tombol aksi cepat `📋 Salin ke Form Input` untuk otomatis memindahkan nomor Master maupun nomor SOS 1, 2, 3 ke form input teks dan menyimpannya secara instan.
   - Menyediakan tombol pemicu manual `📡 Sinkronkan PARAM# dari Tracker` langsung di halaman Pengaturan.
2. **Filter Ketat Respon SMS Tracker di Beranda**:
   - Menyaring seluruh SMS masuk agar hanya balasan dari nomor SIM Card GPS Tracker yang aktif yang diproses dan ditampilkan di log Beranda.
   - SMS masuk dari nomor lain secara otomatis diabaikan sehingga menjaga privasi dan kebersihan log riwayat.

---

### [v1.0.2 - Pengaturan Lengkap Master, SOS & Kecepatan] (27 Oktober 2026)
#### 🚀 Fitur Baru
1. **Pengaturan Nomor Master (Center Number)**:
   - Form input nomor Master dengan tombol pilih dari Kontak HP (`ContactsContract`).
   - Tombol kirim `CENTER,A,NomorHP#` untuk mendaftarkan nomor master pengendali relay mesin.
   - Tombol hapus `CENTER,D#` untuk menghapus hak nomor master.
2. **Pengaturan Nomor SOS Darurat (Slot 1, 2, 3)**:
   - Form pendaftaran 3 nomor SOS dengan tombol pilih kontak HP mandiri per slot dan label nama kontak.
   - Tombol daftarkan semua nomor SOS sekaligus dengan format `SOS,A,Nomor1,Nomor2,Nomor3#`.
   - Tombol hapus individual per nomor menggunakan format `SOS,D,NomorHP#`.
3. **Pengaturan Pembatasan Kecepatan (Speed Limit Alarm)**:
   - Toggle switch interaktif (aktif/nonaktif).
   - Pengaturan batas kecepatan (km/jam), durasi toleransi overspeed (detik), dan pilihan mode alarm:
     - Mode 0: Hanya via Aplikasi / Server
     - Mode 1: SMS + Aplikasi / Server
     - Mode 2: Telepon + SMS + Aplikasi / Server
   - Perintah aktivasi: `SPEED,ON,Detik,Kecepatan,Mode#` dan deaktivasi `SPEED,OFF#`.
4. **Redesain Tombol Simpan & Pembersihan Header**:
   - Tombol simpan pengaturan dibuat lebih elegan dengan background gradient cyan-to-emerald (`btn_save_settings_bg.xml`).
   - Menghapus tombol gear dan link ubah di header atas, navigasi dialihkan murni ke Bottom Navigation Bar.

---

### [v1.0.2 - Navigasi Bottom Bar & Pemilihan Kontak] (27 Oktober 2026)
#### 🚀 Fitur Baru
1. **Sistem Navigasi Dua Halaman (Bottom Bar)**:
   - Menambahkan navigasi bawah dengan tab `🏠 Beranda` dan `⚙️ Pengaturan`.
   - Pergantian tampilan instan antar container layout tanpa delay fragment.
   - Pengaturan diubah dari model dialog popup menjadi halaman mandiri full-page yang luas.
2. **Integrasi Pemilihan Kontak HP (`ContactsContract`)**:
   - Menambahkan permission `android.permission.READ_CONTACTS`.
   - Pengisian nomor SIM Card GPS kini dapat langsung memilih nomor dari kontak buku telepon HP dengan sanitasi karakter otomatis (+62 / 08xx).

---

### [v1.0.2 - Penyempurnaan Tampilan Diagnostik & Widget] (27 Oktober 2026)
#### 🎨 UI/UX & Parsing Firmware
1. **Redesain Textbox Nomor SIM**:
   - Tampilan kolom input dibuat jelas editable dengan background putih bersih, border outline tegas, dan pill badge `✏️ DAPAT DIEDIT`.
2. **Parsing Firmware Format Concox GT06**:
   - Dukungan parsing format balasan `[VERSION]NT36_11_A1D_D23_R0_V17_c_WM`: memecah otomatis model `NT36`, versi firmware `V17_c_WM`, revisi hardware `A1D_D23`, dan tanggal rilis `R0`.
3. **Penyempurnaan Widget Home Screen**:
   - Ukuran widget dioptimalkan menjadi 3x1 sel dengan ukuran tombol dan font lebih proporsional.
   - Pembalikan logika warna tombol:
     - Status Normal / Mesin Hidup: Tombol warna Merah (siap mematikan mesin).
     - Status Terputus / Mesin Mati: Tombol warna Hijau (siap menyalakan mesin).
4. **Pemisahan Judul Diagnostik**:
   - Judul `📊 DIAGNOSTIK & INFORMASI` dipisahkan pada baris mandiri agar tidak berhimpitan dengan bar tombol navigasi carousel.

---

### [v1.0.2 - Stack Swipeable 4 Kartu Diagnostik] (27 Oktober 2026)
#### 🔄 Arsitektur & Carousel
1. **Penggabungan 4 Kategori Diagnostik ke Horizontal Carousel**:
   - Menggabungkan Kategori 3 (Parameter), Kategori 4 (Status Operasional), Kategori 5 (GPRS), dan Kategori 6 (Versi Firmware) ke dalam satu kontainer geser swipeable (`HorizontalScrollView`) dengan tab pemilih dan indikator halaman (1/4 s/d 4/4).
2. **Rebranding Nama Aplikasi**:
   - Mengubah nama aplikasi menjadi **GPS Control** dengan output file build `GPS-Control-V1.0.2.apk`.

---

### [v1.0.1 - Widget Kontrol Mesin & Diagnostik GPRS/Firmware] (26 Oktober 2026)
#### 🚀 Fitur Baru
1. **App Widget Layar Utama (Home Screen Widget)**:
   - Membuat `EngineControlWidget.java` dan layout `widget_engine_control.xml` untuk mematikan/menyalakan mesin langsung dari homescreen tanpa membuka aplikasi.
   - Sinkronisasi status relay real-time antara aplikasi dan widget.
2. **Kartu Cek GPRS & Versi Firmware**:
   - Menambahkan perintah `GPRSSET#` (Cek konfigurasi APN, IP server, port, dan protocol).
   - Menambahkan perintah `VERSION#` (Cek versi firmware tracker).

---

### [v1.0.0 - Rebranding Rush Control] (26 Oktober 2026)
1. Perubahan nama aplikasi pertama kali menjadi **Rush Control** (file `Rush-Control-v1.0.0.apk`) dengan penyesuaian ikon aplikasi.

---

### [v0.9.0 - Inisialisasi & Fitur Inti Kontrol GT06] (26 Oktober 2026)
#### 🛠️ Fondasi Aplikasi
1. **Fungsi Kontrol Inti**:
   - **Matikan Mesin**: Perintah SMS `RELAY,1#` dengan dialog validasi konfirmasi bahaya.
   - **Nyalakan / Normalkan Mesin**: Perintah SMS `RELAY,0#`.
   - **Cek Lokasi Google Maps**: Perintah `URL#` dan `WHERE#` dengan visualisasi peta interaktif langsung di aplikasi (Leaflet OpenStreetMap via WebView) serta tombol buka di Google Maps eksternal.
   - **Cek Konfigurasi Parameter**: Perintah `PARAM#`.
   - **Cek Status Operasional**: Perintah `STATUS#`.
   - **Restart Perangkat**: Perintah `RESET#` dengan dialog konfirmasi validasi.
2. **Sistem Watchdog Timer**:
   - Deteksi otomatis jika tidak ada balasan SMS dari tracker dalam waktu toleransi, memunculkan notifikasi peringatan: *"Tidak Ada Jawaban Dari Perangkat. Cek kembali Pulsa Anda"*.
3. **Penerima SMS Otomatis (`GpsSmsReceiver`)**:
   - Broadcast receiver untuk menangkap balasan SMS dari modul GPS dan memperbarui indikator status secara otomatis di aplikasi.
