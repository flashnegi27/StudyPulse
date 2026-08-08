package com.studypulse.app.di

import android.content.Context
import androidx.room.Room
import com.studypulse.app.data.local.StudyDatabase
import com.studypulse.app.data.local.dao.StudySessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): StudyDatabase =
        Room.databaseBuilder(ctx, StudyDatabase::class.java, "study_pulse.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDao(db: StudyDatabase): StudySessionDao = db.studySessionDao()
}
