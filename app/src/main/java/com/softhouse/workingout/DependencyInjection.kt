package com.softhouse.workingout

import android.content.Context
import androidx.room.Room
import com.softhouse.workingout.alarm.edit.EditViewModel
import com.softhouse.workingout.data.db.AlarmDao
import com.softhouse.workingout.data.db.AppDatabase
import com.softhouse.workingout.data.repository.AlarmRepository
import com.softhouse.workingout.di.AppModule
import com.softhouse.workingout.ui.schedule.SchedulerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val alarmViewModelModule = module {
    viewModel {
        SchedulerViewModel(get(), androidApplication())
    }
}


val editViewModelModule = module {
    viewModel {
        EditViewModel(get(), androidApplication())
    }
}

val alarmRepositoryModule = module {
    fun provideAlarmDao(db: AppDatabase): AlarmDao {
        return db.getAlarmDao()
    }
    single { AlarmRepository(provideAlarmDao(get())) }
}
val databaseModule = module {
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "workout_database.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { provideDatabase(androidContext()) }
}

val modules = listOf(
    alarmViewModelModule,
    editViewModelModule,
    alarmRepositoryModule,
    databaseModule,
)
