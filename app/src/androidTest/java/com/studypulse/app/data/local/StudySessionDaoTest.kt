package com.studypulse.app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studypulse.app.data.local.dao.StudySessionDao
import com.studypulse.app.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StudySessionDaoTest {

    private lateinit var database: StudyDatabase
    private lateinit var dao: StudySessionDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StudyDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.studySessionDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveSession() = runTest {
        val entity = StudySessionEntity(
            locationId = "CLASS_A", startTime = 1000L, endTime = 5000L,
            durationSeconds = 4L, date = "2024-01-15"
        )
        dao.insertSession(entity)

        val sessions = dao.getAllSessions().first()
        assertEquals(1, sessions.size)
        assertEquals("CLASS_A", sessions[0].locationId)
    }

    @Test
    fun dateRangeFilterReturnsOnlyMatchingSessions() = runTest {
        dao.insertSession(StudySessionEntity(
            locationId = "A", startTime = 0L, endTime = 0L,
            durationSeconds = 10L, date = "2024-01-10"
        ))
        dao.insertSession(StudySessionEntity(
            locationId = "B", startTime = 0L, endTime = 0L,
            durationSeconds = 20L, date = "2024-01-15"
        ))
        dao.insertSession(StudySessionEntity(
            locationId = "C", startTime = 0L, endTime = 0L,
            durationSeconds = 30L, date = "2024-01-20"
        ))

        val sessions = dao.getSessionsForDateRange("2024-01-13", "2024-01-19").first()
        assertEquals(1, sessions.size)
        assertEquals("B", sessions[0].locationId)
    }

    @Test
    fun deleteSessionRemovesItFromDb() = runTest {
        val id = dao.insertSession(StudySessionEntity(
            locationId = "A", startTime = 0L, endTime = 0L,
            durationSeconds = 5L, date = "2024-01-15"
        ))
        dao.deleteSessionById(id)

        val sessions = dao.getAllSessions().first()
        assertTrue(sessions.isEmpty())
    }

    @Test
    fun getAllSessionsOrderedByStartTimeDesc() = runTest {
        dao.insertSession(StudySessionEntity(locationId = "A", startTime = 100L, endTime = 200L, durationSeconds = 1L, date = "2024-01-15"))
        dao.insertSession(StudySessionEntity(locationId = "B", startTime = 300L, endTime = 400L, durationSeconds = 1L, date = "2024-01-15"))

        val sessions = dao.getAllSessions().first()
        assertEquals("B", sessions[0].locationId)
        assertEquals("A", sessions[1].locationId)
    }
}
