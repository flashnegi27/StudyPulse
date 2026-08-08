package com.studypulse.app.di

import com.studypulse.app.data.repository.StudyRepositoryImpl
import com.studypulse.app.domain.repository.StudyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStudyRepository(impl: StudyRepositoryImpl): StudyRepository
}
