package com.studypulse.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.studypulse.app.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("""
        SELECT * FROM study_sessions
        WHERE date >= :startDate AND date <= :endDate
        ORDER BY startTime ASC
    """)
    fun getSessionsForDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<StudySessionEntity>>

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)
}
