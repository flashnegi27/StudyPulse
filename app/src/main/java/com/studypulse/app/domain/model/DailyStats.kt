package com.studypulse.app.domain.model

import java.time.DayOfWeek

data class DailyStats(
    val dayOfWeek: DayOfWeek,
    val totalSeconds: Long
) {
    val totalHours: Float get() = totalSeconds / 3600f
}
