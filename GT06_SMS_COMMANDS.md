# Dokumentasi Lengkap Perintah SMS GPS Tracker GT06 / GT06N

Dokumentasi ini merangkum perintah-perintah SMS standar untuk perangkat GPS Tracker seri GT06 / GT06N (Concox / Jimi IoT dan varian klon kompatibel).

> **Aturan Penulisan Perintah:**
> 1. Perintah tidak membedakan huruf besar/kecil (*case-insensitive*), namun disarankan menggunakan HURUF KAPITAL.
> 2. Semua tanda baca koma (`,`) dan pagar (`#`) harus menggunakan karakter ASCII standar (bukan tanda baca full-width).
> 3. Jangan gunakan spasi sebelum atau sesudah tanda koma.
> 4. Sebagian besar perintah diakhiri dengan tanda pagar (`#`).
> 5. Perintah kritis (seperti potong relay / matikan mesin) hanya bisa dieksekusi oleh nomor Center/Admin atau SOS.

---

## 1. Konfigurasi Nomor Otoritas (SOS & Center/Admin)

Pengaturan nomor yang berhak mengontrol dan menerima peringatan dari GPS.

| Perintah | Fungsi | Contoh & Penjelasan |
| :--- | :--- | :--- |
| `SOS,A,Nomor1,Nomor2,Nomor3#` | Mendaftarkan hingga 3 nomor darurat/SOS | `SOS,A,08123456789,08129876543#`<br>Jika hanya 1 nomor: `SOS,A,08123456789#`<br>Jika nomor kedua saja: `SOS,A,,08129876543#` |
| `SOS,D,Index1,Index2#` | Menghapus nomor SOS berdasarkan urutan index | `SOS,D,1#` (Hapus nomor ke-1)<br>`SOS,D,1,2#` (Hapus nomor ke-1 & 2) |
| `SOS,D,NomorHP#` | Menghapus nomor SOS langsung dengan nomor HP | `SOS,D,08123456789#` |
| `CENTER,A,NomorHP#` | Menetapkan **Nomor Center/Master** (Wajib untuk kontrol mesin/Relay) | `CENTER,A,08123456789#`<br>*Catatan: Hanya nomor SOS yang bisa mendaftarkan nomor Center.* |
| `CENTER,D#` | Menghapus nomor Center | `CENTER,D#` |

---

## 2. Kontrol Mesin (Start / Stop Engine - Relay Bahan Bakar & Kelistrikan)

Fitur utama untuk mematikan dan menormalkan kembali kelistrikan/pompa bahan bakar kendaraan.

| Perintah | Fungsi | Catatan Keamanan Perangkat |
| :--- | :--- | :--- |
| `RELAY,1#` | **Matikan Mesin** (Putus relay bensin / kelistrikan) | Hanya dieksekusi jika:<br>1. Dikirim oleh Nomor Center terdaftar.<br>2. Sinyal GPS fix/valid.<br>3. Kecepatan kendaraan **< 20 km/jam** atau dalam posisi diam (safety lock).<br>*Balasan:* `Cut off the fuel supply: Success! Speed:0 Km/h` |
| `RELAY,0#` | **Normalkan Mesin** (Sambung kembali relay) | Mengembalikan jalur relay sehingga mesin bisa dihidupkan kembali.<br>*Balasan:* `Restore fuel supply:Success!` |

> **Varian Firmware Kloning/Lama:**
> Beberapa modul GT06 non-Concox menggunakan sintaks dengan default password:
> - `DYD,123456#` atau `DY,123456#` (Duan You Dian - Matikan Mesin)
> - `HFYD,123456#` atau `HF,123456#` (Hui Fu You Dian - Normalkan Mesin)

---

## 3. Pelacakan & Pengecekan Lokasi (Tracking)

| Perintah | Fungsi | Contoh Balasan |
| :--- | :--- | :--- |
| `WHERE#` | Meminta koordinat GPS saat ini | `Lat:N22.571285,Lon:E113.877115,Course:42.20,Speed:0.0740,DateTime:26-10-26 14:30:12` |
| `URL#` | Meminta link langsung Google Maps | `<Date Time: 26-10-26 14:30:12> http://maps.google.com/maps?q=N22.571490,E113.877103` |
| `123#` atau `POSITION#` | Meminta alamat teks lokasi fisik (jika didukung LBS platform) | Menampilkan perkiraan nama jalan/area. |

---

## 4. Konfigurasi Jaringan GPRS, APN & Server

Untuk menghubungkan GPS ke server platform tracking (seperti Traccar, GPSWOX, Cootrack, dll).

