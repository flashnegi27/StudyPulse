package com.studypulse.app.domain.repository

import com.studypulse.app.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    suspend fun insertSession(session: StudySession)
    fun getAllSessions(): Flow<List<StudySession>>
    fun getSessionsForDateRange(startDate: String, endDate: String): Flow<List<StudySession>>
    suspend fun deleteSession(id: Long)
}
