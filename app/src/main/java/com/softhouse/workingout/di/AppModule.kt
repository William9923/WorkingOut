package com.softhouse.workingout.di

import android.content.Context
import androidx.room.Room
import com.softhouse.workingout.data.db.AppDatabase
import com.softhouse.workingout.shared.Constants.APP_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        APP_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunningDao(db: AppDatabase) = db.getRunningDao()

    @Singleton
    @Provides
    fun provideCyclingDao(db: AppDatabase) = db.getCyclingDao()

}