| Perintah | Fungsi | Format & Contoh |
| :--- | :--- | :--- |
| `APN,NamaAPN#` | Mengatur APN kartu SIM (tanpa user/pass) | Contoh Telkomsel: `APN,internet#`<br>Contoh Indosat: `APN,indosatgprs#`<br>Contoh XL: `APN,xlunlimited#` |
| `APN,NamaAPN,User,Pass#` | Mengatur APN dengan user dan password | `APN,internet,wap,wap123#` |
| `SERVER,1,Domain,Port,0#` | Menghubungkan ke server via Domain/URL (TCP) | `SERVER,1,gps.domainanda.com,5023,0#` (Port Traccar GT06 umumnya 5023) |
| `SERVER,0,IP,Port,0#` | Menghubungkan ke server via alamat IP (TCP) | `SERVER,0,103.20.10.5,5023,0#` |
| `GPRSON,1#` | Mengaktifkan koneksi data GPRS | `GPRSON,1#` (Default ON) |
| `GPRSON,0#` | Mematikan koneksi data GPRS | `GPRSON,0#` |
| `TIMER,T1,T2#` | Interval kirim data ke server (detik) | `TIMER,10,60#`<br>*(T1 = interval saat ACC ON, T2 = interval saat ACC OFF. Rentang: 10-18000s)* |
| `GMT,E/W,Zona,Menit#` | Mengatur zona waktu perangkat | `GMT,E,7,0#` (Untuk WIB / UTC+7)<br>`GMT,E,8,0#` (Untuk WITA / UTC+8) |

---

## 5. Alarm & Keamanan Kendaraan

| Perintah | Fungsi | Keterangan |
| :--- | :--- | :--- |
| `SENALM,ON#` / `SENALM,OFF#` | Mengaktifkan / mematikan alarm getaran (*Vibration Alarm*) | Jika aktif, saat kontak mati (ACC OFF) dan mobil terguncang, GPS akan kirim SMS & telepon nomor SOS. |
| `DEFENSE,Menit#` | Waktu tunda sebelum alarm aktif setelah ACC OFF | `DEFENSE,10#` (Rentang 1-60 menit, default 10 menit). |
| `SENSOR,Waktu,Frekuensi,Tunda#` | Parameter sensitivitas sensor getar | `SENSOR,10,1,5,180#` (Mendeteksi 5 getaran dalam 10 detik, tunda alarm 180 detik). |
| `SPEED,ON,Detik,Kecepatan,Mode#` | Alarm batas kecepatan (*Over-speed Alarm*) | `SPEED,ON,20,100,1#`<br>*(Jika rata-rata > 100 km/jam selama 20 detik, kirim alarm via SMS & GPRS).* |
| Alarm Otomatis Lainnya: | - **Cut Power Alarm:** Notifikasi SMS saat kabel aki diputus pencuri.<br>- **Low Battery Alarm:** Notifikasi jika baterai internal < 3.7V.<br>- **SOS Panic Button:** Tekan tombol fisik SOS 3 detik untuk telepon darurat. |

---

## 6. Fitur Penyadapan Suara (Voice Monitoring)

Jika perangkat dilengkapi microphone eksternal:
1. Pastikan nomor penelepon sudah terdaftar di daftar nomor SOS (`SOS,A,...`).
2. Lakukan panggilan telepon langsung ke nomor kartu SIM di GPS.
3. Setelah berdering sekitar 10 detik, GPS akan otomatis mengangkat panggilan (*auto-answer*) tanpa mengeluarkan suara/nada di speaker kendaraan.

---

## 7. Pengecekan Status & Pemeliharaan Sistem

| Perintah | Fungsi | Keterangan Balasan |
| :--- | :--- | :--- |
| `STATUS#` | Cek status operasional perangkat | Menampilkan status baterai, status sinyal GSM/GPRS, status GPS fix, dan status ACC ignition. |
| `PARAM#` | Cek semua konfigurasi parameter | Menampilkan nomor IMEI, interval Timer, daftar nomor SOS, nomor Center, zona waktu, dll. |
| `GPRSSET#` | Cek status & konfigurasi GPRS | Menampilkan APN aktif, alamat IP/Domain Server, dan port. |
| `VERSION#` | Cek versi firmware perangkat | Menampilkan versi hardware & software tracker. |
| `RESET#` | Restart/Reboot perangkat secara lembut | Perangkat akan reboot dalam 1 menit tanpa menghapus konfigurasi. |
| `FACTORY#` | Reset ke pengaturan awal pabrik (*Factory Reset*) | Menghapus semua nomor SOS, nomor center, dan pengaturan kustom. |
