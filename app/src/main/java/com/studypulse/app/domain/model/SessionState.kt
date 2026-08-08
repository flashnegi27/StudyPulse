package com.studypulse.app.domain.model

sealed class SessionState {
    object Idle : SessionState()
    data class Studying(
        val locationId: String,
        val startTime: Long
    ) : SessionState()
}
