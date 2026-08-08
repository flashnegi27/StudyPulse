package com.studypulse.app.presentation.history

import com.studypulse.app.domain.model.StudySession

data class HistoryUiState(
    val sessions:  List<StudySession> = emptyList(),
    val isLoading: Boolean            = false
)
