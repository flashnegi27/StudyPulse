package com.studypulse.app.domain.model

import java.time.DayOfWeek

data class WeeklyStats(
    val totalSeconds: Long,
    val dailyStats: List<DailyStats>,
    val goalSeconds: Long = GOAL_SECONDS
) {
    val progressFraction: Float
        get() = (totalSeconds / goalSeconds.toFloat()).coerceIn(0f, 1f)

    val totalHours: Float
        get() = totalSeconds / 3600f

    companion object {
        const val GOAL_SECONDS = 36_000L  // 10 hours

        fun empty(): WeeklyStats = WeeklyStats(
            totalSeconds = 0L,
            dailyStats   = DayOfWeek.entries.map { DailyStats(it, 0L) }
        )
    }
}
