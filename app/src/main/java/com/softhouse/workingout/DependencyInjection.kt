package com.meliksahcakir.spotialarm

import android.content.Context
import androidx.room.Room
import com.meliksahcakir.spotialarm.albums.AlbumsViewModel
import com.meliksahcakir.spotialarm.artists.ArtistsViewModel
import com.meliksahcakir.spotialarm.edit.EditViewModel
import com.meliksahcakir.spotialarm.genres.GenresViewModel
import com.meliksahcakir.spotialarm.main.MainViewModel
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.options.OptionsViewModel
import com.meliksahcakir.spotialarm.playlists.PlaylistsViewModel
import com.meliksahcakir.spotialarm.repository.AlarmDao
import com.meliksahcakir.spotialarm.repository.AlarmRepository
import com.meliksahcakir.spotialarm.repository.MusicDao
import com.meliksahcakir.spotialarm.repository.MusicRepository
import com.meliksahcakir.spotialarm.repository.SpotiAlarmDatabase
import com.meliksahcakir.spotialarm.tracks.TracksViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val mainViewModelModule = module {
    viewModel {
        MainViewModel(get(), androidApplication())
    }
}

val optionsViewModelModule = module {
    viewModel {
        OptionsViewModel(get(), androidApplication())
    }
}

val tracksViewModelModule = module {
    viewModel {
        TracksViewModel(get(), androidApplication())
    }
}

val playlistsViewModelModule = module {
    viewModel {
        PlaylistsViewModel(get(), androidApplication())
    }
}

val albumsViewModelModule = module {
    viewModel {
        AlbumsViewModel(get(), androidApplication())
    }
}

val artistsViewModelModule = module {
    viewModel {
        ArtistsViewModel(get(), androidApplication())
    }
}

val genresViewModelModule = module {
    viewModel {
        GenresViewModel(get(), androidApplication())
    }
}

val editViewModelModule = module {
    viewModel {
        EditViewModel(get(), get(), androidApplication())
    }
}

val alarmRepositoryModule = module {
    fun provideAlarmDao(db: SpotiAlarmDatabase): AlarmDao {
        return db.alarmDao()
    }
    single { AlarmRepository(provideAlarmDao(get())) }
}

val musicRepositoryModule = module {
    fun provideMusicDao(db: SpotiAlarmDatabase): MusicDao {
        return db.musicDao()
    }

    single { MusicRepository(get(), provideMusicDao(get())) }
}

val apiModule = module {
    fun provideApi(retrofit: Retrofit): NapsterService {
        return retrofit.create(NapsterService::class.java)
    }
    single { provideApi(get()) }
}

val retrofitModule = module {

    fun provideHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder().addInterceptor(logger).build()
    }

    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NapsterService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { provideHttpClient() }
    single { provideRetrofit(get()) }
}

val databaseModule = module {
    fun provideDatabase(context: Context): SpotiAlarmDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SpotiAlarmDatabase::class.java,
            "Alarms.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { provideDatabase(androidContext()) }
}

val modules = listOf(
    mainViewModelModule,
    optionsViewModelModule,
    tracksViewModelModule,
    playlistsViewModelModule,
    albumsViewModelModule,
    artistsViewModelModule,
    genresViewModelModule,
    editViewModelModule,
    alarmRepositoryModule,
    musicRepositoryModule,
    apiModule,
    retrofitModule,
    databaseModule
)
