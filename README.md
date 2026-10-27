# GPS Control (GT06 GPS Tracker Controller)

Aplikasi Android native untuk kontrol mesin kendaraan jarak jauh, live tracking Google Maps, pemantauan parameter diagnostik, dan alarm keamanan perangkat GPS Tracker GT06 berbasis SMS.

## 🚀 Fitur Utama

- **Kontrol Mesin Jarak Jauh**: Tombol kontrol terpadu untuk mematikan (`RELAY,1#`) dan menyalakan mesin (`RELAY,0#`) dengan validasi konfirmasi dialog serta widget home screen (3x1).
- **Pelacakan Kendaraan Real-Time**:
  - Tombol **Lokasi Maps** (`URL#`) dengan timestamp update tanggal dan jam (`dd/MM/yyyy HH:mm:ss`).
  - Tombol **Koordinat** (`WHERE#`) dengan indikator kecepatan kendaraan (`⚡ Speed`) dan timestamp update.
  - Peta interaktif Leaflet in-app + tombol Buka Google Maps eksternal.
- **Pengaturan Lengkap**:
  - Nomor Master (`CENTER,A,NomorHP#` / `CENTER,D#`).
  - Nomor SOS Darurat (`SOS,A,Nomor1,Nomor2,Nomor3#` / `SOS,D,NomorHP#`).
  - Pembatasan Kecepatan (`SPEED,ON,Detik,Kecepatan,Mode#` / `SPEED,OFF#`).
  - Alarm Getaran Sensor Parkir (`SENALM,ON#` / `SENALM,OFF#`).
  - Sinkronisasi otomatis dari balasan `PARAM#`.
  - Integrasi kontak buku telepon Android (`ContactsContract`).
- **Keamanan Akses**: Proteksi menu Pengaturan menggunakan Sidik Jari (Biometrik) atau Kunci Layar (Pola / PIN) native sistem HP Android.
- **Sistem Notifikasi Darurat**: Notifikasi status bar prioritas tinggi (`NotificationChannel`) dan in-app alert dialog untuk bahaya Melebihi Batas Kecepatan (Overspeed), Putus Daya Aki (Cut Power), Baterai Cadangan Lemah (Low Battery), dan Getaran Mobil.

## 🛠️ Build & Install

Aplikasi ini dikompilasi secara native tanpa gradle berat menggunakan toolchain Android SDK (`aapt2`, `d8`, `uapksigner`):

```bash
bash build.sh
adb install -r GPS-Control-V1.0.2.apk
```

## 📄 Dokumentasi

- Riwayat perubahan lengkap: [CHANGELOG.md](CHANGELOG.md)
- Referensi perintah SMS GT06: [GT06_SMS_COMMANDS.md](GT06_SMS_COMMANDS.md)
