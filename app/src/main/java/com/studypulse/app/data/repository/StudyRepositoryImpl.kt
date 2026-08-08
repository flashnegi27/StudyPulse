package com.studypulse.app.data.repository

import com.studypulse.app.data.local.dao.StudySessionDao
import com.studypulse.app.data.mapper.toDomain
import com.studypulse.app.data.mapper.toEntity
import com.studypulse.app.domain.model.StudySession
import com.studypulse.app.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val dao: StudySessionDao
) : StudyRepository {

    override suspend fun insertSession(session: StudySession) {
        dao.insertSession(session.toEntity())
    }

    override fun getAllSessions(): Flow<List<StudySession>> =
        dao.getAllSessions().map { entities -> entities.map { it.toDomain() } }

    override fun getSessionsForDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<StudySession>> =
        dao.getSessionsForDateRange(startDate, endDate)
           .map { entities -> entities.map { it.toDomain() } }

    override suspend fun deleteSession(id: Long) {
        dao.deleteSessionById(id)
    }
}
