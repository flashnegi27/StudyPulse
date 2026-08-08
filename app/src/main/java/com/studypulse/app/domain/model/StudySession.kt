package com.studypulse.app.domain.model

data class StudySession(
    val id: Long,
    val locationId: String,
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Long,
    val date: String
) {
    fun formattedDuration(): String {
        val hours   = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        return when {
            hours > 0   -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else        -> "${seconds}s"
        }
    }
}
