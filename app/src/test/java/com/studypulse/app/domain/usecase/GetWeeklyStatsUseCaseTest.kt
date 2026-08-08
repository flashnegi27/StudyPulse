package com.studypulse.app.domain.usecase

import com.studypulse.app.domain.model.StudySession
import com.studypulse.app.domain.repository.StudyRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class GetWeeklyStatsUseCaseTest {

    private val repository = mockk<StudyRepository>()
    private val useCase    = GetWeeklyStatsUseCase(repository)

    private fun makeSession(durationSeconds: Long, date: String) = StudySession(
        id = 1L, locationId = "CLASS_A",
        startTime = 0L, endTime = 0L,
        durationSeconds = durationSeconds, date = date
    )

    @Test
    fun `empty repository returns zero stats`() = runTest {
        every { repository.getSessionsForDateRange(any(), any()) } returns flowOf(emptyList())

        val stats = useCase().first()

        assertEquals(0L, stats.totalSeconds)
        assertEquals(0f, stats.progressFraction)
        assertEquals(7, stats.dailyStats.size)
    }

    @Test
    fun `progress fraction clamps at 1 when over 10 hours`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        every { repository.getSessionsForDateRange(any(), any()) } returns
            flowOf(listOf(makeSession(40_000L, today)))

        val stats = useCase().first()

        assertEquals(1.0f, stats.progressFraction)
    }

    @Test
    fun `sessions are bucketed into correct day of week`() = runTest {
        val monday = LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        every { repository.getSessionsForDateRange(any(), any()) } returns
            flowOf(listOf(makeSession(3_600L, monday)))

        val stats = useCase().first()

        val mondayStat = stats.dailyStats.first { it.dayOfWeek == DayOfWeek.MONDAY }
        assertEquals(3_600L, mondayStat.totalSeconds)
    }

    @Test
    fun `total seconds sums all sessions in range`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        every { repository.getSessionsForDateRange(any(), any()) } returns flowOf(
            listOf(
                makeSession(1_800L, today),
                makeSession(2_700L, today)
            )
        )

        val stats = useCase().first()
        assertEquals(4_500L, stats.totalSeconds)
    }
}
