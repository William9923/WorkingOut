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
<img src="docs/sports_news_1.jpg" alt="Sports News Portrait" width="200"/>
<img src="docs/sports_news_2.jpg" alt="Sports News Landscape" width="400"/>

### News WebView
<img src="docs/sports_news_webview.jpg" alt="Sports News Webview" width="200"/>

### Training Tracker
<img src="docs/tracker_running1.jpg" alt="Running Tracker" width="200"/>
<img src="docs/tracker_running_2.jpg" alt="Running Tracker" width="200"/>
<img src="docs/tracker_cycling_1.jpg" alt="Cycling Tracker" width="200"/>
<img src="docs/tracker_cycling_2.jpg" alt="Cycling Tracker" width="200"/>

### Notification While App is Running
<img src="docs/notification_1.jpg" alt="Notification Running App" width="200"/>

### Notification After App is Closed While Tracking
<img src="docs/notification_2.jpg" alt="Notification App Closed" width="200"/>

### Log Calendar
<img src="docs/running_calendar.jpg" alt="Running Calendar" width="200"/>
<img src="docs/cycling_calendar.jpg" alt="Cycling Calendar" width="200"/>

### Running Logs
<img src="docs/running_history_1.jpg" alt="Running Logs Portrait" width="200"/>
<img src="docs/running_details.jpg" alt="Running Logs Details Portrait" width="200"/>
<img src="docs/running_history_2.jpg" alt="Running Logs Landscape" width="400"/>

### Cycling Logs
<img src="docs/cycling_history_1.jpg" alt="Cycling Logs Portrait" width="200"/>
<img src="docs/cycling_details.jpg" alt="Cycling Logs Details Portrait" width="200"/>
<img src="docs/cycling_history_2.jpg" alt="Cycling Logs Landscape" width="400"/>


### Scheduler


## Library

Berikut penggunaan library pada aplikasi :
| No | Library | Fungsi |
|----|-------------|------------|
| 1  | Android Activity context (single activity - many fragment architecture) | Digunakan untuk mengakses API yang ada pada Activity |
| 2  | Material Design | Digunakan untuk mengimplementasikan Material Design pada aplikasi dengan API |
| 3  | Navigation | Digunakan untuk navigasi antara Fragments, Activities, atau komponen lainnya |
| 4  | Recycler view | Digunakan untuk menampilkan data yang terhitung besar namun meminimalkan penggunaan memori |
| 5  | Retrofit | Digunakan untuk membuat HTTP Request seperti pada fitur Sports News |
| 6  | Glide | Digunakan untuk membuat load gambar menjadi lebih efisien |
| 7  | Easy Permission | Digunakan untuk mengatur permission dari aplikasi |
| 8  | Google Maps Location Services | Digunakan untuk mengakses maps dari Google Maps seperti pada fitur Cycling Details |
| 9  | Room | Digunakan untuk pengaksesan database SQLite yang lebih baik |
| 10  | Kotlin Extensions and Coroutines support for Room | Digunakan untuk pengaksesan database SQLite yang lebih baik |
| 11  | Dagger | Digunakan untuk |
| 12  | Dagger Android | Digunakan untuk |
| 13  | Dagger - Hilt | Digunakan untuk |
| 14  | Material Calendar | Digunakan untuk |
| 15  | Two Pane Layout | Digunakan untuk pengimplementasian sliding pane untuk layout |
| 16  | Utilities | Digunakan untuk |


## Pembagian Kerja

* 13518003 - Dimas Lucky Mahendra

| No | Kontribusi |
|----|------------|
| 1  | Sports News UI Design | 
| 2  | Running Logs UI Design | 
| 3  | Cycling Logs UI Design | 
| 4  | Scheduler UI Design |
| 5  | Tester | 


* 13518006 - Ahadi Ihsan Rasyidin

| No | Kontribusi |
|----|------------|
| 1  | Single Alarm Without Date |
| 2  | Repeating Alarm |
| 3  | Layout Scheduler |
| 4  | Auto Tracker |
| 5  | Tester |


* 13518138 - William

| No | Kontribusi |
|----|------------|
| 1  | - |



