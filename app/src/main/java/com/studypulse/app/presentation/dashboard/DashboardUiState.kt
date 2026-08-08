package com.studypulse.app.presentation.dashboard

import com.studypulse.app.domain.model.SessionState
import com.studypulse.app.domain.model.StudySession
import com.studypulse.app.domain.model.WeeklyStats

data class DashboardUiState(
    val sessionState:   SessionState       = SessionState.Idle,
    val elapsedSeconds: Long               = 0L,
    val weeklyStats:    WeeklyStats        = WeeklyStats.empty(),
    val recentSessions: List<StudySession> = emptyList()
)
