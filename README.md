# IF3210 2021 K03 Android Project

## Deskripsi
Dalam tugas besar kali ini, kami membangun sebuah aplikasi pada platform Android untuk menunjang kegiatan workout. Fitur-fitur yang ada di dalam aplikasi kami adalah Sports News, Training Tracker, Training History, dan Training Scheduler.

Pada fitur Sports News, kami menggunakan RecyclerView untuk menampilkan list berita. Fitur Sports News juga akan menampilkan halaman yang responsive, apabila pada orientasi portrait akan menampilkan 1 kolom list berita, sedangkan pada orientasi landscape akan menampilkan 2 kolom list berita. Kami juga menyimpan history dan schedule ke dalam SQLite. Penggunaan Fragment digunakan untuk fitur Training History, dimana halaman log list dan halaman log detail dapat ditampilkan pada halaman berbeda ketika orientasi portrait dan halaman yang sama ketika orientasi landscape.

## Fungsionalitas Aplikasi & Screenshot
Berikut penjelasan dari beberapa fungsionalitas dalam aplikasi : 
| No | Spesifikasi | Penjelasan |
|----|-------------|------------|
| 1  | Sports News | Pengguna dapat melihat berita olahraga yang diperoleh melalui Indonesia Sports News API. Terdapat halaman untuk menampilkan list berita dan setiap item dapat dibuka untuk menampilkan WebView dengan konten sesuai URL berita. |
| 2  | Training Tracker | Pengguna dapat melakukan “mulai” dan “selesai” mengenai pelacakan latihan yang memiliki dua jenis, yaitu Cycling dan Running. Pada mode Cycling, aplikasi akan melacak jarak tempuh dan rute yang dilalui oleh sepeda menggunakan GPS. Hasil pelacakan dapat dilihat berupa angka (km jarak tempuh) dan peta (garis rute sepeda). Pada mode Running, aplikasi akan melacak jumlah step yang dilakukan oleh pengguna. Sistem pelacakan berjalan pada background sehingga masih dapat berjalan meskipun aplikasi ditutup jika statusnya sedang aktif. Selain itu, pada halaman ini juga ditampilkan kompas yang selalu menunjuk ke arah utara, yang diimplementasikan dengan memanfaatkan sensor.|
| 3  | Training History | Terdapat halaman history yang menyimpan seluruh kegiatan latihan pengguna yang dilacak, ditampilkan dalam bentuk kalender. Setiap hari pada kalender dapat dibuka untuk menampilkan list log latihan, dengan setiap log dapat dibuka untuk menampilkan informasi detail seperti pada spesifikasi Training Tracker|
| 4  | Training Scheduler | Pengguna dapat menentukan jadwal latihan yang dapat dikustomisasi untuk berjalan secara suatu waktu spesifik, rutin per hari, ataupun rutin per hari tertentu. Selain itu, setiap jadwal latihan dapat dikaitkan pada jenis latihan tertentu (cycling, walking) dengan target sesuai masukan pengguna (misalnya 5 km atau 10000 langkah). Saat waktu mulai jadwal, aplikasi akan menampilkan notifikasi sesuai tipe dan target latihan. Pengguna juga dapat mengatur agar pelacakan dapat berjalan secara otomatis saat waktu mulai jadwal latihan. Saat waktu selesai jadwal, aplikasi akan menampilkan notifikasi. Jika opsi pelacakan otomatis dinyalakan, pada waktu selesai jadwal juga ditampilkan pencapaian pengguna yang disimpan pada history, lalu pelacakan dimatikan.|

## Screenshot Aplikasi
Berikut beberapa screenshot aplikasi saat dijalankan :

### Sports News
![Sports News Portrait](docs/sports_news_1.jpg)
![Sports News Landscape](docs/sports_news_2.jpg)

### News WebView
![Sports News Webview](docs/sports_news_webview.jpg)

### Training Tracker
![Running Tracker](docs/tracker_running1.jpg)
![Running Tracker](docs/tracker_running_2.jpg)
![Cycling Tracker](docs/tracker_cycling_1.jpg)
![Cycling Tracker](docs/tracker_cycling_2.jpg)

### Log Calendar
![Running Calendar](docs/running_calendar.jpg)
![Cycling Calendar](docs/cycling_calendar.jpg)

### Running Logs
![Running Logs Portrait](docs/running_history_1.jpg)
![Running Logs Details Portrait](docs/running_details.jpg)
![Running Logs Landscape](docs/running_history_2.jpg)

### Cycling Logs
![Cycling Logs Portrait](docs/cycling_history_1.jpg)
![Cycling Logs Details Portrait](docs/cycling_details.jpg)
![Cycling Logs Landscape](docs/cycling_history_2.jpg)


### Scheduler


## Library

Berikut penggunaan library pada aplikasi :
- Android Activity context (single activity - many fragment architecture): Digunakan untuk
- Material Design: Digunakan untuk
- Navigation: Digunakan untuk
- Recycler view: Digunakan untuk
- Retrofit: Digunakan untuk
- Glide: Digunakan untuk
- Easy Permission: Digunakan untuk
- Google Maps Location Services: Digunakan untuk
- Room: Digunakan untuk
- Kotlin Extensions and Coroutines support for Room: Digunakan untuk
- Dagger: Digunakan untuk
- Dagger Android: Digunakan untuk
- Dagger - Hilt: Digunakan untuk
- Material Calender: Digunakan untuk
- Two Pane Layout: Digunakan untuk
- Utilities: Digunakan untuk

## Pembagian Kerja

* 13518003 - Dimas Lucky Mahendra

| No | Kontribusi |
|----|------------|
| 1  | - | 


* 13518006 - Ahadi Ihsan Rasyidin

| No | Kontribusi |
|----|------------|
| 1  | - |


* 13518138 - William

| No | Kontribusi |
|----|------------|
| 1  | - |



