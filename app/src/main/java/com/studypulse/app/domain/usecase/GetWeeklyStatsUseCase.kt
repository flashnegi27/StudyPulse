package com.studypulse.app.domain.usecase

import com.studypulse.app.domain.model.DailyStats
import com.studypulse.app.domain.model.WeeklyStats
import com.studypulse.app.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class GetWeeklyStatsUseCase @Inject constructor(
    private val repository: StudyRepository
) {
    operator fun invoke(): Flow<WeeklyStats> {
        val today   = LocalDate.now()
        val monday  = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday  = monday.plusDays(6)
        val fmt     = DateTimeFormatter.ISO_LOCAL_DATE

        return repository.getSessionsForDateRange(
            monday.format(fmt),
            sunday.format(fmt)
        ).map { sessions ->
            val dayMap = DayOfWeek.entries.associateWith { 0L }.toMutableMap()
            var totalSeconds = 0L

            sessions.forEach { session ->
                val sessionDay = LocalDate.parse(session.date, fmt).dayOfWeek
                dayMap[sessionDay] = (dayMap[sessionDay] ?: 0L) + session.durationSeconds
                totalSeconds += session.durationSeconds
            }

            val dailyStats = DayOfWeek.entries.map { day ->
                DailyStats(day, dayMap[day] ?: 0L)
            }

            WeeklyStats(
                totalSeconds = totalSeconds,
                dailyStats   = dailyStats
            )
        }
    }
}
