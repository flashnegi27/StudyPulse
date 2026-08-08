package com.studypulse.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.data.datastore.SessionStateDataStore
import com.studypulse.app.domain.model.SessionState
import com.studypulse.app.domain.model.WeeklyStats
import com.studypulse.app.domain.usecase.GetSessionHistoryUseCase
import com.studypulse.app.domain.usecase.GetWeeklyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dataStore:          SessionStateDataStore,
    private val getWeeklyStats:     GetWeeklyStatsUseCase,
    private val getSessionHistory:  GetSessionHistoryUseCase
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = dataStore.getSessionState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionState.Idle)

    val elapsedSeconds: StateFlow<Long> = sessionState
        .flatMapLatest { state ->
            when (state) {
                is SessionState.Idle     -> flowOf(0L)
                is SessionState.Studying -> flow {
                    while (true) {
                        emit((System.currentTimeMillis() - state.startTime) / 1_000L)
                        delay(1_000L)
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val weeklyStats: StateFlow<WeeklyStats> = getWeeklyStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeeklyStats.empty())

    val recentSessions = getSessionHistory()
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
