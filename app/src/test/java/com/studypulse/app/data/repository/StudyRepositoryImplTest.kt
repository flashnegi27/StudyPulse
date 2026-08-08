package com.studypulse.app.data.repository

import com.studypulse.app.data.local.dao.StudySessionDao
import com.studypulse.app.data.local.entity.StudySessionEntity
import com.studypulse.app.domain.model.StudySession
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StudyRepositoryImplTest {

    private val dao        = mockk<StudySessionDao>(relaxed = true)
    private val repository = StudyRepositoryImpl(dao)

    private fun makeEntity(id: Long) = StudySessionEntity(
        id = id, locationId = "CLASS_A",
        startTime = 1000L, endTime = 5000L,
        durationSeconds = 4L, date = "2024-01-15"
    )

    private fun makeSession(id: Long) = StudySession(
        id = id, locationId = "CLASS_A",
        startTime = 1000L, endTime = 5000L,
        durationSeconds = 4L, date = "2024-01-15"
    )

    @Test
    fun `insertSession delegates to dao with correct entity`() = runTest {
        repository.insertSession(makeSession(0L))
        coVerify {
            dao.insertSession(match { entity ->
                entity.locationId == "CLASS_A" && entity.durationSeconds == 4L
            })
        }
    }

    @Test
    fun `getAllSessions maps entities to domain models`() = runTest {
        every { dao.getAllSessions() } returns flowOf(listOf(makeEntity(1L), makeEntity(2L)))

        val sessions = repository.getAllSessions().first()

        assertEquals(2, sessions.size)
        assertEquals(1L, sessions[0].id)
        assertEquals("CLASS_A", sessions[0].locationId)
    }

    @Test
    fun `deleteSession delegates to dao`() = runTest {
        repository.deleteSession(42L)
        coVerify { dao.deleteSessionById(42L) }
    }

    @Test
    fun `getSessionsForDateRange passes dates to dao`() = runTest {
        every { dao.getSessionsForDateRange("2024-01-13", "2024-01-19") } returns
            flowOf(listOf(makeEntity(1L)))

        val sessions = repository.getSessionsForDateRange("2024-01-13", "2024-01-19").first()

        assertEquals(1, sessions.size)
    }
}
