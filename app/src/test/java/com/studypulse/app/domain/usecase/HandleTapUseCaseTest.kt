package com.studypulse.app.domain.usecase

import com.studypulse.app.data.datastore.SessionStateDataStore
import com.studypulse.app.domain.model.SessionState
import com.studypulse.app.domain.model.StudySession
import com.studypulse.app.domain.repository.StudyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HandleTapUseCaseTest {

    private val dataStore  = mockk<SessionStateDataStore>(relaxed = true)
    private val repository = mockk<StudyRepository>(relaxed = true)
    private val useCase    = HandleTapUseCase(dataStore, repository)

    @Test
    fun `idle state transitions to studying and returns SessionStarted`() = runTest {
        coEvery { dataStore.getSessionState() } returns flowOf(SessionState.Idle)

        val result = useCase("CLASS_A")

        assertTrue(result is TapResult.SessionStarted)
        assertEquals("CLASS_A", (result as TapResult.SessionStarted).locationId)
        coVerify { dataStore.saveStudyingState("CLASS_A", any()) }
    }

    @Test
    fun `studying state stops session and returns SessionStopped`() = runTest {
        val startTime = System.currentTimeMillis() - 60_000L
        coEvery { dataStore.getSessionState() } returns
            flowOf(SessionState.Studying("CLASS_A", startTime))
        coEvery { repository.insertSession(any()) } returns Unit

        val result = useCase("CLASS_A")

        assertTrue(result is TapResult.SessionStopped)
        val stopped = result as TapResult.SessionStopped
        assertEquals("CLASS_A", stopped.locationId)
        assertTrue(stopped.durationSeconds >= 60L)
        coVerify { repository.insertSession(any<StudySession>()) }
        coVerify { dataStore.clearState() }
    }

    @Test
    fun `session stopped inserts record with correct date format`() = runTest {
        val startTime = System.currentTimeMillis() - 5_000L
        coEvery { dataStore.getSessionState() } returns
            flowOf(SessionState.Studying("LAB_B", startTime))

        var capturedSession: StudySession? = null
        coEvery { repository.insertSession(capture(mutableListOf<StudySession>().also {
            // use slot approach instead
        })) } answers { capturedSession = firstArg() }

        useCase("LAB_B")

        coVerify {
            repository.insertSession(match { session ->
                session.date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                session.durationSeconds >= 5L
            })
        }
    }
}
